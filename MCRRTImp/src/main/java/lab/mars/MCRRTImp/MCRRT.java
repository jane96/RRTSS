package lab.mars.MCRRTImp;


import lab.mars.RRTBase.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D> {


    public MCRRT(float deltaTime,
                 Provider<List<Obstacle>> obstacleProvider,
                 Provider<Attacker> aircraftProvider,
                 Provider<WayPoint2D> targetProvider,
                 Applier<Path2D> pathApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
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
        do {
            double randomR = MathUtil.random(0, R);
            double randomTheta = MathUtil.random(-alpha, alpha);
            Vector2 SA = direction.cpy().rotate(randomTheta).normalize().scale(randomR);
            Vector2 A = SA.cpy().add(position);
            boolean collide = false;
            for (Obstacle obs : obstacles) {
                if (obs.contains(A)) {
                    collide = true;
                    break;
                }
            }
            if (collide) {
                continue;
            }
            Stream<Map.Entry<Vector2, Double>> sorted =
                    availableDirections.stream().collect(Collectors.toMap(dir -> dir, SA::angle))
                            .entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue));

        } while (Way.size() != n);
        return null;
    }
}
