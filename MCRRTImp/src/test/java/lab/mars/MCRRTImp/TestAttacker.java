package lab.mars.MCRRTImp;

import javafx.application.Application;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.junit.Test;

import java.util.List;

public class TestAttacker {
    @Test
    public void test() {

    }

    public static class TestTransform extends GUIBase {
        @Override
        void draw(Pencil pencil) {

            Attacker attacker = new Attacker(new Vector2(5,1), new Vector2(0, 250), 180, 30, 200, 100, 2);
            List<Transform> transforms = attacker.simulateKinetic(1);
            Vector2 origin = attacker.position();
            pencil.scale(100).filled().color(Color.YELLOWGREEN).circle(origin, 1);
            for (Transform t : transforms) {
                Vector2 position = t.position;
                Vector2 direction = t.velocity;
                Vector2 transformed = position.cpy().add(direction);
                pencil.stroked(2).color(Color.BLACK).line(position, transformed).color(Color.RED).line(position, origin);
            }
        }
    }
}
