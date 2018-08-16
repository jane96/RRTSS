package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Vector;
import lab.mars.RRTBase.Vehicle;

import java.util.ArrayList;
import java.util.List;

public abstract class SimulatedVehicle<V extends Vector<V>> implements Vehicle<V> {

    protected V position;

    protected V velocity;

    protected double rotationLimits;

    protected int numberOfDirection;

    protected double safeDistance;

    public SimulatedVehicle(V position, V velocity, double rotationLimits, int numberOfDirection, double safeDistance) {
        this.position = position;
        this.velocity = velocity;
        this.rotationLimits = rotationLimits;
        this.numberOfDirection = numberOfDirection;
        this.safeDistance = safeDistance;
    }

    protected abstract double simulateVelocity(double currentVelocity, double angle);

    public final void setPosition(V position) {
        this.position = position;
    }

    public final void setVelocity(V velocity) {
        this.velocity = velocity;
    }

    public final V position() {
        return position;
    }

    public final V velocity() {
        return velocity;
    }

    public final double rotationLimits() {
        return rotationLimits;
    }

    public final double safeDistance() {
        return safeDistance;
    }

    public final int numberOfDirection() {
        return numberOfDirection;
    }


    /** Return a RIGHT -> LEFT Point List */
    public final List<Transform<V>> simulateKinetic(V currentVelocity, double deltaTime) {
        List<Transform<V>> ret = new ArrayList<>();
        double deltaTheta = this.rotationLimits / this.numberOfDirection;
        double sliceCount = 100;
        for (int i = -numberOfDirection / 2; i <= numberOfDirection / 2; i++) {
            V rotated = currentVelocity.cpy();
            V translated = currentVelocity.cpy().zero();
            V nextV = currentVelocity.cpy();
            double totalAngleRotated = i * deltaTheta * deltaTime;
            double slicedDeltaTheta = totalAngleRotated / sliceCount;      // The delta for Integrating function
            nextV.rotate(totalAngleRotated);        // Set the Velocity angle
            nextV.normalize().scale(simulateVelocity(currentVelocity.len(), i * deltaTheta));      // Set the Velocity's len()
            double newV = nextV.len();          // Get the Velocity's len()
            for (int c = 0; c < sliceCount; c++) {
                rotated.rotate(slicedDeltaTheta);
                translated.translate(rotated.normalize().scale(newV * deltaTime / sliceCount));
            }
            ret.add(new Transform<>(nextV, translated));
        }
        return ret;
    }


}
