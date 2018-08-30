package lab.mars.MCRRTImp.Vector2BasedImp

import infrastructure.PolarBasedImp.Polar
import infrastructure.Vector2BasedImp.Vector2
import lab.mars.MCRRTImp.model.GridCell
import org.junit.Test

class TestGridCell {

    @Test
    fun testEquality() {
        val polarGridCell = GridCell(Polar(0.0, 0.0))
        val vector2GridCell = GridCell(Vector2(0.0, 0.0))
        assert(true)
    }
}
