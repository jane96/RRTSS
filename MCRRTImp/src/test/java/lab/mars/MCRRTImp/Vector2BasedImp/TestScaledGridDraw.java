package lab.mars.MCRRTImp.Vector2BasedImp;

import javafx.application.Application;
import javafx.scene.paint.Color;
import infrastructure.Vector2BasedImp.CircleObstacle;
import infrastructure.Vector2BasedImp.Vector2;
import infrastructure.ui.GUIBase;
import infrastructure.ui.Pencil;
import lab.mars.MCRRTImp.model.GridCell;
import lab.mars.MCRRTImp.model.ScaledGrid;
import lab.mars.RRTBase.MathUtil;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Space;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestScaledGridDraw extends GUIBase {

    static int mapWidth = 1280;

    static int mapHeight = 800;

    @Override
    protected void draw(Pencil pencil) {
        pencil.stroked(5).rect(new Vector2(mapWidth / 2, mapHeight / 2), new Vector2(mapWidth, mapHeight));
        Vector2 cellSize = scaledGrid.cellSize();
        scaledGrid.forEach(v -> {
            Color color;
            if (scaledGrid.check(v)) {
                color = Color.RED;
            } else {
                color = new Color(0.2,0.7, 0, 1);
            }
            Vector2 rectCentroid = new Vector2(v.x() + cellSize.x() / 2, v.y() + cellSize.y() / 2);
            pencil.filled().color(color).rect(rectCentroid, cellSize);
            pencil.stroked(0.5).color(Color.BLUE).rect(rectCentroid, cellSize);
        });
        obstacles.forEach(obs -> {
            CircleObstacle circleObstacle = (CircleObstacle)obs;
            pencil.filled().color(Color.LIGHTBLUE).circle(circleObstacle.origin, circleObstacle.radius);
        });
    }

    private static List<Obstacle<Vector2>> generateRandomObstacle(int count, Space<Vector2> space, double radiusUpper) {
        List<Obstacle<Vector2>> obstacles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Vector2 sampled = space.sample();
            double radius = MathUtil.random(0, radiusUpper);
            obstacles.add(new CircleObstacle(sampled.x(), sampled.y(), radius));
        }
        return obstacles;
    }

    static ScaledGrid<Vector2> scaledGrid;
    static Space<Vector2> originalSpace;
    static List<Obstacle<Vector2>> obstacles;

    public static void testCreate() {
        originalSpace = new Space<>(new Vector2(mapWidth, mapHeight), new Vector2(0,0));
        scaledGrid = new ScaledGrid<>(originalSpace, 20);
    }

    public static void testScan() {
        obstacles = generateRandomObstacle(200, originalSpace, 30);
        long start = System.currentTimeMillis();
        scaledGrid.scan(obstacles);
        System.out.println(System.currentTimeMillis() - start + "ms");
        Set<String> gridString = new HashSet<>();
        int count = 0;
        for (GridCell<Vector2> c : scaledGrid.grid) {
            count++;
            gridString.add(c.toString());
        }
        assert count == gridString.size();
    }

    public static void main(String[] args) {
        testCreate();
        testScan();
        Application.launch(args);
    }
}
