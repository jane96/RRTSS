package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.algorithm.MCRRT;
import lab.mars.RRTBase.Aircraft;
import lab.mars.RRTBase.Vector;

import java.util.List;

public class Attacker<V extends Vector<V>> implements Aircraft<V> {

    private V position;

    private V velocity;

    private double rotationLimits;

    private double viewDistance;

    private double viewAngle;

    private int numberOfDirection;

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

    public <V extends Vector<V>> List<Transform <V>> simulateKinetic( V currentVelocity, V currentPosition, double deltaTime) {
        /** Return a RIGHT -> LEFT Point List */
        List<Transform<V>> ret = new ArrayList<>();
        double eachGap = this.rotationLimits / (this.numberOfDirection - 1);
        double sliceCount = 100;
        for(int i = -numberOfDirection / 2; i <= numberOfDirection / 2; i++) {
            V rotated = currentVelocity.cpy();
            V translated = currentPosition.cpy();
            V nextV = currentVelocity.cpy();
            double totalAngleRotated = i * eachGap * deltaTime;
            double sliceThetaGap = totalAngleRotated / sliceCount;      // The delta for Integrating function
            nextV.rotate(totalAngleRotated);        // Set the Velocity angle
            nextV.normalize().scale(simulateVelocity(currentVelocity.len(), i * eachGap));      // Set the Velocity's len()
            double newV = nextV.len();          // Get the Velocity's len()
            for(int c=0; c<sliceCount; c++){
                rotated.rotate(sliceThetaGap);
                translated.translate(rotated.normalize().scale(newV * deltaTime / sliceCount));
            }
            ret.add(new Transform<>(nextV, translated));
        }
        return ret;
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
        return numberOfDirection;
    }


    public void startAlgorithm() {
        this.algorithm.solve(false);
    }

}
