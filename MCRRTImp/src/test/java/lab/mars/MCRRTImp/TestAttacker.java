package lab.mars.MCRRTImp;

import javafx.scene.paint.Color;
import lab.mars.MCRRTImp.model.Attacker;
import lab.mars.MCRRTImp.model.Transform;
import lab.mars.MCRRTImp.infrastructure.ui.GUIBase;
import lab.mars.MCRRTImp.infrastructure.ui.Pencil;
import lab.mars.MCRRTImp.model.Vector2;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestAttacker {



    @Test
    public void test() {

    }

    public static class TestTransform extends GUIBase {


        private Attacker aircraft = new Attacker(new Vector2(5, 5), new Vector2(1, 1).normalize().scale(3), 10, 30, 200, 50, 2);

        private double simulateVelocity(double velocity, double angle) {
            return velocity * (1 - Math.abs(angle) / aircraft.rotationLimits());
        }

        private List<Transform> simulateKinetic(Vector2 position, Vector2 velocity, double deltaTime) {
            List<Transform> ret = new ArrayList<>();
            double v = velocity.len();
            int sliceCount = 100;
            double rotationLimits = aircraft.rotationLimits();
            double graduation = aircraft.rotationGraduation();
            for (double i = 0; i < rotationLimits / 2; i += graduation) {
                double totalAngleRotated = i * deltaTime;
                double slicedAngleRotated = totalAngleRotated / sliceCount;
                Vector2 rotated = velocity.cpy();
                Vector2 translated = position.cpy();
                double newV = simulateVelocity(v, i);
                for (int c = 0; c < sliceCount; c++) {
                    rotated.rotate(slicedAngleRotated);
                    translated.add(rotated.cpy().normalize().scale(newV * deltaTime / sliceCount));
                }
                ret.add(new Transform(translated, rotated.normalize().scale(newV)));
            }
            for (double i = -graduation; i > -rotationLimits / 2; i -= graduation) {
                double totalAngleRotated = i * deltaTime;
                double slicedAngleRotated = totalAngleRotated / sliceCount;
                Vector2 rotated = velocity.cpy();
                Vector2 translated = position.cpy();
                double newV = simulateVelocity(v, i);
                for (int c = 0; c < sliceCount; c++) {
                    rotated.rotate(slicedAngleRotated);
                    translated.add(rotated.normalize().cpy().scale(newV * deltaTime / sliceCount));
                }
                ret.add(new Transform(translated, rotated.normalize().scale(newV)));
            }
            return ret;
        }

        @Override
        protected void draw(Pencil pencil) {
            List<Transform> transforms = simulateKinetic(aircraft.position(), aircraft.velocity(), 1);
            Vector2 targetPosition = new Vector2(10, 10);
            NormalDistribution N01 = new NormalDistribution(0, 1);
            double halfRotation = aircraft.rotationLimits() / 2.0;
            transforms.forEach(t -> {
                double angle = t.velocity.cpy().angle(targetPosition.cpy().subtract(aircraft.position()));

                System.out.println(angle);
            });
            Vector2 origin = aircraft.position();
            double scaleBase = 100;
            pencil.scale(scaleBase).filled().color(Color.YELLOWGREEN).circle(origin, 1);
            for (Transform t : transforms) {
                Vector2 position = t.position;
                Vector2 direction = t.velocity;
                Vector2 transformed = position.cpy().add(direction);
                pencil.stroked(2).color(Color.BLACK).line(position, transformed).color(Color.RED).line(position, origin);
            }
        }
    }
}
