package lab.mars.MCRRTImp;

import org.junit.Test;

public class TestMathUtil {

    @Test
    public void testDoubleEqualityWithError() {
        double d1 = 1;
        double d2 = 1.5;
        boolean result = MathUtil.epsilonEquals(d1, d2);
        assert !result;
        result = MathUtil.epsilonEquals(d1, d2, 1);
        assert result;
    }

}
