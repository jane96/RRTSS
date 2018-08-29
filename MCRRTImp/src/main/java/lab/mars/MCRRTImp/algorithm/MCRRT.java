package lab.mars.MCRRTImp.algorithm;

import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;
import lab.mars.RRTBase.Vector;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;
import java.util.stream.Collectors;

public class MCRRT<V extends Vector<V>> extends RRT<SimulatedVehicle<V>, V, DimensionalWayPoint<V>, DimensionalPath<DimensionalWayPoint<V>>> {

    private final Provider<Integer> pathLengthProvider;
    private final Applier<Map<Integer, Double>> probabilityModifier;
    public boolean verbose;
    private Space<V> spaceRestriction;

    private PathSampler<V> pathSampler;

    public interface PathSampler<V extends Vector<V>> {

        DimensionalPath<DimensionalWayPoint<V>> sample(SimulatedVehicle<V> attacker, double timeScalar);
    }

    private void verbose(String message) {
        if (verbose) {
            System.out.println(message);
        }
    }

    private long startTime = 0;

    private boolean secondLevelTimeOut = false;

    private DimensionalPath<DimensionalWayPoint<V>> defaultSampler(SimulatedVehicle<V> vehicle, double deltaTime) {
        int trials = 0;
        while (trials < 1) {
            trials++;
            startTime = System.currentTimeMillis();
            DimensionalPath<DimensionalWayPoint<V>> areaPathCache = firstLevelRRT(this.vehicle.position(), this.vehicle.velocity());
            if (areaPathCache == null) {
                verbose("algorithm failed reason : first level timed out");
                continue;
            }
            this.vehicle = aircraftProvider.provide();
            V start = this.vehicle.position();
            V velocity = this.vehicle.velocity();
            DimensionalPath<DimensionalWayPoint<V>> newPath = secondLevelRRT(areaPathCache, start, velocity, pathLengthProvider.provide());
            if (secondLevelTimeOut) {
                secondLevelTimeOut = false;
                verbose("algorithm failed reason : second level timed out");
                continue;
            }
            return newPath;
        }
        return null;
    }


