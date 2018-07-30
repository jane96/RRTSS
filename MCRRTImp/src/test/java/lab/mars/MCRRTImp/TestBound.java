package lab.mars.MCRRTImp;

import lab.mars.MCRRTImp.Vector2BasedImp.Bound;
import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import org.junit.Test;

public class TestBound {

    @Test
    public void testContains() {
        Bound bound = new Bound(100, 100);
        assert bound.contains(new Vector2(100.1, 100));
    }
}
