package lab.mars.MCRRTImp.PolarBasedImp;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import lab.mars.MCRRTImp.infrastructure.ui.GUIBase;
import lab.mars.MCRRTImp.infrastructure.ui.Pencil;
import lab.mars.RRTBase.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class TestBound extends GUIBase {

    private Bound bound = new Bound(100, 100);

    private List<Polar> points = new ArrayList<>();

    @Override
    protected void draw(Pencil pencil) {
        Polar polar = new Polar(MathUtil.random(0, 200), MathUtil.random(0, 360));
        points.add(polar);
        for (Polar point : points) {
            Vector2 origin = new Vector2(1, 0);
            origin.rotate(point.theta()).normalize().scale(point.r());
            if (bound.contains(point)) {
                pencil.filled().color(Color.RED).circle(origin, 1);
            } else {
                pencil.filled().color(Color.BLUE).circle(origin, 1);
            }

        }
    }
}
