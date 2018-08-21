package lab.mars.MCRRTImp.algorithm;

import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;
import lab.mars.RRTBase.Vector;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.*;

public class MCRRT<V extends Vector<V>> extends RRT<SimulatedVehicle<V>, V, DimensionalWayPoint<V>, DimensionalPath<DimensionalWayPoint<V>>> {

    private Space<V> spaceRestriction;

    private Applier<ScaledGrid> grid2DApplier;

    private Applier<DimensionalPath<DimensionalWayPoint<V>>> areaPathApplier;

    private PathGenerationConfiguration pathGenerationConfiguration;

    private PathSampler<V> pathSampler;

    public static class PathGenerationConfiguration {
        public int immutablePathLength = 0;
        public int mutablePathLength = 0;
        public int thirdPathLength = 0;
        public int replanWaitTime = 10;

        public PathGenerationConfiguration(int immutablePathLength, int mutablePathLength, int thirdPathLength, int replanWaitTime) {
            this.immutablePathLength = immutablePathLength;
            this.mutablePathLength = mutablePathLength;
            this.thirdPathLength = thirdPathLength;
            this.replanWaitTime = replanWaitTime;
        }
    }

    public interface PathSampler<V extends Vector<V>> {

        DimensionalPath<DimensionalWayPoint<V>> sample(SimulatedVehicle<V> attacker, PathGenerationConfiguration pathConfiguration, ScaledGrid<V> scaledSpace, double timeScalar,  DimensionalPath<DimensionalWayPoint<V>> lastPath);
    }

    private DimensionalPath<DimensionalWayPoint<V>> defaultSampler(SimulatedVehicle<V> simulatedVehicle, PathGenerationConfiguration pathConfiguration, ScaledGrid<V> scaledSpace, double timeScalar,  DimensionalPath<DimensionalWayPoint<V>> lastPath) {
        while (true) {
            System.out.println("first level");
            DimensionalPath<DimensionalWayPoint<V>> areaPath = firstLevelRRT(vehicle.position(), vehicle.velocity());
            System.out.println("second level");
            DimensionalPath<DimensionalWayPoint<V>> actualPath = secondLevelRRT(areaPath, vehicle.position(), vehicle.velocity(), 1000);
//            pathApplier.apply(actualPath);
            return actualPath;
        }
    }


