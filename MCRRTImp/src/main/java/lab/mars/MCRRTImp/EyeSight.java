package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

public class EyeSight implements Obstacle<Vector2> {

    private Provider<Vector2> eyeCenterProvider;

    private Provider<Double> eyeSightRadiusProvider;

    public EyeSight(Provider<Vector2> eyeCenterProvider, Provider<Double> eyeSightRadiusProvider) {
        this.eyeCenterProvider = eyeCenterProvider;
        this.eyeSightRadiusProvider = eyeSightRadiusProvider;
    }

    public Vector2 centroid() {
        return eyeCenterProvider.provide();
    }

    public Double radius() {
        return eyeSightRadiusProvider.provide();
    }

    @Override
    public boolean contains(Vector2 o) {
        double r = radius();
        return o.distance2(centroid()) > r * r;
    }
}
