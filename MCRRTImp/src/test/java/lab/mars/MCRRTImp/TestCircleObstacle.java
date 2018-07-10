package lab.mars.MCRRTImp;

import org.junit.Test;

public class TestCircleObstacle {

    @Test
    public void testContains() {
        CircleObstacle obstacle = new CircleObstacle(10, 10, 10, 15);
        Vector2 planeCenter = new Vector2(0, 0);
        Vector2 plane2Center = new Vector2(3, 3);
        assert !obstacle.contains(planeCenter);
        assert obstacle.contains(plane2Center);
    }

}
