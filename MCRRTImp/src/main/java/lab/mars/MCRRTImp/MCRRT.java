package lab.mars.MCRRTImp;

import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D<WayPoint2D>> {


    public MCRRT(float deltaTime,
                 Provider<List<Obstacle>> obstacleProvider,
                 Provider<Attacker> aircraftProvider,
                 Provider<WayPoint2D> targetProvider,
                 Applier<Path2D<WayPoint2D>> pathApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
    }

    private Path2D<Cell2D> firstLevelRRT() {
        double R = aircraft.viewDistance();
        Vector2 aircraftPosition = aircraft.position();
        Grid2D gridWorld = new Grid2D((int) R, (int) R, 100, () -> aircraftPosition.cpy().add(new Vector2(-R, -R)));
        obstacles.add(new EyeSight(() -> aircraftPosition, () -> R));
        gridWorld.scan(obstacles);
        Vector2 gridAircraft = gridWorld.transformToCellCenter(aircraftPosition);
        double gridCellEdgeLength = gridWorld.cellSize();
        NTreeNode<Cell2D> root = new NTreeNode<>(new Cell2D(gridAircraft, gridCellEdgeLength));
        while (true) {
            Cell2D sampled = new Cell2D(gridWorld.sample(), gridCellEdgeLength);
            NTreeNode<Cell2D> nearestNode = root.findNearest(sampled, (c1, c2) -> c1.centroid.distance2(c2.centroid));
            Vector2 direction = sampled.centroid.cpy().subtract(nearestNode.getElement().centroid);
            Vector2 stepped = sampled.centroid.add(direction.normalize().scale(gridCellEdgeLength));
            if (gridWorld.check(stepped)) {
                continue;
            }
            sampled.centroid.set(gridWorld.transformToCellCenter(stepped));
            nearestNode.createChild(sampled);
            return null;
        }
        //TODO : need to complete grid world scan
    }

    @Override
    public Path2D<WayPoint2D> algorithm() {
        Path2D<WayPoint2D> path = new Path2D<>();
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
