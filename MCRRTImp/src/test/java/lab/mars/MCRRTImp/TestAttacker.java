package lab.mars.MCRRTImp;

import javafx.application.Application;
import javafx.scene.paint.Color;
import lab.mars.MCRRTImp.Vector2BasedImp.Attacker;
import lab.mars.MCRRTImp.model.Transform;
import lab.mars.MCRRTImp.infrastructure.ui.GUIBase;
import lab.mars.MCRRTImp.infrastructure.ui.Pencil;
import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

import java.util.List;

public class TestAttacker {

    @Test
    public void testTimeScalar() {
        Attacker<Vector2> aircraft = new Attacker<>(new Vector2(5, 5), new Vector2(1, 1).normalize().scale(4.16666667), 10, 5, 200, 50, 2, null, null, null);
        double timeScalar = 1;
        while (timeScalar < 100) {
            List<Transform<Vector2>> transforms = aircraft.simulateKinetic(aircraft.velocity(), timeScalar);
            System.out.println("time scalar : " + timeScalar);
            System.out.println(transforms.get(0).position.angle(transforms.get(transforms.size() - 1).position));
            timeScalar ++;
        }
    }

    public static class TestTransform extends GUIBase {


        private Attacker<Vector2> aircraft = new Attacker<>(new Vector2(5, 5), new Vector2(1, 1).normalize().scale(4.16666667), 10, 5, 200, 50, 2, null, null, null);


        @Override
        protected void draw(Pencil pencil) {
            List<Transform<Vector2>> transforms = aircraft.simulateKinetic(aircraft.velocity(), 10);
            Vector2 origin = aircraft.position();
            double scaleBase = 1;
            pencil.scale(scaleBase).filled().color(Color.YELLOWGREEN).circle(origin, 1);
            for (Transform<Vector2> t : transforms) {
//                System.out.println(t.position);
                Vector2 position = t.position.cpy().translate(origin);
                Vector2 direction = t.velocity;
                Vector2 transformed = position.cpy().translate(direction);
                pencil.filled().color(Color.BLACK).circle(position, 5 / scaleBase);
                pencil.stroked(2).color(Color.BLACK).line(position, transformed);
            }
        }

        public static void main(String[] args) {
            Application.launch(args);
        }
    }
}
