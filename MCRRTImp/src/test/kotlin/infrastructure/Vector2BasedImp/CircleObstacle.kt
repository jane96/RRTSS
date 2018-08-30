package infrastructure.Vector2BasedImp


import lab.mars.MCRRTImp.base.Obstacle

class CircleObstacle(x: Double, y: Double, var radius: Double) : Obstacle<Vector2> {


    var origin: Vector2 = Vector2(x, y)

    override operator fun contains(o: Vector2): Boolean {
        return o.distance2(origin) < radius * radius
    }
}
