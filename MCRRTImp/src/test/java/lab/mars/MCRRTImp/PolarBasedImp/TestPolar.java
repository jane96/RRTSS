package lab.mars.MCRRTImp.PolarBasedImp;

import lab.mars.MCRRTImp.PolarBasedImp.Polar;
import org.junit.Test;

public class TestPolar {
    @Test
    public void testPolar(){
        Polar polar1 = new Polar(10, 45);
        System.out.println(polar1.scale(10).r());
    }
}
