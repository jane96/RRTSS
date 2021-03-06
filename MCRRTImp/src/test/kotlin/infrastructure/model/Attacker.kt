package infrastructure.model

import lab.mars.MCRRTImp.algorithm.MCRRT
import lab.mars.MCRRTImp.base.*
import lab.mars.MCRRTImp.model.*

import java.util.ArrayList
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class Attacker<V : Vector<V>>(position: V,
                              velocity: V,
                              rotationLimits: Double,
                              numberOfDirection: Int,
                              safeDistance: Double,
                              private val viewDistance: Double,
                              private val viewAngle: Double,
                              private val designatedTargetPosition: WayPoint<V>?,
                              area: Space<V>?,
                              private val obstacleProvider: (() -> List<Obstacle<V>>)?) : SimulatedVehicle<V>(position, velocity, rotationLimits, numberOfDirection, safeDistance) {

    private val topVelocity: Double = velocity.len()

    private val actualPath = ConcurrentLinkedQueue<Path<WayPoint<V>>>()

    private val areaPath = ConcurrentLinkedQueue<Path<WayPoint<V>>>()

    private var gridWorld: DiscreteWorld<V>? = null

    private lateinit var algorithm: MCRRT<V>

    init {
        if (area != null && obstacleProvider != null && designatedTargetPosition != null)
            algorithm = MCRRT(
                    spaceRestriction = area,
                    obstacleProvider = obstacleProvider,
                    vehicleProvider = { this },
                    targetProvider = { designatedTargetPosition },
                    verbose = false,
                    firstLevelDeltaTime = 600.0,
                    secondLevelDeltaTime = 10.0
            )
    }

    var leaves: List<NTreeNode<WayPoint<V>>> = ArrayList()
        private set

    private fun leafApplier(leaves: List<NTreeNode<WayPoint<V>>>) {
        this.leaves = leaves
    }


    fun setDesignatedTarget(target: WayPoint<V>) {
        this.designatedTargetPosition?.apply {
            origin.set(target.origin)
            velocity.set(target.velocity)
        }
    }

    fun actualPath(): List<Path<WayPoint<V>>> {
        val ret = ArrayList<Path<WayPoint<V>>>()
        actualPath.forEach { ret.add(it) }
        ret.sortBy {  it.utility }
        return ret
    }

    fun target(): WayPoint<V>? {
        return designatedTargetPosition
    }

    fun gridWorld(): DiscreteWorld<V>? {
        return gridWorld
    }

    fun setAreaPath(areaPath: Path<WayPoint<V>>) {
        this.areaPath.offer(areaPath)
    }

    fun setGridWorld(gridWorld: DiscreteWorld<V>) {
        this.gridWorld = gridWorld
    }

    fun setActualPath(actualPath: Path<WayPoint<V>>) {
        this.actualPath.offer(actualPath)
        //        startAlgorithm();
    }

    fun areaPath(): Queue<Path<WayPoint<V>>> {
            return this.areaPath
    }

    override fun simulateVelocity(currentVelocity: Double, angle: Double): Double {
        return topVelocity * (1 - Math.abs(angle) / this.rotationLimits)
    }

    fun viewDistance(): Double {
        return viewDistance
    }

    fun viewAngle(): Double {
        return viewAngle
    }


    fun startAlgorithm() {

        Thread {
            var i = 0
            while (true) {
//                i++
                println(i)
                val configuration : OneTimeConfiguration<V>
                if (i > 1) {
                    configuration = OneTimeConfiguration(
                            timeTolerance = Long.MAX_VALUE,
                            levelOneReplan = true,
                            levelTwoReplan = false,
                            wayPointApproachDistance = 10.0,
                            levelOneUseCache = true,
                            levelOnePathCache = actualPath.peek(),
                            levelOneReplanCacheIdx = 10

                    )
                } else {
                    configuration = OneTimeConfiguration(
                            timeTolerance = Long.MAX_VALUE,
                            levelOneReplan = true,
                            levelTwoReplan = true,
                            levelTwoFromIdx = 0,
                            levelTwoToIdx = 100,
                            wayPointApproachDistance = 10.0,
                            levelOneUseCache = false)
                }
                algorithm.solve(configuration) { value ->
                    when (value.status) {
                        MCRRT.ResultStatus.InProgress, MCRRT.ResultStatus.Complete -> {
                            actualPath.clear()
                            actualPath.add(value.areaPath!!)
                        }
                        else -> {
                            println(value.status)
                        }
                    }
                }
                val cpy = position.cpy()
                var flag: Boolean
                do {
                    cpy.dimensions[0].value = 0.0 random 1920.0
                    cpy.dimensions[1].value = 0.0 random 1080.0
                    flag = false
                    if (obstacleProvider != null) {
                        for (obstacle in obstacleProvider.invoke()) {
                            if (obstacle.contains(cpy)) {
                                flag = true
                            }
                        }
                    }
                } while (flag)
                position.set(cpy)
            }
            println("over")
        }.start()
    }

}