    private DimensionalPath<DimensionalWayPoint<V>> firstLevelRRT(V plannerStart, V plannerVelocity) {
        ScaledGrid<V> gridWorld;
        double timeScalar = deltaTime * 20;
        NTreeNode<DimensionalWayPoint<V>> pathRoot;
        double gridCellEdgeLength;
        while (true) {
            List<Transform<V>> transforms = vehicle.simulateKinetic(plannerVelocity, timeScalar);
            int scaleBase = (int) transforms.stream().max(Comparator.comparingDouble(o -> o.position.len())).get().position.len();
            transforms.forEach(transform -> transform.position.translate(plannerStart));
            gridWorld = new ScaledGrid<>(spaceRestriction, scaleBase);
            long s = System.currentTimeMillis();
            verbose("1st level : start grid world scan");
            gridWorld.scan(obstacles);
            verbose("1st level : grid scan completed in " + (System.currentTimeMillis() - s) + "ms");
            V gridAircraft = gridWorld.formalize(plannerStart);
            gridCellEdgeLength = gridWorld.cellSize().len();
            pathRoot = new NTreeNode<>(new DimensionalWayPoint<>(gridAircraft, gridCellEdgeLength, plannerVelocity));
            if (gridWorld.check(target.origin) && timeScalar >= deltaTime) {
                timeScalar -= 1;
                if (timeScalar <= deltaTime) {
                    timeScalar = deltaTime;
                }
                continue;
            }
            int step_count = 0;
            DimensionalWayPoint<V> nearest = null;
            double distance = Double.MAX_VALUE;
            verbose("1st level : starting area path generation");
            while (step_count < gridWorld.size()) {
                DimensionalWayPoint<V> sampled = new DimensionalWayPoint<>(gridWorld.sample(), gridCellEdgeLength, plannerVelocity);
                if (nearest != null && MathUtil.random(0.0,1.0) < (1 - nearest.origin.distance(target.origin) / plannerStart.distance(target.origin))) {
                    sampled = new DimensionalWayPoint<>(target.origin.cpy(), gridCellEdgeLength, plannerVelocity);
                } else if (MathUtil.random(0, 1) < 0.3) {
                    sampled = new DimensionalWayPoint<>(target.origin.cpy(), gridCellEdgeLength, plannerVelocity);
                }
                NTreeNode<DimensionalWayPoint<V>> nearestNode = pathRoot.findNearest(sampled, (c1, c2) -> c1.origin.distance2(c2.origin));
                V direction = sampled.origin.cpy().translate(nearestNode.getElement().origin.cpy().reverse());
                V stepped = nearestNode.getElement().origin.cpy().translate(direction.normalize().scale(gridCellEdgeLength));
                if (gridWorld.check(stepped)) {
                    step_count++;
                    continue;
                }
                sampled.origin.set(gridWorld.formalize(stepped));
                nearestNode.createChild(sampled);
                verbose("1st level : generate node on area path " + sampled.origin);
                double dis = target.origin.distance2(stepped);
                if (dis < distance) {
                    distance = dis;
                    nearest = sampled;
                }
                if (sampled.origin.epsilonEquals(gridWorld.formalize(target.origin), 0.001)) {
                    verbose("1st level : area path generation complete");
                    List<DimensionalWayPoint<V>> path = pathRoot.findTrace(sampled);
                    DimensionalPath<DimensionalWayPoint<V>> cellPath = new DimensionalPath<>();
                    path.forEach(cellPath::add);
                    cellPath.ended = true;
                    return cellPath;
                }
                if (System.currentTimeMillis() - startTime > deltaTime * 1000) {
                    return null;
                }
                step_count++;
            }
            if (nearest != null) {
                List<DimensionalWayPoint<V>> path = pathRoot.findTrace(nearest);
                DimensionalPath<DimensionalWayPoint<V>> ret = new DimensionalPath<>();
                path.forEach(e -> ret.add(new DimensionalWayPoint<>(e.origin, e.radius, e.velocity)));
            }
            timeScalar -= 1;
            if (timeScalar <= 5) {
                timeScalar = 5;
            }
        }

    }

