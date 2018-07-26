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
        List<Transform<V>> transforms = aircraft.simulateKinetic(plannerStartVelocity, deltaTime * 10);
        double scalar = 100;
        while (true) {
            ScaledGrid<V> scaledSpace = new ScaledGrid<>(spaceRestriction, scalar);
            V sampled = scaledSpace.sample();
            
        }

    }

    @Override
    public DimensionalPath<DimensionalWayPoint<V>> algorithm() {
        return selfImprovingRRT();
    }
}
