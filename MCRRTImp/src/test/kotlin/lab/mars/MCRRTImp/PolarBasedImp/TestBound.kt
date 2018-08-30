package lab.mars.MCRRTImp.PolarBasedImp

import javafx.scene.paint.Color
import infrastructure.Vector2BasedImp.Vector2
import infrastructure.PolarBasedImp.Bound
import infrastructure.PolarBasedImp.Polar
import infrastructure.ui.GUIBase
import infrastructure.ui.Pencil
import lab.mars.MCRRTImp.base.random

import java.util.ArrayList

class TestBound : GUIBase() {

    private val bound = Bound(100.0, 100.0)

    private val points = ArrayList<Polar>()

    override fun draw(pencil: Pencil) {
        val polar = Polar(0.0 random 200.0, 0.0 random 360.0)
        points.add(polar)
        for (point in points) {
            val origin = Vector2(1.0, 0.0)
            origin.rotate(point.theta()).normalize().scale(point.r())
            if (bound.contains(point)) {
                pencil.filled().color(Color.RED).circle(origin, 1.0)
            } else {
                pencil.filled().color(Color.BLUE).circle(origin, 1.0)
            }

        }
    }
}
