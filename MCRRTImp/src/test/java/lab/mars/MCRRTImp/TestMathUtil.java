package lab.mars.MCRRTImp;

import org.junit.Test;

public class TestMathUtil {

    @Test
    public void testDoubleEqualityWithError() {
        double d1 = 1;
        double d2 = 1.0002;
        boolean result = MathUtil.epsilonEquals(d1, d2);
        assert !result;
        result = MathUtil.epsilonEquals(d1, d2, 1);
        assert result;
    }

    @Test
    public void testRandom() {
        double zero = MathUtil.random(0, 0);
        assert zero == 0;
        for (int i = 0; i < 1000; i++) {
            double num = MathUtil.random(0, 1000);
            assert !MathUtil.epsilonEquals(num, 1000);
        }
        while (true) {
            double num = MathUtil.random(-200, 1000);
            assert num != 1000;
            if (MathUtil.epsilonEquals(num, -200)) {
                System.out.println(num);
                return;
            }
        }
    }

}
