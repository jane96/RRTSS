package infrastructure.PolarBasedImp

import lab.mars.MCRRTImp.base.Obstacle

class Bound(internal var height: Double, internal var width: Double) : Obstacle<Polar> {

    override operator fun contains(o: Polar): Boolean {
        if (o.theta() > 90) {
            return false
        }
        val x = o.r() * Math.cos(Math.toRadians(o.theta()))
        val y = o.r() * Math.sin(Math.toRadians(o.theta()))
        return x <= width && y <= height
    }
}
