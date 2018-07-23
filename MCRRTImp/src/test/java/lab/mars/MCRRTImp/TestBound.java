package lab.mars.MCRRTImp;

import org.junit.Test;

public class TestBound {

    @Test
    public void testContains() {
        Bound bound = new Bound(100, 100);
        assert bound.contains(new Vector2(100.1, 100));
    }
}
