package lab.mars.MCRRTImp.algorithm

import lab.mars.MCRRTImp.base.*
import lab.mars.MCRRTImp.base.Vector
import lab.mars.MCRRTImp.model.*
import org.apache.commons.math3.distribution.NormalDistribution
import java.util.*

typealias PathSampler<V> = (SimulatedVehicle<V>, OneTimeConfiguration<V>) -> Unit

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


    private lateinit var pathApplier: (Result<V>) -> Unit

    fun solve(oneTimeConfiguration: OneTimeConfiguration<V>, pathApplier: (Result<V>) -> Unit) {
        beforeAlgorithm()
        this.pathApplier = pathApplier
        algorithm(oneTimeConfiguration)
    }


    enum class ResultStatus {
        Complete,
        InProgress,
        TimedOut,
        Impossible
    }


    class Result<V : Vector<V>>(val status: ResultStatus, val areaPath: Path<WayPoint<V>>? = null, val actualPath: Path<WayPoint<V>>? = null)


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

    private fun List<WayPoint<V>>.toPath(): Path<WayPoint<V>> {
        val currentPath = Path<WayPoint<V>>()
        this.forEach { currentPath.add(it) }
        return currentPath
    }

    private inline fun verbose(message: String) {
        if (verbose) {
            println(message)
        }
    }

    private fun algorithm(oneTimeConfiguration: OneTimeConfiguration<V>) {
        this.pathSampler(vehicle, oneTimeConfiguration)
    }

    private fun defaultSampler(vehicle: SimulatedVehicle<V>, configuration: OneTimeConfiguration<V>) {
        startTime = System.currentTimeMillis()
        if (configuration.levelOneReplan) {
            if (configuration.levelOneUseCache) {
                areaPathCache = configuration.levelOnePathCache
            }
            val newPath = firstLevelRRT(
                    plannerStart = this.vehicle.position,
                    plannerVelocity = this.vehicle.velocity,
                    rotationLimits = configuration.levelOneRotationLimit,
                    deltaTime = firstLevelDeltaTime,
                    cacheIdx = configuration.levelOneReplanCacheIdx,
                    timeTolerance = configuration.timeTolerance)
            if (newPath == null) {
                verbose("algorithm failed reason : first level timed out")
                pathApplier.invoke(Result(ResultStatus.TimedOut))
                return
            } else if (newPath.size == 0) {
                verbose("algorithm failed reason : target impossible to reach")
                pathApplier(Result(ResultStatus.Impossible))
                return
            }
            areaPathCache = newPath
        }
        if (configuration.levelTwoReplan) {
            this.vehicle = vehicleProvider()
            if (configuration.levelOneUseCache) {
                this.areaPathCache = configuration.levelOnePathCache
            }
            secondLevelRRT(
                    areaPath = areaPathCache!!,
                    idxFrom = configuration.levelTwoFromIdx,
                    idxTo = configuration.levelTwoToIdx,
                    fromPosition = this.vehicle.position,
                    fromVelocity = this.vehicle.velocity,
                    approachDistance = configuration.wayPointApproachDistance,
                    deltaTime = secondLevelDeltaTime,
                    timeTolerance = configuration.timeTolerance)
        } else {
            pathApplier.invoke(Result(ResultStatus.Complete, areaPathCache))
        }
    }


    private fun firstLevelRRT(plannerStart: V, plannerVelocity: V, rotationLimits: Double, deltaTime: Double, cacheIdx: Int = -1, timeTolerance: Long): Path<WayPoint<V>>? {
        while (true) {
            val transforms = vehicle.simulateKinetic(plannerVelocity, 1.0)
            val scaleBase = transforms.map { it.position.len() }.sortedBy { it }.asReversed()[0] * deltaTime
            transforms.forEach { transform -> transform.position.translate(plannerStart) }
            val gridWorld = DiscreteWorld(spaceRestriction, scaleBase)
            val gridScanStartTime = System.currentTimeMillis()
            verbose("1st level : start grid world scan")
            gridWorld.scan(obstacles)
            verbose("1st level : grid scan completed in " + (System.currentTimeMillis() - gridScanStartTime) + "ms")
            val pathStart: NTreeNode<WayPoint<V>>
            val expansionStart: NTreeNode<WayPoint<V>>
            if (cacheIdx != -1) {
                pathStart = NTreeNode(areaPathCache!![0])
                var child = pathStart
                for (idx in 1 until cacheIdx) {
                    child.add(areaPathCache!![idx])
                    child = child[0]
                }
                expansionStart = child
            } else {
                pathStart = NTreeNode(WayPoint(plannerStart, scaleBase, plannerVelocity))
                expansionStart = pathStart
            }
            if (gridWorld.insideObstacle(target.origin)) {
                return Path()
            }
            var stepCount = 0
            verbose("1st level : starting area path generation")
            var targetNearestDistance = Double.MAX_VALUE
            while (true) {
                var sampled: WayPoint<V> = WayPoint(spaceRestriction.sample(), scaleBase, plannerVelocity)
                if (0.0 random 1.0 < 0.5) {
                    sampled = WayPoint(target.origin.cpy(), scaleBase, plannerVelocity)
                }
                val sampledNearestNode = expansionStart.nearestChildOf(sampled) { c1, s ->
                    if (c1.childrenSize >= rotationLimits) {
                        Double.MAX_VALUE
                    }
                    c1.element.origin.distance(s.origin)

                }
                val direction: V = if (sampledNearestNode.parent != null) {
                    sampledNearestNode.element.origin.cpy().translate(sampledNearestNode.parent!!.element.origin.cpy().reverse())
                } else {
                    plannerVelocity
                }
                val expandDirection = sampled.origin.cpy().translate(sampledNearestNode.element.origin.cpy().reverse())
                if (expandDirection.angle(direction) > rotationLimits) {
                    if (expandDirection.crs(direction) < 0) {
                        expandDirection.set(direction.cpy().rotate(rotationLimits))
                    } else {
                        expandDirection.set(direction.cpy().rotate(-rotationLimits))
                    }
                }
                sampled.origin.set(sampledNearestNode.element.origin.cpy().translate(expandDirection.normalize().scale(scaleBase)))
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
                sampledNearestNode += (sampled)
                val distance = sampled.origin.distance(target.origin)
                if (distance < targetNearestDistance) {
                    targetNearestDistance = distance
                }
                verbose("1st level : generate node on area path " + sampled.origin)
                if (sampled.origin.distance(target.origin.cpy()) <= scaleBase) {
                    verbose("1st level : area path generation complete")
                    val trace = pathStart.traceTo(sampled)
                    val cellPath = trace.toPath()
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

    private fun secondLevelRRT(areaPath: Path<WayPoint<V>>, idxFrom: Int, idxTo: Int, fromPosition: V, fromVelocity: V, approachDistance: Double, deltaTime: Double, timeTolerance: Long) {
        var curPosition = fromPosition
        var curVelocity = fromVelocity
        val N01 = NormalDistribution(0.0, 1.0)
        val rotationLimitsOnOneSide = vehicle.rotationLimits / 2
        var deadEndCount = 0
        verbose("2nd level : starting actual path generation")
        val idxUpperBound = if (idxTo >= areaPath.size) areaPath.size - 1 else idxTo
        var straightDirectionCount = 0
        var idx = idxFrom
        while (idx <= idxUpperBound) {
            val area = areaPath[idx]
            val currentPath = Path<WayPoint<V>>()
            val from = idx
            while (curPosition.distance2(area.origin) > approachDistance * approachDistance) {
                for (j in idx + 1 until idxUpperBound) {
                    if (curPosition.distance2(area.origin) <= approachDistance * approachDistance) {
                        idx = j + 1
                        break
                    }
                }
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
                var safeTransform = true
                val selectedIdx =
                        comparableMap.entries
                                .map {
                                    Pair(it.key, Math.abs(it.value - probability))
                                }.sortedBy { it.second }[0].first
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
                    pathApplier.invoke(Result(ResultStatus.TimedOut))
                    return
                }
            }
            currentPath.from = from
            currentPath.to = idx
            if (idx == idxUpperBound) {
                pathApplier.invoke(Result(ResultStatus.Complete, areaPath, currentPath))
            } else {
                pathApplier.invoke(Result(ResultStatus.InProgress, areaPath, currentPath))
            }
            idx++
        }
        verbose("2nd level : finished path generation")

    }


}
