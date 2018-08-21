package lab.mars.MCRRTImp.PolarBasedImp;

import javafx.scene.paint.Color;
import infrastructure.Vector2BasedImp.Vector2;
import infrastructure.PolarBasedImp.Bound;
import infrastructure.PolarBasedImp.Polar;
import infrastructure.ui.GUIBase;
import infrastructure.ui.Pencil;
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
