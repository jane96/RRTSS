package lab.mars.MCRRTImp.model;

import lab.mars.MCRRTImp.algorithm.MCRRT;
import lab.mars.RRTBase.Aircraft;
import lab.mars.RRTBase.Vector;

import java.util.ArrayList;
import java.util.List;

public class Attacker implements Aircraft<Vector2> {

    private Vector2 position;

    private Vector2 velocity;

    private double rotationLimits;

    private double viewDistance;

    private double viewAngle;

    private int numberOfDirection;

    private double safeDistance;

    private WayPoint2D designatedTargetPosition;

    private Path2D<Cell2D> areaPath = new Path2D<>();

    private Path2D<WayPoint2D> actualPath = new Path2D<>();

    private Grid2D gridWorld;

    private MCRRT algorithm;

    public MCRRT.PathGenerationConfiguration configuration = new MCRRT.PathGenerationConfiguration(50, 250, 500, 20);

    public Attacker(Vector2 position, Vector2 velocity, double rotationLimits, double viewAngle, double viewDistance, double safeDistance, int numberOfDirection) {
        this.position = position;
        this.velocity = velocity;
        this.viewAngle = viewAngle;
        this.rotationLimits = rotationLimits;
        this.viewDistance = viewDistance;
        this.safeDistance = safeDistance;
        this.numberOfDirection = numberOfDirection;
        this.algorithm = new MCRRT(1, 2000, 2000, configuration, World::allObstacles, () -> this, () -> this.designatedTargetPosition, this::setActualPath, this::setAreaPath, this::setGridWorld);
    }

    public void setDesignatedTarget(WayPoint2D target) {
        this.designatedTargetPosition = target;
    }

    public void setAreaPath(Path2D<Cell2D> areaPath) {
        this.areaPath = areaPath;
    }

    public Path2D<Cell2D> areaPath() {
        return areaPath;
    }

    public Path2D<WayPoint2D> actualPath() {
        return actualPath;
    }

    public WayPoint2D target() {
        return designatedTargetPosition;
    }

    public Grid2D gridWorld() {
        return gridWorld;
    }

    public void setActualPath(Path2D<WayPoint2D> actualPath) {
        this.actualPath = actualPath;
    }

    public void setGridWorld(Grid2D gridWorld) {
        this.gridWorld = gridWorld;
    }

    private double simulateVelocity(double currentVelocity, double angle) {
        return currentVelocity * (1 - Math.abs(angle) / this.rotationLimits);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public <V extends Vector<V>> List<Transform <V>> simulateKinetic( V currentVelocity, V currentPosition, double deltaTime) {
        /** Return a RIGHT -> LEFT Point List */
        List<Transform<V>> ret = new ArrayList<>();
        double eachGap = this.rotationLimits / (this.numberOfDirection - 1);
        double sliceCount = 100;
        V rotated = currentVelocity.cpy();
        V translated = currentPosition.cpy();
        for(int i = -numberOfDirection / 2; i <= numberOfDirection / 2; i++) {
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

    public Vector2 position() {
        return position;
    }

    public Vector2 velocity() {
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
