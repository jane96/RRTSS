package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Provider;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestGrid2D {

    private class DummyOriginProvider implements Provider<Vector2> {

        Vector2 origin = new Vector2();

        @Override
        public Vector2 provide() {
            return origin;
        }
    }

    @Test
    public void testCreate() {
        Grid2D grid = new Grid2D(10, 10, 5, new DummyOriginProvider());
        assert grid.grid.length == 2;
        assert grid.grid[0].length == 2;
    }

    @Test
    public void testRecordAndSample() {
        Grid2D grid = new Grid2D(10, 10, 3, new DummyOriginProvider());
        assert !grid.sample(new Vector2(0, 0));
        grid.record(new Vector2(0,0));
        assert grid.sample(new Vector2(0, 0));
    }


}
