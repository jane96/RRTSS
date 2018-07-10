package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Aircraft;
import lab.mars.RRTBase.Vector;

public class Attacker implements Aircraft<Vector2> {

    private Vector2 position;

    private Vector2 velocity;

    private double rotationLimits;

    public Vector2 position() {
        return position;
    }

    public Vector2 velocity() {
        return velocity;
    }

    public double rotationLimits() {
        return rotationLimits;
    }

}
