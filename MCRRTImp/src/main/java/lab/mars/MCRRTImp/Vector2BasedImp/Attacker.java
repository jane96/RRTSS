package lab.mars.MCRRTImp.Vector2BasedImp;

import lab.mars.MCRRTImp.algorithm.MCRRT;
import lab.mars.MCRRTImp.model.DimensionalPath;
import lab.mars.MCRRTImp.model.DimensionalWayPoint;
import lab.mars.MCRRTImp.model.ScaledGrid;
import lab.mars.MCRRTImp.model.SimulatedVehicle;
import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.List;

public class Attacker<V extends Vector<V>> extends SimulatedVehicle<V> {

    private double viewDistance;

    private double viewAngle;

    private double topVelocity;

    private DimensionalWayPoint<V> designatedTargetPosition;

    private DimensionalPath<DimensionalWayPoint<V>> actualPath = new DimensionalPath<>();

    private ScaledGrid gridWorld;

    private MCRRT algorithm;

    public MCRRT.PathGenerationConfiguration configuration = new MCRRT.PathGenerationConfiguration(50, 250, 500, 20);

    public Attacker(V position, V velocity, double rotationLimits, int numberOfDirection, double safeDistance, double viewDistance, double viewAngle, DimensionalWayPoint<V> designatedTargetPosition,
                    Space<V> area, Provider<List<Obstacle<V>>> obstacleProvider) {
        super(position, velocity, rotationLimits, numberOfDirection, safeDistance);
        this.topVelocity = velocity.len();
        this.viewDistance = viewDistance;
        this.designatedTargetPosition = designatedTargetPosition;
        this.viewAngle = viewAngle;
        this.algorithm = new MCRRT<>(1, area, null, this.configuration, obstacleProvider, () -> this, () -> designatedTargetPosition, this::setActualPath, this::setGridWorld);
    }

    public void setDesignatedTarget(DimensionalWayPoint<V> target) {
        this.designatedTargetPosition = target;
    }

    public DimensionalPath<DimensionalWayPoint<V>> actualPath() {
        return actualPath;
    }

    public DimensionalWayPoint target() {
        return designatedTargetPosition;
    }

    public ScaledGrid gridWorld() {
        return gridWorld;
    }

    public void setGridWorld(ScaledGrid<V> gridWorld) {
        this.gridWorld = gridWorld;
    }

    public void setActualPath(DimensionalPath<DimensionalWayPoint<V>> actualPath) {
        this.actualPath = actualPath;
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
        this.algorithm.solve(true);
    }

}
