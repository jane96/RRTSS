package lab.mars.MCRRTImp.Vector2BasedImp

import javafx.application.Application
import javafx.scene.paint.Color
import infrastructure.Vector2BasedImp.Vector2
import infrastructure.model.Attacker
import lab.mars.MCRRTImp.model.Transform
import infrastructure.ui.GUIBase
import infrastructure.ui.Pencil
import org.junit.Test

class TestAttacker : GUIBase() {


        private val aircraft = Attacker(Vector2(100.0, 100.0), Vector2(1.0, 1.0).normalize().scale(4.16666667), 10.0, 100, 200.0, 50.0, 2.0, null, null, null)


        var deltaTime = 30.0

        override fun draw(pencil: Pencil) {
            val transforms = aircraft.simulateKinetic(aircraft.velocity, deltaTime)
            val origin = aircraft.position
            val scaleBase = 2.0
            pencil.scale(scaleBase).filled().color(Color.YELLOWGREEN).circle(origin, 1.0)
            for (t in transforms) {
                val position = t.position.cpy().scale(1.0).translate(origin)
                val direction = t.velocity
                val transformed = position.cpy().translate(direction)
                pencil.filled().color(Color.WHITE).circle(position, 5 / scaleBase)
                pencil.stroked(2.0).color(Color.WHITE).line(position, transformed)
            }
            deltaTime ++
            if (deltaTime > 60.0) {
                deltaTime = 1.0
            }
        }

    }



fun main(args: Array<String>) {
    Application.launch(TestAttacker::class.java, *args)
}
