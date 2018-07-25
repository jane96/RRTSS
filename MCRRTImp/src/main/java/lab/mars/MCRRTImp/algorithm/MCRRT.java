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

    private Path2D<Cell2D> firstLevelPathCache = new Path2D<>();

    private Path2D<WayPoint2D> immutablePathCache = new Path2D<>();

    private Path2D<WayPoint2D> mutablePathCache = new Path2D<>();

    private Path2D<WayPoint2D> secondLevelPathCache = new Path2D<>();

    private PathGenerationConfiguration pathGenerationConfiguration;

    public static class PathGenerationConfiguration {
        public int immutablePathLength = 0;
        public int mutablePathLength = 0;
        public int thirdPathLength = 0;
        public int replanWaitTime = 10;

        public PathGenerationConfiguration(int immutablePathLength, int mutablePathLength, int thirdPathLength,  int replanWaitTime) {
            this.immutablePathLength = immutablePathLength;
            this.mutablePathLength = mutablePathLength;
            this.thirdPathLength = thirdPathLength;
            this.replanWaitTime = replanWaitTime;
        }
    }

    public MCRRT(float deltaTime,
                 float w, float h,
                 PathGenerationConfiguration configuration,
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
        this.pathGenerationConfiguration = configuration;
    }

    private Path2D<Cell2D> firstLevelRRT(Vector2 plannerStart, Vector2 plannerVelocity) {
        Grid2D gridWorld;
        int timeScalar = 50;
        NTreeNode<Cell2D> pathRoot;
        double gridCellEdgeLength;
        Vector2 targetPositionInGridWorld;
        long startTime = System.currentTimeMillis();
        while (true) {
            List<Transform> transforms = aircraft.simulateKinetic(plannerStart, plannerVelocity, timeScalar);
            int scaleBase = (int) transforms.get(0).position.distance(plannerStart);
            gridWorld = new Grid2D((int) width, (int) height, scaleBase);
            gridWorld.scan(obstacles);
            Vector2 gridAircraft = gridWorld.transformToCellCenter(plannerStart);
            gridCellEdgeLength = gridWorld.cellSize();
            pathRoot = new NTreeNode<>(new Cell2D(gridAircraft, gridCellEdgeLength));
            targetPositionInGridWorld = gridWorld.findNearestGridCenter(target.origin);
            grid2DApplier.apply(gridWorld);
            if (targetPositionInGridWorld == null && timeScalar != 5) {
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
                    System.out.println(System.currentTimeMillis() - startTime + "ms");
                    return cellPath;
                }
                step_count++;
            }
            if (nearest != null) {
                List<Cell2D> path = pathRoot.findTrace(nearest);
                Path2D<Cell2D> ret = new Path2D<>();
                path.forEach(e -> ret.add(new Cell2D(e.centroid, e.edgeLength)));
            }
            timeScalar -= 1;
            if (timeScalar <= 5) {
                timeScalar = 5;
            }
        }

    }

    private Path2D<WayPoint2D> secondLevelRRT(Path2D<Cell2D> areaPath, Vector2 start, Vector2 v, int count) {
        Path2D<WayPoint2D> ret = new Path2D<>();
        NormalDistribution N01 = new NormalDistribution(0, 1);
        double rotationLimitsOnOneSide = aircraft.rotationLimits() / 2;
        int deadEndCount = 0;
        int startIdx = 0;
        for (Cell2D area : areaPath) {
            if (area.centroid.distance2(start) <= area.edgeLength * area.edgeLength) {
                startIdx = areaPath.indexOf(area);
            }
        }
        for (int s = startIdx; s < areaPath.size() ; s++) {
            Cell2D area = areaPath.get(s);
            while (start.distance(area.centroid) >= area.edgeLength) {
                Map<Integer, Double> comparableMap = new HashMap<>();
                List<Transform> transforms = aircraft.simulateKinetic(start, v, deltaTime);
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
                    int selectedIndex = 0;
                    for (Map.Entry<Integer, Double> entry : comparableMap.entrySet()) {
                        sum += entry.getValue();
                        if (sum >= probability) {
                            int idx = entry.getKey();
                            selected = transforms.get(idx);
                            selectedIndex = idx;
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
                    if (safeTransform) {
                        ret.add(new WayPoint2D(selected.position, selected.velocity.len(), selected.velocity, selectedIndex));
                        start = selected.position.cpy();
                        v = selected.velocity.cpy().normalize().scale(3);
                        if (ret.size() == count) {
                            return ret;
                        }
                        break;
                    }
                    if (accessedTransforms.size() == transforms.size()) {
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
                        deadEndCount++;
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
        firstLevelPathCache = firstLevelRRT(aircraft.position(), aircraft.velocity());
        firstLevelApplier.apply(firstLevelPathCache);
        secondLevelPathCache = secondLevelRRT(firstLevelPathCache, aircraft.position(), aircraft.velocity(),
                pathGenerationConfiguration.immutablePathLength + pathGenerationConfiguration.mutablePathLength + pathGenerationConfiguration.thirdPathLength);
        pathApplier.apply(secondLevelPathCache);
        int timeCounter = 0;
        while (true) {
            if (firstLevelPathCache.size() <= 2) {
                if (firstLevelPathCache.size() == 0) {
                    firstLevelPathCache = firstLevelRRT(aircraft.position(), aircraft.velocity());
                } else {
                    WayPoint2D currentLast = secondLevelPathCache.end();
                    firstLevelPathCache = firstLevelRRT(currentLast.origin, currentLast.velocity);
                }
                firstLevelApplier.apply(firstLevelPathCache.cpy());
            }
            if (timeCounter >= pathGenerationConfiguration.replanWaitTime) {
                WayPoint2D currentLast = secondLevelPathCache.end();
                Path2D<WayPoint2D> newPart = secondLevelRRT(firstLevelPathCache, currentLast.origin, currentLast.velocity, timeCounter);
                newPart.forEach(secondLevelPathCache::add);
                pathApplier.apply(secondLevelPathCache.cpy());
                timeCounter = 0;
            }
            for (int i = 0; i < secondLevelPathCache.size(); i++) {

                WayPoint2D current = secondLevelPathCache.get(i);
                Vector2 currentPosition = aircraft.position();
                if (currentPosition.distance2(current.origin) <= current.radius * current.radius) {
                    for (int c = 0; c <= i;c ++) {
                        secondLevelPathCache.removeAt(0);
                    }
                    timeCounter += i + 1;
                }
            }
        }
    }
}
