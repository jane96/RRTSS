package lab.mars.MCRRTImp;

import lab.mars.RRTBase.MathUtil;
import lab.mars.MCRRTImp.model.Vector2;
import org.junit.Test;

public class TestVector2 {

    @Test
    public void testDistance() {
        Vector2 v1 = new Vector2(1, 1);
        Vector2 v2 = new Vector2(2, 2);
        double result = v1.distance(v2);
        System.out.println(result);
        assert MathUtil.epsilonEquals(result, Math.sqrt(2));
    }

    @Test
    public void testNormalize() {
        Vector2 v1 = new Vector2(3, 4);
        v1.normalize();
        assert MathUtil.epsilonEquals(v1.len(), 1);
    }

    @Test
    public void testDot() {
        Vector2 v1 = new Vector2(2, 5);
        Vector2 v2 = new Vector2(3, 7);
        double result = v1.dot(v2);
        assert MathUtil.epsilonEquals(result, 2 * 3 + 5 * 7);
    }

    @Test
    public void testAngle() {
        Vector2 v1 = new Vector2(1, 0);
        Vector2 v2 = new Vector2(Math.cos(Math.PI / 6), Math.sin(Math.PI / 6));
        double result = v1.angle(v2);
        System.out.println(result);
        assert MathUtil.epsilonEquals(result, 30);
    }

    @Test
    public void testRotate() {
        Vector2 v1 = new Vector2(1, 0);
        Vector2 v2 = v1.cpy().rotate(30);
        System.out.println(v2);
        assert MathUtil.epsilonEquals(v2.x, Math.cos(Math.PI / 6));
        assert MathUtil.epsilonEquals(v2.y, Math.sin(Math.PI / 6));
    }

    @Test
    public void testLerp() {
        Vector2 v1 = new Vector2(2, 2);
        Vector2 v2 = new Vector2(0, 1);
        v1.lerp(v2, 0.5);
        System.out.println(v1);
        System.out.println(v2);
        assert MathUtil.epsilonEquals(v1.x, 1) && MathUtil.epsilonEquals(v1.y, 1.5);
    }
}
