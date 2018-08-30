package infrastructure.Vector2BasedImp

import lab.mars.MCRRTImp.base.Obstacle

class EyeSight(private val origin: Vector2, private val direction: Vector2, private val radius: Double, private val angle: Double) : Obstacle<Vector2> {

    override operator fun contains(o: Vector2): Boolean {
        return !(this.origin.distance2(o) <= this.radius * this.radius && direction.angle(o.cpy().subtract(origin)) <= this.angle / 2.0)
    }
}
