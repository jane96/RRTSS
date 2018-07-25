package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.model.Attacker;
import lab.mars.MCRRTImp.model.CircleObstacle;
import lab.mars.MCRRTImp.model.Path2D;
import lab.mars.MCRRTImp.model.WayPoint2D;
import lab.mars.MCRRTImp.model.Cell2D;
import lab.mars.MCRRTImp.model.Grid2D;
import lab.mars.MCRRTImp.model.Vector2;
import lab.mars.RRTBase.Obstacle;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class World {

    public Attacker attacker;

    public List<CircleObstacle> obstacles;

    public WayPoint2D target;

    public Path2D<WayPoint2D> path;

    public Path2D<Cell2D> areaPath;

    public Grid2D gridWorld;

    private Queue<Object> applierQueue = new ConcurrentLinkedQueue<>();

    public World(Attacker attacker, List<CircleObstacle> obstacles, WayPoint2D target) {
        this.attacker = attacker;
        this.obstacles = obstacles;
        this.target = target;
    }

    public List<Obstacle<Vector2>> allObstacles() {
        return new ArrayList<>(obstacles);
    }

    public Attacker attacker() {
        return attacker;
    }

    public WayPoint2D target() {
        return target;
    }

    public void applyPath(Path2D<WayPoint2D> path) {
        applierQueue.offer(path);
    }

    public void applyAreaPath(Path2D<Cell2D> path) {
        applierQueue.offer(path);
    }

    public void applyGrid(Grid2D grid) {
        applierQueue.offer(grid);
    }

    public void requestUpdate() {

        if (!applierQueue.isEmpty()) {
            Object obj = applierQueue.poll();
            if (obj instanceof Path2D) {
                if (((Path2D) obj).end() instanceof  WayPoint2D) {
                    path = ((Path2D<WayPoint2D>) obj);
                } else {
                    areaPath = ((Path2D<Cell2D>)obj);
                }
            } else if (obj instanceof Grid2D) {
                this.gridWorld = ((Grid2D) obj);
            }
        }
    }
}