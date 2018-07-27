package lab.mars.MCRRTImp;

import javafx.scene.paint.Color;
import lab.mars.MCRRTImp.model.Attacker;
import lab.mars.MCRRTImp.model.Transform;
import lab.mars.MCRRTImp.infrastructure.ui.GUIBase;
import lab.mars.MCRRTImp.infrastructure.ui.Pencil;
import lab.mars.MCRRTImp.model.Vector2;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

import java.util.List;

public class TestAttacker {



    @Test
    public void test() {

    }

    public static class TestTransform extends GUIBase {


        private Attacker<Vector2> aircraft = new Attacker<>(new Vector2(5, 5), new Vector2(1, 1).normalize().scale(3), 10, 30, 200, 50, 2);


        @Override
        protected void draw(Pencil pencil) {
            List<Transform<Vector2>> transforms = aircraft.simulateKinetic(aircraft.position(), aircraft.velocity(), 1);
            Vector2 targetPosition = new Vector2(10, 10);
            NormalDistribution N01 = new NormalDistribution(0, 1);
            double halfRotation = aircraft.rotationLimits() / 2.0;
            transforms.forEach(t -> {
                double angle = t.velocity.cpy().angle(targetPosition.cpy().subtract(aircraft.position()));
            });
            Vector2 origin = aircraft.position();
            double scaleBase = 150;
            pencil.scale(scaleBase).filled().color(Color.YELLOWGREEN).circle(origin, 1);
            for (Transform<Vector2> t : transforms) {
                Vector2 position = t.position;
                Vector2 direction = t.velocity;
                Vector2 transformed = position.cpy().translate(direction);
                pencil.filled().color(Color.BLACK).circle(position, 5 / scaleBase);
                pencil.stroked(2).color(Color.BLACK).line(position, transformed);
            }
            Transform<Vector2> next = transforms.get(4);
            transforms = aircraft.simulateKinetic(next.position, next.velocity.cpy(), 2);
            for (Transform t : transforms) {
                Vector2 position = t.position;
                Vector2 direction = t.velocity;
                Vector2 transformed = position.cpy().translate(direction);
                pencil.filled().color(Color.DARKGREEN).circle(position, 5 / scaleBase);
                pencil.stroked(2).color(Color.DARKGREEN).line(position, transformed);
            }
            transforms = aircraft.simulateKinetic(aircraft.position(), aircraft.velocity(), 2);
            for (Transform t : transforms) {
                Vector2 position = t.position;
                Vector2 direction = t.velocity;
                Vector2 transformed = position.cpy().translate(direction);
                pencil.filled().color(Color.RED).circle(position, 5 / scaleBase);
                pencil.stroked(2).color(Color.RED).line(position, transformed);
            }
        }
    }
}
