package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Applier;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;
import lab.mars.RRTBase.RRT;

import java.util.HashMap;
import java.util.List;

public class RLRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D<WayPoint2D>> {

    public RLRRT(float deltaTime, Provider<List<Obstacle<Vector2>>> obstacleProvider, Provider<Attacker> aircraftProvider, Provider<WayPoint2D> targetProvider, Applier<Path2D<WayPoint2D>> pathApplier) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
    }

    class GridHashMap<K, V> extends HashMap<K, V> {
        @Override
        public V put(K key, V value) {
            this.values().forEach(v -> {

            });
            return super.put(key, value);
        }
    }

    @Override
    public Path2D<WayPoint2D> algorithm() {
        return null;
    }
}
