package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class World {

    private static List<Attacker> _attacker;

    private static List<Obstacle<Vector2>> _obstacles;

    private static Set<Obstacle<Vector2>> _seenObstacles = new ConcurrentSkipListSet<>();

    public static void initialize(List<Attacker> attacker, List<CircleObstacle> obstacles) {
        _attacker = attacker;
        _obstacles = new ArrayList<>(obstacles);
    }

    public static List<Obstacle<Vector2>> allObstacles() {
        return _obstacles;
    }

    public static List<Obstacle<Vector2>> seenObstacles()  {
        return new ArrayList<>(_seenObstacles);
    }

    public static void recordSeenObstacle(List<Obstacle<Vector2>> obstacles) {
        obstacles.forEach(_seenObstacles::add);
    }

    public static List<Attacker> attacker() {
        return _attacker;
    }

}