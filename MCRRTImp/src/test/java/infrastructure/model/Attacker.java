package infrastructure.model;

import lab.mars.MCRRTImp.algorithm.MCRRT;
import lab.mars.MCRRTImp.algorithm.MCTSSampler;
import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Attacker<V extends Vector<V>> extends SimulatedVehicle<V> {

    private double viewDistance;

    private double viewAngle;

    private double topVelocity;

    private DimensionalWayPoint<V> designatedTargetPosition;

    private Queue<DimensionalPath<DimensionalWayPoint<V>>> actualPath = new ConcurrentLinkedQueue<>();

    private Queue<DimensionalPath<DimensionalWayPoint<V>>> areaPath = new ConcurrentLinkedQueue<>();

    private ScaledGrid<V> gridWorld;

    private RRT algorithm;

    private List<NTreeNode<DimensionalWayPoint<V>>> leaves = new ArrayList<>();

    public List<NTreeNode<DimensionalWayPoint<V>>> getLeaves() {
        return leaves;
    }

    private void leafApplier(List<NTreeNode<DimensionalWayPoint<V>>> leaves) {
        this.leaves = leaves;
    }

    public MCRRT.PathGenerationConfiguration configuration = new MCRRT.PathGenerationConfiguration(50, 250, 500, 20);

    public Attacker(V position, V velocity, double rotationLimits, int numberOfDirection, double safeDistance, double viewDistance, double viewAngle, DimensionalWayPoint<V> designatedTargetPosition,
                    Space<V> area, Provider<List<Obstacle<V>>> obstacleProvider) {
        super(position, velocity, rotationLimits, numberOfDirection, safeDistance);
        this.topVelocity = velocity.len();
        this.viewDistance = viewDistance;
        this.designatedTargetPosition = designatedTargetPosition;
        this.viewAngle = viewAngle;
        this.algorithm = new MCRRT<>(1, area, null, this.configuration, obstacleProvider, () -> this, () -> designatedTargetPosition, this::setActualPath, this::setAreaPath, this::setGridWorld);
//        this.algorithm = new MCTSSampler<>(10, area, obstacleProvider, () -> this, () -> designatedTargetPosition, this::setActualPath, null, this::leafApplier, null);

    }

    public void setDesignatedTarget(DimensionalWayPoint<V> target) {
        this.designatedTargetPosition = target;
    }

    public Queue<DimensionalPath<DimensionalWayPoint<V>>> actualPath() {
        return actualPath;
    }

    public DimensionalWayPoint<V> target() {
        return designatedTargetPosition;
    }

    public ScaledGrid<V> gridWorld() {
        return gridWorld;
    }

    public void setAreaPath(DimensionalPath<DimensionalWayPoint<V>> areaPath) {
        this.areaPath.offer(areaPath);
    }

    public void setGridWorld(ScaledGrid<V> gridWorld) {
        this.gridWorld = gridWorld;
    }

    public void setActualPath(DimensionalPath<DimensionalWayPoint<V>> actualPath) {
        this.actualPath.offer(actualPath);
    }

    public Queue<DimensionalPath<DimensionalWayPoint<V>>> areaPath() {
        return this.areaPath;
    }

    @Override
    protected double simulateVelocity(double currentVelocity, double angle) {
        return topVelocity * (1 - Math.abs(angle) / this.rotationLimits);
    }

    public double viewDistance() {
        return viewDistance;
    }

    public double viewAngle() {
        return viewAngle;
    }


    public void startAlgorithm() {
        this.algorithm.solve(false);
    }

}
