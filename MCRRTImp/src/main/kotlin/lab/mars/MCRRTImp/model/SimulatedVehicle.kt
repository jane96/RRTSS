package lab.mars.MCRRTImp.model

import lab.mars.MCRRTImp.base.Vector

abstract class SimulatedVehicle<V : Vector<V>>(private val positionV: V, private val velocityV: V, val rotationLimits : Double, val numberOfDirection : Int, val safeDistance : Double) {

    var position: V
        get() = positionV.cpy()
        set(value) {positionV.set(value)}

    var velocity: V
        get() = velocityV.cpy()
        set(value) { velocityV.set(value) }

    protected abstract fun simulateVelocity(currentVelocity : Double, angle : Double) : Double

    open fun simulateKinetic(currentVelocity: V, deltaTime : Double) : List<Transform<V>> {
        val ret = ArrayList<Transform<V>>()
        val deltaTheta = this.rotationLimits / this.numberOfDirection
        val sliceCount = 100.0
        for (i in -numberOfDirection / 2..numberOfDirection / 2) {
            val rotated = currentVelocity.cpy()
            val translated = currentVelocity.cpy().zero()
            val nextV = currentVelocity.cpy()
            val totalAngleRotated = i.toDouble() * deltaTheta * deltaTime
            val slicedDeltaTheta = totalAngleRotated / sliceCount      // The delta for Integrating function
            nextV.rotate(totalAngleRotated)        // Set the Velocity angle
            nextV.normalize().scale(simulateVelocity(currentVelocity.len(), i * deltaTheta))      // Set the Velocity's len()
            val newV = nextV.len()          // Get the Velocity's len()
            var c = 0
            while (c < sliceCount) {
                rotated.rotate(slicedDeltaTheta)
                translated.translate(rotated.normalize().scale(newV * deltaTime / sliceCount))
                c++
            }
            ret.add(Transform(nextV, translated))
        }
        return ret
    }

}