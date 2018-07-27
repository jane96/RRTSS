package lab.mars.MCRRTImp.algorithm;

import com.sun.istack.internal.Nullable;
import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;
import lab.mars.RRTBase.Vector;

import java.util.*;

public class MCRRT<V extends Vector<V>> extends RRT<Attacker<V>, V, DimensionalWayPoint<V>, DimensionalPath<DimensionalWayPoint<V>>> {

    private Space<V> spaceRestriction;

    private Applier<ScaledGrid> grid2DApplier;

    private Applier<DimensionalPath<Cell2D>> firstLevelApplier;

    private DimensionalPath<Cell2D> firstLevelPathCache = new DimensionalPath<>();

    private DimensionalPath<DimensionalWayPoint<V>> immutablePathCache = new DimensionalPath<>();

    private DimensionalPath<DimensionalWayPoint<V>> mutablePathCache = new DimensionalPath<>();

    private DimensionalPath<DimensionalWayPoint<V>> secondLevelPathCache = new DimensionalPath<>();

    private PathGenerationConfiguration pathGenerationConfiguration;

    private WayPointSampler<V> treeExpander;

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

    public interface WayPointSampler<V extends Vector<V>> {

        DimensionalWayPoint<V> sample(Attacker<V> attacker, Space<V> scaledSpace, @Nullable NTreeNode<DimensionalWayPoint<V>> expandingTree);

        boolean simulate(DimensionalWayPoint<V> sampledWayPoint);

        void expand(@Nullable NTreeNode<DimensionalWayPoint<V>> expandingTree);

        DimensionalPath<DimensionalWayPoint<V>> backup(NTreeNode<DimensionalWayPoint<V>> expandedTree);
    }

    public MCRRT(double deltaTime,
                 Space<V> spaceRestriction,
                 WayPointSampler<V> wayPointSampler,
                 PathGenerationConfiguration configuration,
                 Provider<List<Obstacle<V>>> obstacleProvider,
                 Provider<Attacker<V>> aircraftProvider,
                 Provider<DimensionalWayPoint<V>> targetProvider,
                 Applier<DimensionalPath<DimensionalWayPoint<V>>> pathApplier,
                 Applier<DimensionalPath<Cell2D>> firstLevelApplier,
                 Applier<ScaledGrid> grid2DApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.spaceRestriction = spaceRestriction;
        this.grid2DApplier = grid2DApplier;
        this.firstLevelApplier = firstLevelApplier;
        this.pathGenerationConfiguration = configuration;
        this.treeExpander = wayPointSampler;
    }


    private DimensionalPath<DimensionalWayPoint<V>> selfImprovingRRT() {
        V plannerStartVelocity = aircraft.velocity();
        NTreeNode<DimensionalWayPoint<V>> root = null;
        DimensionalPath<DimensionalWayPoint<V>> lastGeneratedPath = new DimensionalPath<>();
        double timeScalar = deltaTime * 10;
        double rotationLimits = aircraft.rotationLimits();
        double scalar = 100;
        while (true) {
            ScaledGrid<V> scaledSpace = new ScaledGrid<>(spaceRestriction, scalar);
            V randomV = scaledSpace.sample();
            DimensionalWayPoint<V> sampled = new DimensionalWayPoint<>(randomV, 0, randomV.cpy());
            NTreeNode<DimensionalWayPoint<V>> nearestTreeNode = root.findNearest(sampled, (e, c) -> {
                List<Transform<V>> transforms = aircraft.simulateKinetic(e.velocity, timeScalar);
                V translated = c.origin.cpy().translate(e.origin);
                Transform<V> left = transforms.get(transforms.size() - 1);
                Transform<V> right = transforms.get(0);
                if (translated.angle(left.position) < rotationLimits && translated.angle(right.position) < rotationLimits) {
                    return e.origin.distance2(c.origin);
                }
                return Double.POSITIVE_INFINITY;
            });
            DimensionalWayPoint<V> nearestWayPoint = nearestTreeNode.getElement();
            V nearestOrigin = nearestWayPoint.origin;
            V translated = randomV.translate(nearestOrigin.cpy().reverse());
            List<Transform<V>> nearestSteps = aircraft.simulateKinetic(nearestWayPoint.velocity, timeScalar);
            Transform<V> selectedStep = nearestSteps.stream().min((t1, t2) -> {
                double t1Angle = translated.angle(t1.position);
                double t2Angle = translated.angle(t2.position);
                return Double.compare(t1Angle, t2Angle);
            }).get();
            DimensionalWayPoint<V> steppedWayPoint =
                    new DimensionalWayPoint<>(selectedStep.position.translate(nearestOrigin), selectedStep.position.len(), selectedStep.velocity);
            nearestTreeNode.createChild(steppedWayPoint);
            if (steppedWayPoint.origin.distance2(target.origin) <= target.radius * target.radius) {
                List<DimensionalWayPoint<V>> wayPointList = root.findTrace(nearestTreeNode.getChild(0));
                DimensionalPath<DimensionalWayPoint<V>> path = new DimensionalPath<>();
                DimensionalWayPoint<V> firstOne = wayPointList.get(0);
                firstOne.origin.translate(aircraft.position());
                path.add(firstOne);
                for (int i = 1; i < wayPointList.size(); i++) {
                    V positionOffset = wayPointList.get(i - 1).origin;
                    DimensionalWayPoint<V> thisPoint = wayPointList.get(i);
                    thisPoint.origin.translate(positionOffset);
                    path.add(thisPoint);
                }
            }

        }

    }

    @Override
    public DimensionalPath<DimensionalWayPoint<V>> algorithm() {
        return selfImprovingRRT();
    }
}
