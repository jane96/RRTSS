package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.algorithm.MCRRT;
import lab.mars.RRTBase.Aircraft;
import lab.mars.RRTBase.Vector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class Attacker<V extends Vector<V>> implements Aircraft<V> {

    private V position;

    private V velocity;

    private double rotationLimits;

    private double viewDistance;

    private double viewAngle;

    private int rotationGraduation;

    private double safeDistance;

    private DimensionalWayPoint<V> designatedTargetPosition;

    private DimensionalPath<Cell2D> areaPath = new DimensionalPath<>();

    private DimensionalPath<DimensionalWayPoint<V>> actualPath = new DimensionalPath<>();

    private ScaledGrid gridWorld;

    private MCRRT algorithm;

    public MCRRT.PathGenerationConfiguration configuration = new MCRRT.PathGenerationConfiguration(50, 250, 500, 20);

    public Attacker(V position, V velocity, double rotationLimits, double viewAngle, double viewDistance, double safeDistance, int graduation) {
        this.position = position;
        this.velocity = velocity;
        this.viewAngle = viewAngle;
        this.rotationLimits = rotationLimits;
        this.viewDistance = viewDistance;
        this.safeDistance = safeDistance;
        this.rotationGraduation = graduation;
//        this.algorithm = new MCRRT(1, 2000, 2000, configuration, World::allObstacles, () -> this, () -> this.designatedTargetPosition, this::setActualPath, this::setAreaPath, this::setGridWorld);
    }

    public void setDesignatedTarget(DimensionalWayPoint target) {
        this.designatedTargetPosition = target;
    }

    public void setAreaPath(DimensionalPath<Cell2D> areaPath) {
        this.areaPath = areaPath;
    }

    public DimensionalPath<Cell2D> areaPath() {
        return areaPath;
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

    public void setActualPath(DimensionalPath<DimensionalWayPoint<V>> actualPath) {
        this.actualPath = actualPath;
    }

    public void setGridWorld(ScaledGrid gridWorld) {
        this.gridWorld = gridWorld;
    }

    private double simulateVelocity(double currentVelocity, double angle) {
        return currentVelocity * (1 - Math.abs(angle) / this.rotationLimits);
    }

    public void setPosition(V position) {
        this.position = position;
    }

    public void setVelocity(V velocity) {
        this.velocity = velocity;
    }

    public List<Transform<V>> simulateKinetic(V currentVelocity, double deltaTime) {
        //TODO implements abstract simulateKinetic
        throw new NotImplementedException();
    }

    public V position() {
        return position;
    }

    public V velocity() {
        return velocity;
    }

    public double viewDistance() {
        return viewDistance;
    }

    public double rotationLimits() {
        return rotationLimits;
    }

    public double safeDistance() {
        return safeDistance;
    }

    public double viewAngle() {
        return viewAngle;
    }

    @Override
    public int rotationGraduation() {
        return rotationGraduation;
    }


    public void startAlgorithm() {
        this.algorithm.solve(false);
    }

}
