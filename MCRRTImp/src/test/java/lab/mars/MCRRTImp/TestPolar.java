package lab.mars.MCRRTImp;

import lab.mars.MCRRTImp.model.Polar;
import org.junit.Test;

public class TestPolar {
    @Test
    public void testPolar(){
        Polar polar1 = new Polar(10, 45);
        System.out.println(polar1.scale(10).r);
    }
}
