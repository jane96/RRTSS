package infrastructure.Vector2BasedImp;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import infrastructure.model.Attacker;
import infrastructure.model.World;
import infrastructure.ui.AircraftSimulator;
import lab.mars.RRTBase.*;
import lab.mars.MCRRTImp.model.*;
import lab.mars.MCRRTImp.algorithm.MCRRT;
import infrastructure.ui.GUIBase;
import infrastructure.ui.Pencil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class JavaFXUI extends GUIBase {

    private Stage stage;
    private World<Vector2> world;
    private RRT rrt;

    private int mapWidth = 1920;

    private int mapHeight = 1080;

    private List<Obstacle<Vector2>> randomObstacles(int count, double redZoneRadius, double maxRadius, Vector2... redZoneOrigins) {
        List<Obstacle<Vector2>> obstacles = new ArrayList<>();
        for (int i = 0; i < count; ) {
            double x = MathUtil.random(0, mapWidth);
            double y = MathUtil.random(0, mapHeight);
            double radius = MathUtil.random(0, maxRadius);
            Vector2 origin = new Vector2(x, y);
            boolean flag = false;
            for (Vector2 redZone :
                    redZoneOrigins) {
                if (redZone.distance(origin) < radius + redZoneRadius) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                continue;
            }
            i++;
            obstacles.add(new CircleObstacle(x, y, radius));
            System.out.println(String.format("obstacles.translate(new CircleObstacle(%f, %f, %f));", x, y, radius));
        }
        return obstacles;
    }


    private void drawTarget(Attacker<Vector2> attacker, Pencil pencil) {
        DimensionalWayPoint<Vector2> target = attacker.target();
        pencil.filled().color(Color.YELLOWGREEN).circle(target.origin, target.radius);
    }

    private void drawUAV(Attacker<Vector2> attacker, Pencil pencil) {
        pencil.filled().color(new Color(1, 0, 0, 0.3)).circle(attacker.position(), 10);
    }

    private void drawObstacles(Pencil pencil) {
        world.allObstacles().forEach(obs -> {
            if (obs instanceof CircleObstacle) {
                CircleObstacle circleObstacle = (CircleObstacle) obs;
                pencil.filled().color(Color.LIGHTBLUE).circle(circleObstacle.origin, circleObstacle.radius);
                pencil.stroked(1).color(Color.DARKBLUE).circle(circleObstacle.origin, circleObstacle.radius);
            }
        });
        pencil.stroked(5).color(Color.BLUE).rect(new Vector2(mapWidth / 2f, mapHeight / 2f), new Vector2(mapWidth, mapHeight));
    }

    private void drawLeaves(Attacker<Vector2> attacker, Pencil pencil) {
        List<NTreeNode<DimensionalWayPoint<Vector2>>> leaves = attacker.getLeaves();
        leaves.forEach(leaf -> {
            DimensionalWayPoint<Vector2> wayPoint = leaf.getElement();
            pencil.filled().color(Color.RED).circle(wayPoint.origin, 1);
        });
    }

    private void drawAreaPath(Attacker<Vector2> attacker, Pencil pencil) {
        Queue<DimensionalPath<DimensionalWayPoint<Vector2>>> paths = attacker.areaPath();
        if (paths.size() != 0) {
            DimensionalPath<DimensionalWayPoint<Vector2>> path;
            if (paths.size() != 1) {
                path = paths.poll();
            } else {
                path = paths.peek();
            }
            Vector2 cellSize = new Vector2(path.get(0).radius, path.get(0).radius);
            for (DimensionalWayPoint<Vector2> wayPoint : path) {
                pencil.filled().color(Color.LIGHTBLUE).rect(wayPoint.origin.cpy().translate(new Vector2(cellSize.x() / 2, cellSize.y() / 2)), cellSize);
                pencil.stroked(1.0).color(Color.BLUE).rect(wayPoint.origin.cpy().translate(new Vector2(cellSize.x() / 2, cellSize.y() / 2)), cellSize);
            }
        }
    }

    private void drawPath(Attacker<Vector2> attacker, Pencil pencil) {

        DimensionalPath<DimensionalWayPoint<Vector2>> path = attacker.actualPath();
        if (path != null && path.size() != 0) {
            Vector2 last = attacker.position().cpy();
            int counter = 0;
            for (int i = 0; i <path.size(); i++) {
                DimensionalWayPoint<Vector2> wayPoint2D = path.get(i);
                Color color = Color.ORANGE;
//                if (counter < configuration.immutablePathLength) {
//                    color = Color.BLACK;
//                } else if (counter < configuration.mutablePathLength) {
//                    color = Color.RED;
//                }
                if (counter % 2 == 0) {
                    color = Color.BLACK;
                }
                pencil.stroked(2 * scaleBase).color(color).line(last.cpy(), last.set(wayPoint2D.origin));
                counter++;
        }

        }
    }

    private Attacker<Vector2> leftUpAttacker() {
        Vector2 attackerPosition = new Vector2(5, 5);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 5, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    private Attacker<Vector2> middleLeftAttacker() {
        Vector2 attackerPosition = new Vector2(5, 925);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 30, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    private Attacker<Vector2> rightUpAttacker() {
        Vector2 attackerPosition = new Vector2(1955, 5);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 30, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    private Attacker<Vector2> rightUpMiddleAttacker() {
        Vector2 attackerPosition = new Vector2(1125, 265);
        Vector2 targetPosition = new Vector2(1200, 780);
        DimensionalWayPoint<Vector2> target = new DimensionalWayPoint<>(targetPosition, 5, new Vector2());
        Attacker<Vector2> attackerLeftUp = new Attacker<>(attackerPosition, new Vector2(1, 1).normalize().scale(4.1666667), 10, 30, 200, 50, 5, target, world.area(), world::allObstacles);
        attackerLeftUp.setDesignatedTarget(target);
        return attackerLeftUp;
    }

    public void buildWorld() {
        List<Obstacle<Vector2>> circleObstacles = randomObstacles(20, 20, 50, new Vector2(5, 5), new Vector2(1200, 780));
        List<Attacker<Vector2>> attackers = new ArrayList<>();
        world = new World<>(attackers, circleObstacles, new Space<>(new Vector2(mapWidth, mapHeight), new Vector2()));
        for (int i = 0; i < 1; i++) {
            attackers.add(leftUpAttacker());
        }
//        attackers.add(middleLeftAttacker());
//        attackers.add(rightUpAttacker());
//        attackers.add(rightUpMiddleAttacker());
    }

    public JavaFXUI() {
        buildWorld();
    }

    @Override
    protected void draw(Pencil pencil) {
//        for (Attacker<Vector2> attacker : world.attacker()) {
//            drawGrid(attacker, pencil);
//        }
        drawObstacles(pencil);
        for (Attacker<Vector2> attacker : world.attacker()) {
            drawAreaPath(attacker, pencil);
            drawPath(attacker, pencil);
            drawTarget(attacker, pencil);
            drawUAV(attacker, pencil);
            drawLeaves(attacker, pencil);
        }
    }

    private void drawGrid(Attacker<Vector2> attacker, Pencil pencil) {
        ScaledGrid<Vector2> scaledGrid = attacker.gridWorld();
        if (scaledGrid == null) {
            return;
        }
        pencil.stroked(5).rect(new Vector2(mapWidth / 2f, mapHeight / 2f), new Vector2(mapWidth, mapHeight));
        Vector2 cellSize = scaledGrid.cellSize();
        scaledGrid.forEach(v -> {
            Color color;
            if (scaledGrid.check(v)) {
                color = Color.RED;
            } else {
                color = new Color(0.2, 0.7, 0, 1);
            }
            Vector2 rectCentroid = new Vector2(v.x() + cellSize.x() / 2, v.y() + cellSize.y() / 2);
            pencil.filled().color(color).rect(rectCentroid, cellSize);
            pencil.stroked(0.5).color(Color.BLUE).rect(rectCentroid, cellSize);
        });
    }


    @Override
    protected void initializeComponents(Stage primaryStage, Scene scene, Pane root, Canvas canvas) {
        super.initializeComponents(primaryStage, scene, root, canvas);
        this.height = mapHeight;
        this.width = mapWidth;
        primaryStage.setTitle("Test Flight");
        stage = primaryStage;
        ContextMenu menu = new ContextMenu();
        MenuItem solve = new MenuItem("Start");
        solve.setOnAction(event -> rrt.solve(false));
        menu.getItems().add(solve);
        canvas.setOnContextMenuRequested(event -> menu.show(canvas, event.getScreenX(), event.getScreenY()));
        solve.setOnAction(event -> {
            world.attacker().forEach(Attacker::startAlgorithm);
        });

    }

}
