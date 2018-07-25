package lab.mars.MCRRTImp.algorithm;

import lab.mars.MCRRTImp.infrastructure.*;
import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.*;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D<WayPoint2D>> {

    private float width;

    private float height;

    private Applier<Grid2D> grid2DApplier;

    private Applier<Path2D<Cell2D>> firstLevelApplier;

    public MCRRT(float deltaTime,
                 float w, float h,
                 Provider<List<Obstacle<Vector2>>> obstacleProvider,
                 Provider<Attacker> aircraftProvider,
                 Provider<WayPoint2D> targetProvider,
                 Applier<Path2D<WayPoint2D>> pathApplier,
                 Applier<Path2D<Cell2D>> firstLevelApplier,
                 Applier<Grid2D> grid2DApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.width = w;
        this.height = h;
        this.grid2DApplier = grid2DApplier;
        this.firstLevelApplier = firstLevelApplier;
    }

    private Path2D<Cell2D> firstLevelRRT() {
        Vector2 aircraftPosition = aircraft.position();
        Vector2 aircraftVelocity = aircraft.velocity();
        Grid2D gridWorld;
        int timeScalar = 50;
        NTreeNode<Cell2D> pathRoot;
        double gridCellEdgeLength;
        Vector2 targetPositionInGridWorld;
        long startTime = System.currentTimeMillis();
        while (true) {
            List<Transform> transforms = simulateKinetic(aircraftPosition, aircraftVelocity, timeScalar);
            int scaleBase = (int) transforms.get(0).position.distance(aircraftPosition);
            gridWorld = new Grid2D((int) width, (int) height, scaleBase);
            gridWorld.scan(obstacles);
            Vector2 gridAircraft = gridWorld.transformToCellCenter(aircraftPosition);
            gridCellEdgeLength = gridWorld.cellSize();
            pathRoot = new NTreeNode<>(new Cell2D(gridAircraft, gridCellEdgeLength));
            targetPositionInGridWorld = gridWorld.findNearestGridCenter(target.origin);
            grid2DApplier.apply(gridWorld);
            if (timeScalar == 5) {
                throw new RuntimeException("cannot solve the map");
            }
            if (targetPositionInGridWorld == null) {
                timeScalar -= 1;
                if (timeScalar <= 5) {
                    timeScalar = 5;
                }
                continue;
            }
            int step_count = 0;
            Cell2D nearest = null;
            double distance = Double.MAX_VALUE;
            while (step_count < gridWorld.rowCount * gridWorld.columnCount) {
                Cell2D sampled = new Cell2D(gridWorld.sample(), gridCellEdgeLength);
                if (MathUtil.random(0, 1) < 0.3) {
                    sampled = new Cell2D(target.origin.cpy(), gridCellEdgeLength);
                }
                NTreeNode<Cell2D> nearestNode = pathRoot.findNearest(sampled, (c1, c2) -> c1.centroid.distance2(c2.centroid));
                Vector2 direction = sampled.centroid.cpy().subtract(nearestNode.getElement().centroid);
                Vector2 stepped = nearestNode.getElement().centroid.cpy().add(direction.normalize().scale(gridCellEdgeLength));
                if (gridWorld.check(stepped)) {
                    step_count++;
                    continue;
                }
                sampled.centroid.set(gridWorld.transformToCellCenter(stepped));
                nearestNode.createChild(sampled);
                double dis = target.origin.distance2(stepped);
                if (dis < distance) {
                    distance = dis;
                    nearest = sampled;
                }
                if (sampled.centroid.epsilonEquals(targetPositionInGridWorld, 0.001)) {
                    List<Cell2D> path = pathRoot.findTrace(sampled);
                    Path2D<Cell2D> cellPath = new Path2D<>();
                    path.forEach(cellPath::add);
                    cellPath.ended = true;
                    firstLevelApplier.apply(cellPath);
                    System.out.println(System.currentTimeMillis() - startTime + "ms");
                    return cellPath;
                }
                step_count++;
            }
            if (nearest != null) {
                List<Cell2D> path = pathRoot.findTrace(nearest);
                Path2D<Cell2D> ret = new Path2D<>();
                path.forEach(e -> ret.add(new Cell2D(e.centroid, e.edgeLength)));
                firstLevelApplier.apply(ret);
            }
            timeScalar -= 1;
            if (timeScalar <= 5) {
                timeScalar = 5;
            }
        }

    }


    private double simulateVelocity(double velocity, double angle) {
        return velocity * (1 - Math.abs(angle) / aircraft.rotationLimits());
    }

    private List<Transform> simulateKinetic(Vector2 position, Vector2 velocity, double deltaTime) {
        List<Transform> ret = new ArrayList<>();
        double v = velocity.len();
        int sliceCount = 100;
        double rotationLimits = aircraft.rotationLimits();
        double graduation = aircraft.rotationGraduation();
        for (double i = 0; i < rotationLimits / 2; i += graduation) {
            double totalAngleRotated = i * deltaTime;
            double slicedAngleRotated = totalAngleRotated / sliceCount;
            Vector2 rotated = velocity.cpy();
            Vector2 translated = position.cpy();
            double newV = simulateVelocity(v, i);
            for (int c = 0; c < sliceCount; c++) {
                rotated.rotate(slicedAngleRotated);
                translated.add(rotated.cpy().normalize().scale(newV * deltaTime / sliceCount));
            }
            ret.add(new Transform(translated, rotated.normalize().scale(newV)));
        }
        for (double i = -graduation; i > -rotationLimits / 2; i -= graduation) {

            double totalAngleRotated = i * deltaTime;
            double slicedAngleRotated = totalAngleRotated / sliceCount;
            Vector2 rotated = velocity.cpy();
            Vector2 translated = position.cpy();
            double newV = simulateVelocity(v, i);
            for (int c = 0; c < sliceCount; c++) {
                rotated.rotate(slicedAngleRotated);
                translated.add(rotated.normalize().cpy().scale(newV * deltaTime / sliceCount));
            }
            ret.add(new Transform(translated, rotated.normalize().scale(newV)));
        }
        return ret;
    }


    private Path2D<WayPoint2D> secondLevelRRT(Path2D<Cell2D> areaPath) {
        Path2D<WayPoint2D> ret = new Path2D<>();
        NormalDistribution N01 = new NormalDistribution(0, 1);
        Vector2 start = aircraft.position().cpy();
        Vector2 v = aircraft.velocity().cpy();
        double rotationLimitsOnOneSide = aircraft.rotationLimits() / 2;
        int deadEndCount = 0;
        for (Cell2D area : areaPath) {
            while (start.distance(area.centroid) >= area.edgeLength) {
                Map<Integer, Double> comparableMap = new HashMap<>();
                List<Transform> transforms = simulateKinetic(start, v, 1);
                boolean outOfBound = false;
                for (int i = 0; i < transforms.size(); i++) {
                    Transform t = transforms.get(i);
                    Vector2 target = area.centroid.cpy().subtract(start).normalize();
                    Vector2 next = t.velocity.cpy().normalize();
                    double angle = target.angle(next);
                    if (angle > rotationLimitsOnOneSide) {
                        outOfBound = true;
                    }
                    comparableMap.put(i, angle);
                }
                if (!outOfBound) {
                    comparableMap.entrySet().forEach(e -> e.setValue(1 - N01.cumulativeProbability(e.getValue() / rotationLimitsOnOneSide * 2.58)));
                } else {
                    double minAngle = comparableMap.values().stream().min(Double::compareTo).get();
                    comparableMap.entrySet().forEach(e -> e.setValue(1 - N01.cumulativeProbability((e.getValue() - minAngle) / rotationLimitsOnOneSide * 2.58)));
                }
                double valueSum = 0;
                for (Double value : comparableMap.values()) {
                    valueSum += value;
                }
                final double _valueSum = valueSum;
                comparableMap.entrySet().forEach(e -> e.setValue(e.getValue() / _valueSum));
                Set<Integer> accessedTransforms = new HashSet<>();
                while (true) {
                    double probability = MathUtil.random(0, 1);
                    double sum = 0;
                    boolean safeTransform = true;
                    Transform selected = null;
                    for (Map.Entry<Integer, Double> entry : comparableMap.entrySet()) {
                        sum += entry.getValue();
                        if (sum >= probability) {
                            int idx = entry.getKey();
                            selected = transforms.get(idx);
                            accessedTransforms.add(idx);
                            for (Obstacle<Vector2> obs : obstacles) {
                                if (obs.contains(selected.position)) {
                                    safeTransform = false;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (selected == null) {
                        throw new RuntimeException("error");
                    }
                    if (safeTransform) {
                        ret.add(new WayPoint2D(selected.position, selected.velocity.len(), selected.velocity));
                        start = selected.position.cpy();
                        v = selected.velocity.cpy().normalize().scale(3);
                        break;
                    }
                    if (accessedTransforms.size() == transforms.size()) {
                        pathApplier.apply(ret);
                        for (int i = 0; i < deadEndCount; i++) {
                            if (ret.size() != 0) {
                                ret.removeAt(ret.size() - 1);
                            }
                        }
                        if (ret.size() == 0) {
                            start = aircraft.position();
                            v = aircraft.velocity();
                        } else {
                            WayPoint2D last = ret.end();
                            start = last.origin;
                            v = last.velocity;
                        }
                        accessedTransforms.clear();
                        deadEndCount ++;
                        if (ret.size() == 0) {
                            deadEndCount = 0;
                        }
                        break;
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public Path2D<WayPoint2D> algorithm() {
        Path2D<Cell2D> areaPath = firstLevelRRT();
        Path2D<WayPoint2D> ret = secondLevelRRT(areaPath);
        System.out.println("finished");
        return ret;
    }
}
