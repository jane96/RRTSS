package lab.mars.MCRRTImp.algorithm

import lab.mars.MCRRTImp.base.*
import lab.mars.MCRRTImp.base.Vector
import lab.mars.MCRRTImp.model.*
import org.apache.commons.math3.distribution.NormalDistribution
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

typealias PathSampler<V> = (SimulatedVehicle<V>, OneTimeConfiguration) -> MCRRT.Result<V>

@Suppress("NOTHING_TO_INLINE")
class MCRRT<V : Vector<V>>(
        private val spaceRestriction: Space<V>,
        pathSampler: PathSampler<V>? = null,
        private val firstLevelDeltaTime: Double,
        private val secondLevelDeltaTime: Double = 0.0,
        private val obstacleProvider: () -> List<Obstacle<V>>,
        private val vehicleProvider: () -> SimulatedVehicle<V>,
        private val targetProvider: () -> WayPoint<V>,
        var verbose: Boolean = false
) {


    private lateinit var vehicle: SimulatedVehicle<V>

    private lateinit var obstacles: List<Obstacle<V>>

    private lateinit var target: WayPoint<V>

    private inline fun beforeAlgorithm() {
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
        InProgress,
        TimedOut,
        Impossible
    }


    class Result<V : Vector<V>>(val status: ResultStatus, val levelOnePath: Path<WayPoint<V>>? = null, val levelTwoPaths: Queue<Path<WayPoint<V>>>? = null)


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

    private var actualPathCache = LinkedBlockingQueue<Path<WayPoint<V>>>()

    private fun List<WayPoint<V>>.toPath() : Path<WayPoint<V>> {
        val currentPath = Path<WayPoint<V>>()
        this.forEach { currentPath.add(it) }
        return currentPath
    }

    private inline fun verbose(message: String) {
        if (verbose) {
            println(message)
        }
    }

    private fun algorithm(oneTimeConfiguration: OneTimeConfiguration): Result<V> {
        return this.pathSampler(vehicle, oneTimeConfiguration)
    }

    private fun <E> LinkedBlockingQueue<E>.cpy() : LinkedBlockingQueue<E> {
        val ret = LinkedBlockingQueue<E>()
        this.forEach { ret.offer(it) }
        return ret
    }

    private fun defaultSampler(vehicle: SimulatedVehicle<V>, configuration: OneTimeConfiguration): Result<V> {
        startTime = System.currentTimeMillis()
        if (configuration.levelOneReplan) {
            areaPathCache = firstLevelRRT(this.vehicle.position, this.vehicle.velocity, firstLevelDeltaTime, configuration.timeTolerance)
            if (areaPathCache == null) {
                verbose("algorithm failed reason : first level timed out")
                return Result(ResultStatus.TimedOut)
            } else if (areaPathCache!!.size == 0) {
                verbose("algorithm failed reason : target impossible to reach" )
                return Result(ResultStatus.Impossible)
            }
        }
        if (configuration.levelTwoReplan) {
            this.vehicle = vehicleProvider()
            val newPaths = secondLevelRRT(
                    areaPath = areaPathCache!!,
                    idxFrom = configuration.levelTwoFromIdx,
                    idxTo = configuration.levelTwoToIdx,
                    fromPosition = this.vehicle.position,
                    fromVelocity = this.vehicle.velocity,
                    approachDistance = configuration.wayPointApproachDistance,
                    deltaTime = secondLevelDeltaTime,
                    timeTolerance = configuration.timeTolerance)
            if (newPaths == null) {
                verbose("algorithm failed reason : second level timed out")
                return Result(ResultStatus.TimedOut)
            }
            actualPathCache = newPaths
        }
        return Result(ResultStatus.Complete, levelOnePath =  areaPathCache!!.cpy(), levelTwoPaths =  actualPathCache.cpy())
    }


    private fun firstLevelRRT(plannerStart: V, plannerVelocity: V, deltaTime: Double, timeTolerance: Long): Path<WayPoint<V>>? {
        while (true) {
            val transforms = vehicle.simulateKinetic(plannerVelocity, 1.0)
            val scaleBase = transforms.map { it.position.len() }.sortedBy { it }.asReversed()[0] * deltaTime
            transforms.forEach { transform -> transform.position.translate(plannerStart) }
            val gridWorld = DiscreteWorld(spaceRestriction, scaleBase)
            val gridScanStartTime = System.currentTimeMillis()
            verbose("1st level : start grid world scan")
            gridWorld.scan(obstacles)
            verbose("1st level : grid scan completed in " + (System.currentTimeMillis() - gridScanStartTime) + "ms")
            val pathRoot = NTreeNode(WayPoint(plannerStart, scaleBase, plannerVelocity))
            if (gridWorld.insideObstacle(target.origin)) {
                return Path()
            }
            var stepCount = 0
            verbose("1st level : starting area path generation")
            var targetNearestDistance = Double.MAX_VALUE
            while (true) {
                var sampled: WayPoint<V> = WayPoint(gridWorld.sample(), scaleBase, plannerVelocity)
                if (0.0 random 1.0 < 0.2) {
                    sampled = WayPoint(target.origin.cpy(), scaleBase, plannerVelocity)
                }
                val sampledNearestNode = pathRoot.nearestChildOf(sampled) { c1, s -> c1.element.origin.distance(s.origin) }
                val expandDirection = sampled.origin.cpy().translate(sampledNearestNode.element.origin.cpy().reverse())
                sampled.origin.set(gridWorld.formalize(sampledNearestNode.element.origin)).translate(expandDirection.normalize().scale(scaleBase))
                var sampledInvalid = false
                if (gridWorld.insideObstacle(sampled.origin)) {
                    sampledInvalid = true
                } else {
                    for (i in 1 until 10) {
                        val position = sampledNearestNode.element.origin.cpy().translate(expandDirection.cpy().scale(i.toDouble() / 10.0))
                        if (gridWorld.insideObstacle(position)) {
                            sampledInvalid = true
                            break
                        }
                    }
                }
                if (sampledInvalid) {
                    continue
                }
                sampled.origin.set(gridWorld.formalize(sampled.origin))
                sampledNearestNode.forEachChild {
                    if (it.element.origin.epsilonEquals(sampled.origin)) {
                        sampledInvalid = true
                    }
                }
                if (sampledInvalid) {
                    continue
                }
                sampledNearestNode += (sampled)
                val distance = sampled.origin.distance(target.origin)
                if (distance < targetNearestDistance) {
                    targetNearestDistance = distance
                }
                verbose("1st level : generate node on area path " + sampled.origin)
                val path = pathRoot.traceTo(sampled).toPath()
                path.utility = (0 - path.size - (path.end.origin.distance(target.origin.cpy())))
                pathApplier(Result(ResultStatus.InProgress, path))
                if (sampled.origin.distance(target.origin.cpy()) <= scaleBase) {
                    verbose("1st level : area path generation complete")
                    val trace = pathRoot.traceTo(sampled)
                    val cellPath = trace.toPath()
                    cellPath.add(WayPoint(target.origin, scaleBase, target.velocity))
                    cellPath.forEach {
                        cellPath.utility -= it.origin.distance(target.origin)
                    }
                    cellPath.finished = true
                    return cellPath
                }
                if (System.currentTimeMillis() - startTime > timeTolerance) {
                    return null
                }
                stepCount++
            }
        }

    }

    private fun secondLevelRRT(areaPath: Path<WayPoint<V>>, idxFrom : Int, idxTo: Int, fromPosition: V, fromVelocity: V, approachDistance: Double, deltaTime: Double, timeTolerance: Long): LinkedBlockingQueue<Path<WayPoint<V>>>? {
        var curPosition = fromPosition
        var curVelocity = fromVelocity
        val ret = LinkedBlockingQueue<Path<WayPoint<V>>>()
        val N01 = NormalDistribution(0.0, 1.0)
        val rotationLimitsOnOneSide = vehicle.rotationLimits / 2
        var deadEndCount = 0
        verbose("2nd level : starting actual path generation")
        var straightDirectionCount = 0
        for (i in idxFrom until idxTo + 1) {
            val area = areaPath[i]
            val currentPath = Path<WayPoint<V>>()
            while (curPosition.distance2(area.origin) > approachDistance * approachDistance) {
                val comparableMap = HashMap<Int, Double>()
                val transforms = vehicle.simulateKinetic(curVelocity, deltaTime)
                for (transform in transforms) {
                    transform.position.translate(curPosition)
                }
                var outOfBound = false
                val origin = area.origin.cpy()
                transforms.forEachIndexed { transformIdx, t ->
                    val target = origin.cpy().translate(curPosition.cpy().reverse()).normalize()
                    val next = t.velocity.cpy().normalize()
                    val angle = target.angle(next)
                    if (angle > rotationLimitsOnOneSide) {
                        outOfBound = true
                    }
                    comparableMap[transformIdx] = angle
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
                        if (Math.random() <= (1 - curPosition.distance2(area.origin) / area.origin.distance2(areaPath[idxTo + 1].origin))) {
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
                    currentPath.add(WayPoint(selected.position, selected.velocity.len(), selected.velocity))
                    curPosition = selected.position.cpy()
                    curVelocity = selected.velocity.cpy()
                    if (selectedIdx.toDouble() == Math.ceil(transforms.size / 2.0)) {
                        straightDirectionCount += 1
                        currentPath.utility -= straightDirectionCount
                    } else {
                        if (straightDirectionCount > 0) {
                            straightDirectionCount -= 1
                        }
                    }
                    if (currentPath.end.origin.distance(area.origin) <= approachDistance) {
                        currentPath.utility -= currentPath.end.origin.distance2(target.origin).toInt()
                        currentPath.utility -= currentPath.size
                        ret.offer(currentPath)
                        break
                    }
                } else {
                    deadEndCount++
                    verbose("2nd level : dead end back propagation with $deadEndCount path points ")
                    (0 until deadEndCount).forEach {
                        if (currentPath.size != 0) {
                            currentPath.remove(currentPath.end)
                        }
                    }
                    if (currentPath.size == 0) {
                        curPosition = vehicle.position.cpy()
                        curVelocity = vehicle.velocity.cpy()
                        deadEndCount = 0
                    } else {
                        val last = currentPath.end
                        curPosition = last.origin.cpy()
                        curVelocity = last.velocity.cpy()
                    }
                }
                if (System.currentTimeMillis() - startTime > timeTolerance) {
                    return null
                }
            }
        }
        verbose("2nd level : finished path generation")
        return ret
    }


}
