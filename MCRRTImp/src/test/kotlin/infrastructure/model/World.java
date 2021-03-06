package infrastructure.model;


import lab.mars.MCRRTImp.base.Obstacle;
import lab.mars.MCRRTImp.base.Vector;
import lab.mars.MCRRTImp.model.Space;

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