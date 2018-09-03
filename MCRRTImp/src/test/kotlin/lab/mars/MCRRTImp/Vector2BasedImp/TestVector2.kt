package lab.mars.MCRRTImp.Vector2BasedImp

import infrastructure.Vector2BasedImp.Vector2
import org.junit.Test

import lab.mars.MCRRTImp.base.epsilonEquals

class TestVector2 {

    @Test
    fun testDistance() {
        val v1 = Vector2(1.0, 1.0)
        val v2 = Vector2(2.0, 2.0)
        val result = v1.distance(v2)
        println(result)
        assert(result.epsilonEquals(Math.sqrt(2.0)))
    }

    @Test
    fun testNormalize() {
        val v1 = Vector2(3.0, 4.0)
        v1.normalize()
        assert(v1.len().epsilonEquals(1.0))
    }

    @Test
    fun testDot() {
        val v1 = Vector2(2.0, 5.0)
        val v2 = Vector2(3.0, 7.0)
        val result = v1.dot(v2)
        assert(result.epsilonEquals((2 * 3 + 5 * 7).toDouble()))
    }

    @Test
    fun testAngle() {
        val v1 = Vector2(1.0, 0.0)
        val v2 = Vector2(Math.cos(Math.PI / 6), Math.sin(Math.PI / 6))
        val result = v1.angle(v2)
        println(result)
        assert(result epsilonEquals 30.0)
    }

    @Test
    fun testRotate() {
        val v1 = Vector2(1.0, 0.0)
        val v2 = v1.cpy().rotate(30.0)
        println(v2)
        assert(v2.x().epsilonEquals(Math.cos(Math.PI / 6)))
        assert(v2.y().epsilonEquals(Math.sin(Math.PI / 6)))
    }

    @Test
    fun testLerp() {
        val v1 = Vector2(2.0, 2.0)
        val v2 = Vector2(0.0, 1.0)
        v1.lerp(v2, 0.5)
        println(v1)
        println(v2)
        assert(v1.x().epsilonEquals(1.0) && v1.y().epsilonEquals(1.5))
    }
}
