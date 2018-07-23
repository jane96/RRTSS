package lab.mars.HRRTImp;

import lab.mars.RRTBase.Aircraft;

public class Attacker implements Aircraft<Vector2> {

    private Vector2 position;

    private Vector2 velocity;

    private double rotationLimits;
    private double viewDistance;

    private int graduation;


    public Attacker(Vector2 position, Vector2 velocity, double rotationLimits, double viewDistance, int graduation) {
        this.position = position;
        this.velocity = velocity;
        this.rotationLimits = rotationLimits;
        this.viewDistance = viewDistance;
        this.graduation = graduation;


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

    @Override
    public int rotationGraduation() {
        return graduation;
    }

}
