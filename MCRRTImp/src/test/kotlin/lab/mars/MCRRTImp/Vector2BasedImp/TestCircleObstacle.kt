package lab.mars.MCRRTImp.Vector2BasedImp

import infrastructure.Vector2BasedImp.CircleObstacle
import infrastructure.Vector2BasedImp.Vector2
import org.junit.Test

class TestCircleObstacle {

    @Test
    fun testContains() {
        val obstacle = CircleObstacle(10.0, 10.0, 15.0)
        val planeCenter = Vector2(0.0, 0.0)
        val plane2Center = Vector2(3.0, 3.0)
        assert(obstacle.contains(planeCenter))
        assert(obstacle.contains(plane2Center))
    }

}
