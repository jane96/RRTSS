package lab.mars.MCRRTImp.algorithm;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;
import lab.mars.RRTBase.Vector;
import java.util.*;

public class MCRRT<V extends Vector<V>> extends RRT<SimulatedVehicle<V>, V, DimensionalWayPoint<V>, DimensionalPath<DimensionalWayPoint<V>>> {

    private Space<V> spaceRestriction;

    private Applier<ScaledGrid> grid2DApplier;

    private PathGenerationConfiguration pathGenerationConfiguration;

    private PathSampler<V> pathSampler;

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

    public interface PathSampler<V extends Vector<V>> {

        DimensionalPath<DimensionalWayPoint<V>> sample(SimulatedVehicle<V> attacker, PathGenerationConfiguration pathConfiguration,  ScaledGrid<V> scaledSpace, double timeScalar, @NotNull DimensionalPath<DimensionalWayPoint<V>> lastPath);
    }

    private DimensionalPath<DimensionalWayPoint<V>> defaultSampler(SimulatedVehicle<V> simulatedVehicle, PathGenerationConfiguration pathConfiguration,  ScaledGrid<V> scaledSpace, double timeScalar, @NotNull DimensionalPath<DimensionalWayPoint<V>> lastPath) {
        V randomV;
        NTreeNode<DimensionalWayPoint<V>> expandingTree = new NTreeNode<>(new DimensionalWayPoint<>(simulatedVehicle.position().cpy(), simulatedVehicle.safeDistance(), simulatedVehicle.velocity()));
        int count = 0;
        while (true) {
            if (MathUtil.random(0, 1) < 0.3) {
                if (lastPath.size() == 0) {
                    randomV = target.origin.cpy();
                } else {
                    int wayPointRandomIdx = (int) MathUtil.random(0, lastPath.size());
                    randomV = lastPath.get(wayPointRandomIdx).origin;
                }
            } else {
                randomV = scaledSpace.sample();
            }
            double rotationLimits = simulatedVehicle.rotationLimits() * timeScalar / 2.0;
            DimensionalWayPoint<V> sampled = new DimensionalWayPoint<>(randomV, 0, randomV.cpy());
            NTreeNode<DimensionalWayPoint<V>> nearestTreeNode = expandingTree.findNearest(sampled, (e, s) -> {
                List<Transform<V>> transforms = simulatedVehicle.simulateKinetic(e.velocity, timeScalar); // uses absolute velocity direction
                V translated = s.origin.cpy().translate(e.origin.cpy().reverse()).normalize();
                Transform<V> left = transforms.get(transforms.size() - 1);
                Transform<V> right = transforms.get(0);
                if (translated.angle(left.position) < rotationLimits && translated.angle(right.position) < rotationLimits) {
                    return e.origin.distance2(s.origin);
                }
                return Double.POSITIVE_INFINITY;
            });
            DimensionalWayPoint<V> nearestWayPoint = nearestTreeNode.getElement();
            V nearestOrigin = nearestWayPoint.origin;
            V translated = randomV.cpy().translate(nearestOrigin.cpy().reverse());
            List<Transform<V>> nearestSteps = vehicle.simulateKinetic(nearestWayPoint.velocity, timeScalar);
            Transform<V> selectedStep = nearestSteps.stream().min((t1, t2) -> {
                double t1Angle = translated.angle(t1.position);
                double t2Angle = translated.angle(t2.position);
                return Double.compare(t1Angle, t2Angle);
            }).get();
            double distanceTraveled = selectedStep.position.len();
            if (scaledSpace.check(selectedStep.position.translate(nearestOrigin))) {
                continue;
            }
            DimensionalWayPoint<V> steppedWayPoint =
                    new DimensionalWayPoint<>(selectedStep.position, distanceTraveled, selectedStep.velocity);

            nearestTreeNode.createChild(steppedWayPoint);
            count++;
            if (count >= 1000 || steppedWayPoint.origin.distance2(target.origin) <= target.radius * target.radius) {
                List<DimensionalWayPoint<V>> wayPointList = expandingTree.findTrace(nearestTreeNode.getChild(0));
                int requiredSize = pathConfiguration.immutablePathLength + pathConfiguration.mutablePathLength + pathConfiguration.thirdPathLength;
                int size = wayPointList.size() > requiredSize ? requiredSize : wayPointList.size();
                DimensionalPath<DimensionalWayPoint<V>> path = new DimensionalPath<>();
                DimensionalWayPoint<V> firstOne = wayPointList.get(0);
                firstOne.origin.translate(vehicle.position());
                path.add(firstOne);
                for (int i = 1; i < size; i++) {
                    V positionOffset = wayPointList.get(i - 1).origin.cpy();
                    DimensionalWayPoint<V> thisWayPoint = wayPointList.get(i);
                    V thisPoint = thisWayPoint.origin.cpy();
                    thisPoint.translate(positionOffset.reverse());
                    System.out.println(thisPoint.len());
                    path.add(new DimensionalWayPoint<V>(thisPoint, thisWayPoint.radius, thisWayPoint.velocity));
                }
                return path;
            }
        }
    }

    public MCRRT(double deltaTime,
                 @NotNull Space<V> spaceRestriction,
                 @Nullable PathSampler<V> pathSampler,
                 @NotNull PathGenerationConfiguration configuration,
                 @NotNull Provider<List<Obstacle<V>>> obstacleProvider,
                 @NotNull Provider<SimulatedVehicle<V>> aircraftProvider,
                 @NotNull Provider<DimensionalWayPoint<V>> targetProvider,
                 @NotNull Applier<DimensionalPath<DimensionalWayPoint<V>>> pathApplier,
                 @Nullable Applier<ScaledGrid> grid2DApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.spaceRestriction = spaceRestriction;
        this.grid2DApplier = grid2DApplier;
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
