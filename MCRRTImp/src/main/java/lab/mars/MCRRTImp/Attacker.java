package lab.mars.MCRRTImp;

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

    private int graduation;

    private double safeDistance;

    public Attacker(Vector2 position, Vector2 velocity, double rotationLimits, double viewAngle, double viewDistance, double safeDistance, int graduation) {
        this.position = position;
        this.velocity = velocity;
        this.viewAngle = viewAngle;
        this.rotationLimits = rotationLimits;
        this.viewDistance = viewDistance;
        this.safeDistance = safeDistance;
        this.graduation = graduation;
    }

    public double simulateVelocity(double velocity, double angle) {
        return velocity * (1 - Math.abs(angle) / rotationLimits);
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
        return graduation;
    }

    public List<Transform> simulateKinetic(double deltaTime) {
        List<Transform> ret = new ArrayList<>();
        double v = this.velocity.len();
        int sliceCount = 100;
        for (double i = - this.rotationLimits / 2; i < this.rotationLimits / 2; i += this.graduation) {
            double totalAngleRotated = i * deltaTime;
            double slicedAngleRotated = totalAngleRotated / sliceCount;
            Vector2 rotated = this.velocity.cpy();
            Vector2 translated = null;
            for (int c = 0; c < sliceCount;c ++) {
                double alpha = c * slicedAngleRotated;
                double newV = simulateVelocity(v, alpha);
                rotated.rotate(slicedAngleRotated).normalize().scale(newV);
                translated = position.cpy().add(rotated.normalize().scale(newV * deltaTime / sliceCount));
            }
            ret.add(new Transform(translated, rotated));
        }
        return ret;
    }

}
