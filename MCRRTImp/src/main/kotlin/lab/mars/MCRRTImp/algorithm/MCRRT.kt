package lab.mars.MCRRTImp.algorithm

import lab.mars.MCRRTImp.base.*
import lab.mars.MCRRTImp.model.*
import org.apache.commons.math3.distribution.NormalDistribution

typealias PathSampler<V> = (SimulatedVehicle<V>, Double) -> MCRRT.Result<V>

class MCRRT<V : Vector<V>>(private val deltaTime: Double,
                           private val spaceRestriction: Space<V>,
                           pathSampler: PathSampler<V>? = null,
                           private val pathLengthProvider: () -> Int,
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


    fun solve(oneTimeConfiguration: OneTimeConfiguration, pathApplier: (Result<V>) -> Unit) {
        beforeAlgorithm()
        pathApplier(algorithm(oneTimeConfiguration))
    }


    enum class ResultStatus {
        Complete,
        TimedOut
    }

    private var timeTolerance = 0L

    class Result<V : Vector<V>>(val status : ResultStatus, val path: Path<WayPoint<V>>? = null)

    private fun algorithm(oneTimeConfiguration: OneTimeConfiguration): Result<V> {
        timeTolerance = oneTimeConfiguration.timeTolerance
        return this.pathSampler(vehicle, deltaTime)
    }

    private val pathSampler: PathSampler<V>

    private var startTime: Long = 0

    private var secondLevelTimeOut = false

    private fun verbose(message: String) {
        if (verbose) {
            println(message)
        }
    }

    private fun defaultSampler(vehicle: SimulatedVehicle<V>, deltaTime: Double): Result<V> {
        var trials = 0
        while (trials < 1) {
            trials++
            startTime = System.currentTimeMillis()
            val areaPathCache = firstLevelRRT(this.vehicle.position, this.vehicle.velocity)
            if (areaPathCache == null) {
                verbose("algorithm failed reason : first level timed out")
                continue
            }
            this.vehicle = vehicleProvider()
            val start = this.vehicle.position
            val velocity = this.vehicle.velocity
            val newPath = secondLevelRRT(areaPathCache, start, velocity, pathLengthProvider())
            if (secondLevelTimeOut) {
                secondLevelTimeOut = false
                verbose("algorithm failed reason : second level timed out")
                continue
            }
            return Result(ResultStatus.Complete, newPath)
        }
        return Result(ResultStatus.TimedOut)
    }


    private fun firstLevelRRT(plannerStart: V, plannerVelocity: V): Path<WayPoint<V>>? {
        var gridWorld: DiscreteWorld<V>
        var timeScalar = deltaTime * 20
        var pathRoot: NTreeNode<WayPoint<V>>
        var gridCellEdgeLength: Double
        while (true) {
            val transforms = vehicle.simulateKinetic(plannerVelocity, timeScalar)
            val scaleBase = transforms.stream().max(Comparator.comparingDouble<Transform<V>> { o -> o.position.len() }).get().position.len()
            transforms.forEach { transform -> transform.position.translate(plannerStart) }
            gridWorld = DiscreteWorld(spaceRestriction, scaleBase)
            val s = System.currentTimeMillis()
            verbose("1st level : start grid world scan")
            gridWorld.scan(obstacles)
            verbose("1st level : grid scan completed in " + (System.currentTimeMillis() - s) + "ms")
            val gridAircraft = gridWorld.formalize(plannerStart)
            gridCellEdgeLength = gridWorld.cellSize.len()
            pathRoot = NTreeNode(WayPoint(gridAircraft, gridCellEdgeLength, plannerVelocity))
            if (gridWorld.insideObstacle(target.origin) && timeScalar >= deltaTime) {
                timeScalar -= 1.0
                if (timeScalar <= deltaTime) {
                    timeScalar = deltaTime
                }
                continue
            }
            val targetCell = gridWorld.formalize(target.origin)
            var stepCount = 0
            var nearest: WayPoint<V>? = null
            var distance = java.lang.Double.MAX_VALUE
            verbose("1st level : starting area path generation")
            while (stepCount < gridWorld.size) {
                var sampled: WayPoint<V> = WayPoint(gridWorld.sample(), gridCellEdgeLength, plannerVelocity)
                if (nearest != null && 0.0.random(1.0) < 1 - nearest.origin.distance(target.origin) / plannerStart.distance(target.origin)) {
                    sampled = WayPoint(target.origin.cpy(), gridCellEdgeLength, plannerVelocity)
                } else if (0.0 random 1.0 < 0.3) {
                    sampled = WayPoint(target.origin.cpy(), gridCellEdgeLength, plannerVelocity)
                }
                val nearestNode = pathRoot.nearestOf(sampled) { c1, c2 -> c1.origin.distance2(c2.origin) }
                val direction = sampled.origin.cpy().translate(nearestNode.element.origin.cpy().reverse())
                val stepped = nearestNode.element.origin.cpy().translate(direction.normalize().scale(gridCellEdgeLength))
                if (gridWorld.insideObstacle(stepped)) {
                    stepCount++
                    continue
                }
                sampled.origin.set(gridWorld.formalize(stepped))
                nearestNode += (sampled)
                verbose("1st level : generate node on area path " + sampled.origin)
                val dis = target.origin.distance2(stepped)
                if (dis < distance) {
                    distance = dis
                    nearest = sampled
                }
                if (sampled.origin.epsilonEquals(targetCell)) {
                    verbose("1st level : area path generation complete")
                    val path = pathRoot.traceTo(sampled)
                    val cellPath = Path<WayPoint<V>>()
                    path.forEach(cellPath::add)
                    return cellPath
                }
                if (System.currentTimeMillis() - startTime > timeTolerance) {
                    return null
                }
                stepCount++
            }
            if (nearest != null) {
                val path = pathRoot.traceTo(nearest)
                val ret = Path<WayPoint<V>>()
                path.forEach { e -> ret.add(WayPoint(e.origin, e.radius, e.velocity)) }
            }
            timeScalar -= 1.0
            if (timeScalar <= 5) {
                timeScalar = 5.0
            }
        }

    }

    private fun secondLevelRRT(areaPath: Path<WayPoint<V>>, startPosition: V, startVelocity: V, count: Int): Path<WayPoint<V>> {
        var start = startPosition
        var v = startVelocity
        val ret = Path<WayPoint<V>>()
        val N01 = NormalDistribution(0.0, 1.0)
        val rotationLimitsOnOneSide = vehicle.rotationLimits / 2
        var deadEndCount = 0
        var startIdx = 0
        areaPath.add(target)
        for (area in areaPath) {
            if (area.origin.distance2(start) <= area.radius * area.radius) {
                startIdx = areaPath.indexOf(area)
            }
        }
        verbose("2nd level : starting actual path generation")
        var s = startIdx
        while (s < areaPath.size) {
            val area = areaPath[s]
            var straightDirectionCount = 0
            while (start.distance2(area.origin) > area.radius * area.radius) {
                var continued = false
                for (c in s + 1 until areaPath.size) {
                    val position = areaPath[c]
                    if (start.distance2(position.origin) <= position.radius * position.radius) {
                        continued = true
                        verbose("2nd level : filtered passed area path point at $s")
                        s = c - 1
                        break
                    }
                }
                if (continued) {
                    break
                }
                val comparableMap = HashMap<Int, Double>()
                val transforms = vehicle.simulateKinetic(v, deltaTime)
                for (transform in transforms) {
                    transform.position.translate(start)
                }
                var outOfBound = false
                var origin = area.origin
                val prob = s / areaPath.size.toDouble()
                if ((0.0 random 1.0) < prob) {
                    origin = target.origin
                }
                for (i in transforms.indices) {
                    val t = transforms[i]
                    val target = origin.cpy().translate(start.cpy().reverse()).normalize()
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
                val selectedIdx = comparableMap.entries
                        .sortedBy { it.value }
                        .map {
                            sum += it.value
                            Pair(it.key, sum)
                        }.filter { it.second >= probability }[0].first
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
                    start = selected.position.cpy()
                    v = selected.velocity.cpy()
                    if (selectedIdx.toDouble() == Math.ceil(transforms.size / 2.0)) {
                        straightDirectionCount += 1
                        ret.utility -= straightDirectionCount
                    } else {
                        if (straightDirectionCount > 0) {
                            straightDirectionCount -= 1
                        }
                    }
                    if (ret.size == count) {
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
                        start = vehicle.position.cpy()
                        v = vehicle.velocity.cpy()
                        deadEndCount = 0
                    } else {
                        val last = ret.end
                        start = last.origin.cpy()
                        v = last.velocity.cpy()
                    }
                }
                if (System.currentTimeMillis() - startTime > timeTolerance) {
                    secondLevelTimeOut = true
                    return ret
                }
            }
            s++
        }
        return ret
    }


    init {
        if (pathSampler != null) {
            this.pathSampler = pathSampler
        } else {
            this.pathSampler = this::defaultSampler
        }
    }

}
