package infrastructure.PolarBasedImp

import lab.mars.MCRRTImp.base.Obstacle


class CircleObstacle(internal var centroid: Polar, internal var radius: Double) : Obstacle<Polar> {

    override operator fun contains(o: Polar): Boolean {
        return false
    }
}
