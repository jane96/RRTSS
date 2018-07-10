package lab.mars.MCRRTImp;


import lab.mars.RRTBase.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D> {


    public MCRRT(
            Provider<List<Obstacle>> obstacleProvider,
            Provider<Attacker> aircraftProvider,
            Provider<WayPoint2D> targetProvider,
            Applier<Path2D> pathApplier) {
        super(obstacleProvider, aircraftProvider, targetProvider, pathApplier);
    }


    @Override
    public Path2D algorithm() {
        //TODO : implement solve method using random point generation
        throw new NotImplementedException();
    }
}
