package lab.mars.MCRRTImp;

import javafx.scene.canvas.GraphicsContext;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.RRT;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestMCRRT {

    @Test
    public void testListMapSortedOrder() {
        List<Vector2> testCase = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testCase.add(new Vector2(MathUtil.random(0, 100), MathUtil.random(0, 100)));
        }
        Vector2 comparator = new Vector2(1, 0);
        Stream<Map.Entry<Vector2, Double>> map = testCase.stream().
                collect(Collectors.toMap(dir -> dir, dir -> dir.angle(comparator))).
                entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue));
        map.forEach(e -> System.out.println(e.getKey() + ", " + e.getValue()));
    }


    class TestWorld {

        Attacker attacker;

        Bound bound;

        List<CircleObstacle> obstacles;

        WayPoint2D target ;

        Path2D<WayPoint2D> path;

        public TestWorld(Attacker attacker, Bound bound, List<CircleObstacle> obstacles, WayPoint2D target) {
            this.attacker = attacker;
            this.bound = bound;
            this.obstacles = obstacles;
            this.target = target;
        }

        public List<Obstacle> allObstacles() {
            List<Obstacle> ret = new ArrayList<>(obstacles);
            ret.add(bound);
            return ret;
        }

        public Attacker attacker() {
            return attacker;
        }

        public WayPoint2D target() {
            return target;
        }

        public void applyPath(Path2D<WayPoint2D> path) {
            this.path = path;
            
        }

        public void draw(GraphicsContext pencil) {

        }
    }

    TestWorld world;

    @Before
    public void buildWorld() {
        double worldHeight = 400;
        double worldWidth = 500;
        Vector2 attackerPosition = new Vector2(5,5);
        Vector2 targetPosition = new Vector2(300, 300);
        Attacker attacker = new Attacker(attackerPosition, new Vector2(1, 1).normalize().scale(5), 10, 30, 200, 50, 2);
        Bound bound = new Bound(worldWidth, worldHeight);
        WayPoint2D target = new WayPoint2D(targetPosition, 5);
        List<CircleObstacle> circleObstacles = new ArrayList<>();
        for (int i = 0; i < 100; ) {
            double x = MathUtil.random(0, 500);
            double y = MathUtil.random(0, 400);
            Vector2 centroid = new Vector2(x, y);
            double radius = MathUtil.random(0, 10);
            double distance2 = centroid.distance2(attackerPosition);
            if (distance2 + radius < attacker.safeDistance()) {
                continue;
            }
            distance2 = centroid.distance2(targetPosition);
            if (distance2 + radius < target.radius) {
                continue;
            }
            i ++;
            CircleObstacle obstacle = new CircleObstacle(x, y, radius);
            System.out.println("circleObstacles.add(new CircleObstacle(" + x + "," + y + "," + radius + "));");
            circleObstacles.add(obstacle);

        }
        world = new TestWorld(attacker, bound, circleObstacles, target);
    }

    @Test
    public void testFirstLevelRRT() {
        RRT rrt = new MCRRT(1 / 30.0f, world::allObstacles, world::attacker, world::target, world::applyPath);
        rrt.solve(true);
    }
}
