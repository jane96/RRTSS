package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Provider;
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
        Grid2D grid = new Grid2D(2, 3, 3, 5, new DummyOriginProvider());
        assert grid.grid.length == 2;
        assert grid.grid[0].length == 3;
    }

    @Test
    public void testRecordAndSample() {
        DummyOriginProvider provider = new DummyOriginProvider();
        Grid2D grid = new Grid2D(3, 3, 3, 5, provider);
        for (int movement = 0; movement < 10000; movement++) {
            double x = MathUtil.random(movement, 3 + movement);
            double y = MathUtil.random(movement, 5 + movement);
            Vector2 position = new Vector2(x, y);
            grid.record(position);
            System.out.println(provider.origin);
            System.out.println(position);
            System.out.println();
            provider.origin.add(new Vector2(1, 1));
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                System.out.print((int) grid.grid[c][r] + ",");
            }
            System.out.println();
        }

        double value = grid.sample(new Vector2(2.99 + 10000, 4.99 + 10000));
        System.out.println(value);
    }


}
