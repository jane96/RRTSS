package infrastructure.Vector2BasedImp


import lab.mars.MCRRTImp.base.Obstacle

class Bound(private val width: Double, private val height: Double) : Obstacle<Vector2> {

    override operator fun contains(o: Vector2): Boolean {
        if (o.x() < 0 || o.x() > width) {
            return true
        }
        return if (o.y() < 0 || o.y() > height) {
            true
        } else false
    }
}
