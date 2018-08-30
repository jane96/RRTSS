package infrastructure.Vector2BasedImp


import lab.mars.MCRRTImp.base.Obstacle

class CircleBound(x: Double, y: Double, internal var radius: Double) : Obstacle<Vector2> {

    internal var centroid: Vector2 = Vector2(x, y)

    override operator fun contains(o: Vector2): Boolean {
        return o.distance2(centroid) > radius * radius
    }
}