    private DimensionalPath<DimensionalWayPoint<V>> secondLevelRRT(DimensionalPath<DimensionalWayPoint<V>> areaPath, V start, V v, int count) {
        DimensionalPath<DimensionalWayPoint<V>> ret = new DimensionalPath<>();
        NormalDistribution N01 = new NormalDistribution(0, 1);
        double rotationLimitsOnOneSide = vehicle.rotationLimits() / 2;
        int deadEndCount = 0;
        int startIdx = 0;
        areaPath.add(target);
        for (DimensionalWayPoint<V> area : areaPath) {
            if (area.origin.distance2(start) <= area.radius * area.radius) {
                startIdx = areaPath.indexOf(area);
            }
        }
        verbose("2nd level : starting actual path generation");
        for (int s = startIdx; s < areaPath.size(); s++) {
            DimensionalWayPoint<V> area = areaPath.get(s);
            int lastSelectedIdx = 0;
            int sameDirectionCount = 0;
            while (start.distance2(area.origin) > area.radius * area.radius) {
                boolean continued = false;
                for (int c = s + 1; c < areaPath.size(); c++) {
                    DimensionalWayPoint<V> position = areaPath.get(c);
                    if (start.distance2(position.origin) <= position.radius * position.radius) {
                        continued = true;
                        verbose("2nd level : filtered passed area path point at " + s);
                        s = c - 1;
                        break;
                    }
                }
                if (continued) {
                    break;
                }
                Map<Integer, Double> comparableMap = new HashMap<>();
                List<Transform<V>> transforms = vehicle.simulateKinetic(v, deltaTime);
                for (Transform<V> transform : transforms) {
                    transform.position.translate(start);
                }
                boolean outOfBound = false;
                V origin = area.origin;
                double prob = s / (double) areaPath.size();
                if (MathUtil.random(0.0, 1.0) < prob) {
                    origin = target.origin;
                }
                for (int i = 0; i < transforms.size(); i++) {
                    Transform<V> t = transforms.get(i);
                    V target = origin.cpy().translate(start.cpy().reverse()).normalize();
                    V next = t.velocity.cpy().normalize();
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
                if (probabilityModifier != null) {
                    probabilityModifier.apply(comparableMap);
                }
                double probability = MathUtil.random(0, 1);
                double sum = 0.0;
                boolean safeTransform = true;
                Transform<V> selected = null;
                int selectedIdx = 0;
                List<Map.Entry<Integer, Double>> sorted = comparableMap.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toList());
                for (Map.Entry<Integer, Double> e : sorted) {
                    Integer key = e.getKey();
                    Double value = e.getValue();
                    sum += value;
                    if (sum >= probability) {
                        selectedIdx = key;
                        selected = transforms.get(key);
                        break;
                    }
                }
                if (!spaceRestriction.include(selected.position)) {
                    safeTransform = false;
                }
                for (Obstacle<V> obs : obstacles) {
                    if (obs.contains(selected.position)) {
                        safeTransform = false;
                    }
                }
                if (safeTransform) {
                    verbose("2nd level : expanding actual path point :" + selected.position);
                    ret.add(new DimensionalWayPoint<>(selected.position, selected.velocity.len(), selected.velocity));
                    ret.utility -= Math.abs(selectedIdx - Math.ceil(transforms.size() / 2.0));
                    start = selected.position.cpy();
                    v = selected.velocity.cpy();
                    if (lastSelectedIdx == selectedIdx) {
                        sameDirectionCount += 1;
                        ret.utility -= (sameDirectionCount);
                    } else {
                        sameDirectionCount = 0;
                    }
                    lastSelectedIdx = selectedIdx;
                    if (ret.size() == count) {
                        ret.utility -= ret.end().origin.distance2(target.origin);
                        ret.utility -= ret.size();
                        verbose("2nd level : finished path generation");
                        return ret;
                    }
                } else {
                    deadEndCount++;
                    verbose("2nd level : dead end back propagation with " + deadEndCount + " path points ");
                    for (int i = 0; i < deadEndCount; i++) {
                        if (ret.size() != 0) {
                            ret.remove(ret.end());
                        }
                    }
                    if (ret.size() == 0) {
                        start = vehicle.position().cpy();
                        v = vehicle.velocity().cpy();
                        deadEndCount = 0;
                    } else {
                        DimensionalWayPoint<V> last = ret.end();
                        start = last.origin.cpy();
                        v = last.velocity.cpy();
                    }
                }
                if (System.currentTimeMillis() - startTime > deltaTime * 1000) {
                    secondLevelTimeOut = true;
                    return ret;
                }
            }
        }
        return ret;
    }


    public MCRRT(double deltaTime,
                 Space<V> spaceRestriction,
                 PathSampler<V> pathSampler,
                 Provider<Integer> pathLengthProvider,
                 Provider<List<Obstacle<V>>> obstacleProvider,
                 Provider<SimulatedVehicle<V>> aircraftProvider,
                 Provider<DimensionalWayPoint<V>> targetProvider,
                 Applier<Map<Integer, Double>> probabilityModifier,
                 Applier<DimensionalPath<DimensionalWayPoint<V>>> pathApplier,
                 boolean verbose
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.verbose = verbose;
        this.probabilityModifier = probabilityModifier;
        this.spaceRestriction = spaceRestriction;
        this.pathLengthProvider = pathLengthProvider;
        if (pathSampler != null) {
            this.pathSampler = pathSampler;
        } else {
            this.pathSampler = this::defaultSampler;
        }
    }

    @Override
    public DimensionalPath<DimensionalWayPoint<V>> algorithm() {
        return this.pathSampler.sample(vehicle, deltaTime);
    }
}
