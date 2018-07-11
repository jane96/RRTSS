package lab.mars.MCRRTImp;


import lab.mars.RRTBase.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D> {


    public MCRRT(
            Provider<List<Obstacle>> obstacleProvider,
            Provider<Attacker> aircraftProvider,
            Provider<WayPoint2D> targetProvider,
            Applier<Path2D> pathApplier
    ) {
        super(obstacleProvider, aircraftProvider, targetProvider, pathApplier);
    }


    @Override
    public Path2D algorithm() {
        Path2D path = new Path2D();
        double R = aircraft.viewDistance();
        double totalRotationAngle = aircraft.rotationLimits();
        double alpha = totalRotationAngle / 2.0;
        double n = aircraft.rotationGraduation();
        Vector2 position = aircraft.position();
        Vector2 direction = aircraft.velocity().cpy().normalize();
        List<Vector2> availableDirections = new ArrayList<>();
        for (double a = -alpha; a < alpha; alpha += totalRotationAngle / n) {
            Vector2 rotation = direction.cpy().rotate(a);
            availableDirections.add(rotation);
        }
        List<Vector2> Way = new ArrayList<>();
        for (int i = 0; i < n; ) {
            double randomR = MathUtil.random(0, R);
            double randomTheta = MathUtil.random(-alpha, alpha);
            Vector2 SA = direction.cpy().rotate(randomTheta).normalize().scale(randomR);
            List<Double> sorted = availableDirections.stream().collect(Collectors.toMap(dir -> dir, dir -> SA.angle((Vector2) dir)));
            i++;
        }
        return null;
    }
}
