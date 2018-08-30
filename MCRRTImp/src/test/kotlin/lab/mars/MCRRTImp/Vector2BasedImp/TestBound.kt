package lab.mars.MCRRTImp.Vector2BasedImp

import infrastructure.Vector2BasedImp.Bound
import infrastructure.Vector2BasedImp.Vector2
import org.junit.Test

class TestBound {

    @Test
    fun testContains() {
        val bound = Bound(100.0, 100.0)
        assert(bound.contains(Vector2(100.1, 100.0)))
    }
}
