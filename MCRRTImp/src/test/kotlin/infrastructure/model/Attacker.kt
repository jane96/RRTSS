package infrastructure.model

import infrastructure.Vector2BasedImp.Vector2
import lab.mars.MCRRTImp.algorithm.MCRRT
import lab.mars.MCRRTImp.base.*
import lab.mars.MCRRTImp.model.*

import java.util.ArrayList
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue

class Attacker<V : Vector<V>>(position: V,
                              velocity: V,
                              rotationLimits: Double,
                              numberOfDirection: Int,
                              safeDistance: Double,
                              private val viewDistance: Double,
                              private val viewAngle: Double,
                              private var designatedTargetPosition: WayPoint<V>,
                              area: Space<V>,
                              private val obstacleProvider: () -> List<Obstacle<V>>) : SimulatedVehicle<V>(position, velocity, rotationLimits, numberOfDirection, safeDistance) {

    private val topVelocity: Double = velocity.len()

    private val actualPath = ConcurrentLinkedQueue<Path<WayPoint<V>>>()

    private val areaPath = ConcurrentLinkedQueue<Path<WayPoint<V>>>()

    private var gridWorld: DiscreteWorld<V>? = null

    private val algorithm: MCRRT<V> = MCRRT(
            spaceRestriction = area,
            obstacleProvider = obstacleProvider,
            vehicleProvider = { this },
            targetProvider = { designatedTargetPosition },
            verbose = true
    )

    var leaves: List<NTreeNode<WayPoint<V>>> = ArrayList()
        private set

    private fun leafApplier(leaves: List<NTreeNode<WayPoint<V>>>) {
        this.leaves = leaves
    }


    fun setDesignatedTarget(target: WayPoint<V>) {
        this.designatedTargetPosition = target
    }

    fun actualPath(): Path<WayPoint<V>> ? {
        return when {
            actualPath.size > 1 -> actualPath.poll()
            actualPath.size > 0 -> actualPath.peek()
            else -> null
        }
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
                algorithm.solve(OneTimeConfiguration(
                        timeTolerance = Long.MAX_VALUE,
                        levelOneReplan = true,
                        levelTwoReplan = true,
                        levelTwoStartIdx = 0,
                        wayPointApproachDistance = 10.0,
                        firstLevelDeltaTime = 4.0,
                        secondLevelDeltaTime = 1.0)) { value ->
                    if (value.status == MCRRT.ResultStatus.Complete) {
                        actualPath.offer(value.levelOnePath)
                    }
                }
                val cpy = position.cpy()
                var flag: Boolean
                do {
                    cpy.dimensions[0].value = 0.0 random 1920.0
                    cpy.dimensions[1].value = 0.0 random 1080.0
                    flag = false
                    for (obstacle in obstacleProvider()) {
                        if (obstacle.contains(cpy)) {
                            flag = true
                        }
                    }
                } while (flag)
                position.set(cpy)
            }.start()
    }

}
