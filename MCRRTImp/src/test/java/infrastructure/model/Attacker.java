package infrastructure.model;

import infrastructure.Vector2BasedImp.Vector2;
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

    private Provider<List<Obstacle<V>>> obstacleProvider;

    private List<NTreeNode<DimensionalWayPoint<V>>> leaves = new ArrayList<>();

    public List<NTreeNode<DimensionalWayPoint<V>>> getLeaves() {
        return leaves;
    }

    private void leafApplier(List<NTreeNode<DimensionalWayPoint<V>>> leaves) {
        this.leaves = leaves;
    }


    public Attacker(V position, V velocity, double rotationLimits, int numberOfDirection, double safeDistance, double viewDistance, double viewAngle, DimensionalWayPoint<V> designatedTargetPosition,
                    Space<V> area, Provider<List<Obstacle<V>>> obstacleProvider) {
        super(position, velocity, rotationLimits, numberOfDirection, safeDistance);
        this.obstacleProvider = obstacleProvider;
        this.topVelocity = velocity.len();
        this.viewDistance = viewDistance;
        this.designatedTargetPosition = designatedTargetPosition;
        this.viewAngle = viewAngle;
        this.algorithm = new MCRRT<>(1 / 2.0, area, null, () -> 1000,   obstacleProvider, () -> this, () -> designatedTargetPosition, (set) -> {},  this::setActualPath, false);
//        this.algorithm = new MCTSSampler<>(10, area, obstacleProvider, () -> this, () -> designatedTargetPosition, this::setActualPath, null, this::leafApplier, null);

    }

    public void setDesignatedTarget(DimensionalWayPoint<V> target) {
        this.designatedTargetPosition = target;
    }

    public DimensionalPath<DimensionalWayPoint<V>> actualPath() {
        while (actualPath.size() > 1) {
            actualPath.poll();
        }
        return actualPath.peek();
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
//        startAlgorithm();
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
        new Thread(() -> {
            while (true) {
                algorithm.solve(true);
                V cpy = position.cpy();
                boolean flag = false;
                do {
                    cpy.dimensions()[0].value = MathUtil.random(0.0, 1920.0);
                    cpy.dimensions()[1].value = MathUtil.random(0.0, 1080.0);
                    flag = false;
                    for (Obstacle<V> obstacle : obstacleProvider.provide()) {
                        if (obstacle.contains(cpy)) {
                            flag = true;
                        }
                    }
                }while (flag);
                position.set(cpy);
            }
        }).start();
    }

}