    private DimensionalPath<DimensionalWayPoint<V>> firstLevelRRT(V plannerStart, V plannerVelocity) {
        ScaledGrid<V> gridWorld;
        double timeScalar = deltaTime * 10;
        NTreeNode<DimensionalWayPoint<V>> pathRoot;
        double gridCellEdgeLength;
        long startTime = System.currentTimeMillis();
        while (true) {
            List<Transform<V>> transforms = vehicle.simulateKinetic(plannerVelocity, timeScalar);
            int scaleBase = (int) transforms.stream().max(Comparator.comparingDouble(o -> o.position.len())).get().position.len();
            transforms.forEach(transform -> transform.position.translate(plannerStart));
            gridWorld = new ScaledGrid<>(spaceRestriction, scaleBase);
            gridWorld.scan(obstacles);
            V gridAircraft = gridWorld.formalize(plannerStart);
            gridCellEdgeLength = gridWorld.cellSize().len();
            //TODO : decide the actual cell growth length according to growth direction
            pathRoot = new NTreeNode<>(new DimensionalWayPoint<>(gridAircraft, gridCellEdgeLength, plannerVelocity));
            grid2DApplier.apply(gridWorld);
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
            while (step_count < gridWorld.size()) {
                DimensionalWayPoint<V> sampled = new DimensionalWayPoint<>(gridWorld.sample(), gridCellEdgeLength, plannerVelocity);
                if (MathUtil.random(0, 1) < 0.3) {
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
                double dis = target.origin.distance2(stepped);
                if (dis < distance) {
                    distance = dis;
                    nearest = sampled;
                }
                if (sampled.origin.epsilonEquals(gridWorld.formalize(target.origin), 0.001)) {
                    List<DimensionalWayPoint<V>> path = pathRoot.findTrace(sampled);
                    DimensionalPath<DimensionalWayPoint<V>> cellPath = new DimensionalPath<>();
                    path.forEach(cellPath::add);
                    cellPath.ended = true;
                    System.out.println(System.currentTimeMillis() - startTime + "ms");
                    DimensionalPath<DimensionalWayPoint<V>> copied = new DimensionalPath<>();
                    cellPath.forEach(copied::add);
                    areaPathApplier.apply(copied);
                    return cellPath;
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
        for (DimensionalWayPoint<V> area : areaPath) {
            if (area.origin.distance2(start) <= area.radius * area.radius) {
                startIdx = areaPath.indexOf(area);
            }
        }
        for (int s = startIdx; s < areaPath.size(); s++) {
            DimensionalWayPoint<V> area = areaPath.get(s);
            while (start.distance(area.origin) > area.radius) {
                Map<Integer, Double> comparableMap = new HashMap<>();
                List<Transform<V>> transforms = vehicle.simulateKinetic(v, deltaTime);
                for (Transform<V> transform : transforms) {
                    transform.position.translate(start);
                }
                boolean outOfBound = false;
                for (int i = 0; i < transforms.size(); i++) {
                    Transform<V> t = transforms.get(i);
                    V target = area.origin.cpy().translate(start.cpy().reverse()).normalize();
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
                Set<Integer> accessedTransforms = new HashSet<>();
                while (true) {
                    double probability = MathUtil.random(0, 1);
                    double sum = 0;
                    boolean safeTransform = true;
                    Transform<V> selected = null;
                    for (Map.Entry<Integer, Double> entry : comparableMap.entrySet()) {
                        sum += entry.getValue();
                        if (sum >= probability) {
                            int idx = entry.getKey();
                            selected = transforms.get(idx);
                            accessedTransforms.add(idx);
                            for (Obstacle<V> obs : obstacles) {
                                if (obs.contains(selected.position)) {
                                    safeTransform = false;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (safeTransform) {
                        ret.add(new DimensionalWayPoint<>(selected.position, selected.velocity.len(), selected.velocity));
                        start = selected.position.cpy();
                        v = selected.velocity.cpy().normalize().scale(3);
                        if (ret.size() == count) {
                            return ret;
                        } else {
                            DimensionalPath<DimensionalWayPoint<V>> copied = new DimensionalPath<>();
                            ret.forEach(copied::add);
                            pathApplier.apply(copied);

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
                            start = vehicle.position();
                            v = vehicle.velocity();
                        } else {
                            DimensionalWayPoint<V> last = ret.end();
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


    public MCRRT(double deltaTime,
                  Space<V> spaceRestriction,
                  PathSampler<V> pathSampler,
                  PathGenerationConfiguration configuration,
                  Provider<List<Obstacle<V>>> obstacleProvider,
                  Provider<SimulatedVehicle<V>> aircraftProvider,
                  Provider<DimensionalWayPoint<V>> targetProvider,
                  Applier<DimensionalPath<DimensionalWayPoint<V>>> pathApplier,
                  Applier<DimensionalPath<DimensionalWayPoint<V>>> areaPathApplier,
                  Applier<ScaledGrid> grid2DApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.spaceRestriction = spaceRestriction;
        this.grid2DApplier = grid2DApplier;
        this.areaPathApplier = areaPathApplier;
        this.pathGenerationConfiguration = configuration;
        if (pathSampler != null) {
            this.pathSampler = pathSampler;
        } else {
            this.pathSampler = this::defaultSampler;
        }
    }


    private DimensionalPath<DimensionalWayPoint<V>> selfImprovingRRT() {
        V plannerStartVelocity = vehicle.velocity();
        DimensionalPath<DimensionalWayPoint<V>> lastGeneratedPath = new DimensionalPath<>();
        double scaledTime = deltaTime;
        while (true) {
            List<Transform<V>> initialTransforms = vehicle.simulateKinetic(plannerStartVelocity, scaledTime);
            double scalar = initialTransforms.get(0).position.len();
            ScaledGrid<V> scaledSpace = new ScaledGrid<>(spaceRestriction, scalar);
            lastGeneratedPath = this.pathSampler.sample(vehicle, pathGenerationConfiguration, scaledSpace, scaledTime, lastGeneratedPath);
            System.out.println("returned");
            scaledTime /= 2;
//            if (scaledTime <= deltaTime) {
            return lastGeneratedPath;
//            }
        }

    }

    @Override
    public DimensionalPath<DimensionalWayPoint<V>> algorithm() {
        return selfImprovingRRT();
    }
}
