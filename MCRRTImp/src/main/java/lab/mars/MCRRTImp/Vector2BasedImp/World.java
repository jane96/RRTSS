package lab.mars.MCRRTImp.Vector2BasedImp;

import lab.mars.MCRRTImp.Vector2BasedImp.Attacker;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Space;
import lab.mars.RRTBase.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class World<V extends Vector<V>> {


    private Space<V> area;

    private List<Attacker<V>> _attacker;

    private List<Obstacle<V>> _obstacles;

    private Set<Obstacle<V>> _seenObstacles = new ConcurrentSkipListSet<>();

    public World(List<Attacker<V>> attacker, List<Obstacle<V>> obstacles, Space<V> area) {
        _attacker = attacker;
        _obstacles = new ArrayList<>(obstacles);
        this.area = area;
    }

    public Space<V> area() {
        return area;
    }

    public List<Obstacle<V>> allObstacles() {
        return _obstacles;
    }

    public List<Obstacle<V>> seenObstacles()  {
        return new ArrayList<>(_seenObstacles);
    }

    public void recordSeenObstacle(List<Obstacle<V>> obstacles) {
        _seenObstacles.addAll(obstacles);
    }

    public List<Attacker<V>> attacker() {
        return _attacker;
    }

}