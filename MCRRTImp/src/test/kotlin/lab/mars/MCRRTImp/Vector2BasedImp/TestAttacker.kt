package lab.mars.MCRRTImp.Vector2BasedImp

import javafx.application.Application
import javafx.scene.paint.Color
import infrastructure.Vector2BasedImp.Vector2
import infrastructure.model.Attacker
import lab.mars.MCRRTImp.model.Transform
import infrastructure.ui.GUIBase
import infrastructure.ui.Pencil
import org.junit.Test

class TestAttacker {

    @Test
    fun testTimeScalar() {
//        val aircraft = Attacker(Vector2(5.0, 5.0), Vector2(1.0, 1.0).normalize().scale(4.16666667), 10.0, 5, 200.0, 50.0, 2.0, null!!, null!!, null!!)
//        var timeScalar = 1
//        while (timeScalar < 100) {
//            val transforms = aircraft.simulateKinetic(aircraft.velocity, timeScalar.toDouble())
//            //            System.out.println("time scalar : " + timeScalar);
//            //            System.out.println(transforms.get(0).position.angle(transforms.get(transforms.size() - 1).position));
//            timeScalar++
//            transforms.forEach { transform -> println(transform.velocity.angle(Vector2(1.0, 1.0))) }
//        }
    }

    class TestTransform : GUIBase() {


        private val aircraft = Attacker(Vector2(5.0, 5.0), Vector2(1.0, 1.0).normalize().scale(4.16666667), 10.0, 5, 200.0, 50.0, 2.0, null!!, null!!, null!!)


        override fun draw(pencil: Pencil) {
            val transforms = aircraft.simulateKinetic(aircraft.velocity, 10.0)
            val origin = aircraft.position
            val scaleBase = 1.0
            pencil.scale(scaleBase).filled().color(Color.YELLOWGREEN).circle(origin, 1.0)
            for (t in transforms) {
                //                System.out.println(t.position);
                val position = t.position.cpy().translate(origin)
                val direction = t.velocity
                val transformed = position.cpy().translate(direction)
                pencil.filled().color(Color.BLACK).circle(position, 5 / scaleBase)
                pencil.stroked(2.0).color(Color.BLACK).line(position, transformed)
            }
        }

    }
}

fun main(args: Array<String>) {
    Application.launch(TestAttacker.TestTransform::class.java, *args)
}