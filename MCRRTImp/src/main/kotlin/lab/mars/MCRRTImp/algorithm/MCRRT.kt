package lab.mars.MCRRTImp.algorithm

import lab.mars.MCRRTImp.base.*
import lab.mars.MCRRTImp.model.*
import org.apache.commons.math3.distribution.NormalDistribution

typealias PathSampler<V> = (SimulatedVehicle<V>, OneTimeConfiguration) -> MCRRT.Result<V>

class MCRRT<V : Vector<V>>(
        private val spaceRestriction: Space<V>,
        pathSampler: PathSampler<V>? = null,
        protected val obstacleProvider: () -> List<Obstacle<V>>,
        protected val vehicleProvider: () -> SimulatedVehicle<V>,
        protected val targetProvider: () -> WayPoint<V>,
        var verbose: Boolean = false
) {


    private lateinit var vehicle: SimulatedVehicle<V>

    private lateinit var obstacles: List<Obstacle<V>>

    private lateinit var target: WayPoint<V>


    private fun beforeAlgorithm() {
        this.obstacles = obstacleProvider()
        this.vehicle = vehicleProvider()
        this.target = targetProvider()
    }


    private lateinit var pathApplier : (Result<V>) -> Unit

    fun solve(oneTimeConfiguration: OneTimeConfiguration, pathApplier: (Result<V>) -> Unit) {
        beforeAlgorithm()
        this.pathApplier = pathApplier
        pathApplier(algorithm(oneTimeConfiguration))
    }


    enum class ResultStatus {
        Complete,
        TimedOut,
        Impossible
    }


    class Result<V : Vector<V>>(val status: ResultStatus, val levelOnePath: Path<WayPoint<V>>? = null, val levelTwoPath: Path<WayPoint<V>>? = null)


    private val pathSampler: PathSampler<V>

    init {
        if (pathSampler != null) {
            this.pathSampler = pathSampler
        } else {
            this.pathSampler = this::defaultSampler
        }
    }

    private var startTime: Long = 0

    private var areaPathCache: Path<WayPoint<V>>? = null

    private var actualPathCache: Path<WayPoint<V>>? = null

    private fun List<WayPoint<V>>.toPath() : Path<WayPoint<V>> {
        val ret = Path<WayPoint<V>>()
        this.forEach { ret.add(it) }
        return ret
    }

    private inline fun verbose(message: String) {
        if (verbose) {
            println(message)
        }
    }

    private fun algorithm(oneTimeConfiguration: OneTimeConfiguration): Result<V> {
        return this.pathSampler(vehicle, oneTimeConfiguration)
    }

    private fun defaultSampler(vehicle: SimulatedVehicle<V>, configuration: OneTimeConfiguration): Result<V> {
        startTime = System.currentTimeMillis()
        if (configuration.levelOneReplan || areaPathCache == null) {
            areaPathCache = firstLevelRRT(this.vehicle.position, this.vehicle.velocity, configuration.firstLevelDeltaTime, configuration.timeTolerance)
            if (areaPathCache == null) {
                verbose("algorithm failed reason : first level timed out")
                return Result(ResultStatus.TimedOut)
            } else if (areaPathCache!!.size == 0) {
                verbose("algorithm failed reason : target impossible to reach" )
                return Result(ResultStatus.Impossible)
            }
        }
        if (configuration.levelTwoReplan || actualPathCache == null) {
            this.vehicle = vehicleProvider()
            val newPath = secondLevelRRT(
                    areaPath = areaPathCache!!,
                    idx = configuration.levelTwoStartIdx,
                    startPosition = this.vehicle.position,
                    startVelocity = this.vehicle.velocity,
                    approachDistance = configuration.wayPointApproachDistance,
                    deltaTime = configuration.secondLevelDeltaTime,
                    timeTolerance = configuration.timeTolerance)
            if (newPath == null) {
                verbose("algorithm failed reason : second level timed out")
                return Result(ResultStatus.TimedOut)
            }
            actualPathCache = newPath
        }
        return Result(ResultStatus.Complete, areaPathCache, actualPathCache)
    }


    private fun firstLevelRRT(plannerStart: V, plannerVelocity: V, deltaTime: Double, timeTolerance: Long): Path<WayPoint<V>>? {
        val gridWorld: DiscreteWorld<V>
        var timeScalar = deltaTime
        val pathRoot: NTreeNode<WayPoint<V>>
        val gridCellEdgeLength: Double
        while (true) {
            val transforms = vehicle.simulateKinetic(plannerVelocity, 1.0)
            val scaleBase = transforms.map { it.position.len() }.sortedBy { it }.asReversed()[0] * deltaTime
            transforms.forEach { transform -> transform.position.translate(plannerStart) }
            gridWorld = DiscreteWorld(spaceRestriction, scaleBase)
            val gridScanStartTime = System.currentTimeMillis()
            verbose("1st level : start grid world scan")
            gridWorld.scan(obstacles)
            verbose("1st level : grid scan completed in " + (System.currentTimeMillis() - gridScanStartTime) + "ms")
            val gridAircraft = gridWorld.formalize(plannerStart)
            gridCellEdgeLength = gridWorld.cellSize.len()
            pathRoot = NTreeNode(WayPoint(gridAircraft, gridCellEdgeLength, plannerVelocity))
            if (gridWorld.insideObstacle(target.origin)) {
                return Path()
            }
            val targetCell = gridWorld.formalize(target.origin)
            var stepCount = 0
            var nearest: WayPoint<V>? = null
            var distance = java.lang.Double.MAX_VALUE
            verbose("1st level : starting area path generation")
            while (true) {
                var sampled: WayPoint<V> = WayPoint(spaceRestriction.sample(), gridCellEdgeLength, plannerVelocity)
                if (nearest != null /*&& 0.0.random(1.0) < 1 - nearest.origin.distance(target.origin) / plannerStart.distance(target.origin)*/) {
                    sampled = WayPoint(target.origin.cpy(), gridCellEdgeLength, plannerVelocity)
                }
                val nearestNode = pathRoot.nearestOf(sampled) { c1, s ->
                    val delta = s.origin.cpy().translate(c1.origin.cpy().reverse())
                    val transformList = vehicle.simulateKinetic(c1.velocity, deltaTime)

                    val sortedByAngle = transformList.map { it -> Pair(it, it.position.angle(delta)) }.filter { it.second <= vehicle.rotationLimits * deltaTime }
                    if (sortedByAngle.size != transformList.size) {
                        Double.MAX_VALUE
                    } else {
                        c1.origin.distance2(s.origin)
                    }
                }
                val delta = sampled.origin.cpy().translate(nearestNode.element.origin.cpy().reverse())
                val transformList = vehicle.simulateKinetic(nearestNode.element.velocity, deltaTime)
                val nextTransform = transformList.map { it -> Pair(it, it.position.angle(delta)) }.sortedBy { it.second }[0].first
                val stepped = nearestNode.element.origin.cpy().translate(nextTransform.position)
                if (gridWorld.insideObstacle(stepped)) {
                    stepCount++
                    if (stepCount > gridWorld.size) {
                        pathRoot.clear()
                        distance = Double.MAX_VALUE
                        nearest = null
                        stepCount = 0
                    }
                    continue
                }
                sampled.origin.set(stepped)
                sampled.velocity.set(nextTransform.velocity)
                nearestNode += (sampled)
                verbose("1st level : generate node on area path " + sampled.origin)
                val dis = target.origin.distance2(stepped)
                if (dis < distance) {
                    distance = dis
                    nearest = sampled
                    val trace = pathRoot.traceTo(nearest)
                    pathApplier(Result(ResultStatus.Complete, trace.toPath()))
                }
                if (gridWorld.formalize(sampled.origin).epsilonEquals(targetCell)) {
                    verbose("1st level : area path generation complete")
                    val path = pathRoot.traceTo(sampled)
                    val cellPath = Path<WayPoint<V>>()
                    path.forEach(cellPath::add)
                    cellPath.utility = 0 - cellPath.size
                    return cellPath
                }
                if (System.currentTimeMillis() - startTime > timeTolerance) {
                    return null
                }
                stepCount++
            }
        }

    }

    private fun secondLevelRRT(areaPath: Path<WayPoint<V>>, idx: Int, startPosition: V, startVelocity: V, approachDistance: Double, deltaTime: Double, timeTolerance: Long): Path<WayPoint<V>>? {
        var curPosition = startPosition
        var curVelocity = startVelocity
        val ret = Path<WayPoint<V>>()
        val N01 = NormalDistribution(0.0, 1.0)
        val rotationLimitsOnOneSide = vehicle.rotationLimits / 2
        var deadEndCount = 0
        verbose("2nd level : starting actual path generation")
        val area = areaPath[idx]
        var straightDirectionCount = 0
        while (curPosition.distance2(area.origin) > area.radius * area.radius) {
            val comparableMap = HashMap<Int, Double>()
            val transforms = vehicle.simulateKinetic(curVelocity, deltaTime)
            for (transform in transforms) {
                transform.position.translate(curPosition)
            }
            var outOfBound = false
            val origin = area.origin
            for (i in transforms.indices) {
                val t = transforms[i]
                val target = origin.cpy().translate(curPosition.cpy().reverse()).normalize()
                val next = t.velocity.cpy().normalize()
                val angle = target.angle(next)
                if (angle > rotationLimitsOnOneSide) {
                    outOfBound = true
                }
                comparableMap[i] = angle
            }
            if (!outOfBound) {
                comparableMap.entries.forEach { e -> e.setValue(1 - N01.cumulativeProbability(e.value / rotationLimitsOnOneSide * 2.58)) }
            } else {
                val minAngle = comparableMap.values.stream().min { obj, anotherDouble -> obj.compareTo(anotherDouble) }.get()
                comparableMap.entries.forEach { e -> e.setValue(1 - N01.cumulativeProbability((e.value - minAngle) / rotationLimitsOnOneSide * 2.58)) }
            }
            var valueSum = 0.0
            for (value in comparableMap.values) {
                valueSum += value
            }
            comparableMap.entries.forEach { e -> e.setValue(e.value / valueSum) }
            val probability = 0.0 random 1.0
            var sum = 0.0
            var safeTransform = true
            val selectedIdx =
                    if (Math.random() <= (1 - curPosition.distance2(area.origin) / area.origin.distance2(areaPath[idx + 1].origin))) {
                        comparableMap.entries.sortedBy { 1 - it.value }[0].key
                    } else {
                        comparableMap.entries.sortedBy { it.value }
                                .map {
                                    sum += it.value
                                    Pair(it.key, sum)
                                }.filter { it.second >= probability }[0].first
                    }
            val selected = transforms[selectedIdx]
            if (!spaceRestriction.contains(selected.position)) {
                safeTransform = false
            }
            for (obs in obstacles) {
                if (obs.contains(selected.position)) {
                    safeTransform = false
                }
            }
            if (safeTransform) {
                verbose("2nd level : expanding actual path point :" + selected.position)
                ret.add(WayPoint(selected.position, selected.velocity.len(), selected.velocity))
                curPosition = selected.position.cpy()
                curVelocity = selected.velocity.cpy()
                if (selectedIdx.toDouble() == Math.ceil(transforms.size / 2.0)) {
                    straightDirectionCount += 1
                    ret.utility -= straightDirectionCount
                } else {
                    if (straightDirectionCount > 0) {
                        straightDirectionCount -= 1
                    }
                }
                if (ret.end.origin.distance(area.origin) <= approachDistance) {
                    ret.utility -= ret.end.origin.distance2(target.origin).toInt()
                    ret.utility -= ret.size
                    verbose("2nd level : finished path generation")
                    return ret
                }
            } else {
                deadEndCount++
                verbose("2nd level : dead end back propagation with $deadEndCount path points ")
                for (i in 0 until deadEndCount) {
                    if (ret.size != 0) {
                        ret.remove(ret.end)
                    }
                }
                if (ret.size == 0) {
                    curPosition = vehicle.position.cpy()
                    curVelocity = vehicle.velocity.cpy()
                    deadEndCount = 0
                } else {
                    val last = ret.end
                    curPosition = last.origin.cpy()
                    curVelocity = last.velocity.cpy()
                }
            }
            if (System.currentTimeMillis() - startTime > timeTolerance) {
                return null
            }
        }

        return ret
    }


}
