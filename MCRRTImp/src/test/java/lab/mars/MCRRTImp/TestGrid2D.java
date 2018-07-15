package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        Grid2D grid = new Grid2D(10, 10, 10, 10, new DummyOriginProvider());
        assert grid.grid.length == 10;
        assert grid.grid[0].length == 10;
    }

    @Test
    public void testRecordAndSample() {
        Grid2D grid = new Grid2D(10, 10, 10, 10, new DummyOriginProvider());
        assert !grid.sample(new Vector2(0, 0));
        grid.record(new Vector2(0, 0));
        assert grid.sample(new Vector2(0, 0));
    }

    private String scanResult;

    @Before
    public void scanResult() {
        scanResult =
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "*******************000000000000*******************\n" +
                "*****************0000000000000000*****************\n" +
                "***************00000000000000000000***************\n" +
                "*************000000000000000000000000*************\n" +
                "************00000000000000000000000000************\n" +
                "***********0000000000000000000000000000***********\n" +
                "**********000000000000000000000000000000**********\n" +
                "*********00000000000000000000000000000000*********\n" +
                "*********00***000000000000000000000000000*********\n" +
                "********000***0000000000000000000000000000********\n" +
                "********0000000000000000000000000000000000********\n" +
                "*******000000000000000000000000000000000000*******\n" +
                "*******000000000000000000000000000000000000*******\n" +
                "******00000000000000000000000000000000000000******\n" +
                "******0000000000000000******0000000000000000******\n" +
                "******000000000000000********000000000000000******\n" +
                "******00000000000000**********00000000000000******\n" +
                "******00000000000000**********00000000000000******\n" +
                "******00000000000000**********000000000000000*****\n" +
                "******00000000000000**********000000000000000*****\n" +
                "******00000000000000**********000000000000000*****\n" +
                "******00000000000000**********00000000000000******\n" +
                "******000000000000000********000000000000000******\n" +
                "******0000000000000000******0000000000000000******\n" +
                "******000000000000000000000000*****000000000******\n" +
                "*******00000000000000000000000*****00000000*******\n" +
                "*******00000000000000000000000*****00000000*******\n" +
                "********0000000000000000000000*****00000000*******\n" +
                "********0000000000000000000000*****0000000********\n" +
                "*********00000000000000000000000000000000*********\n" +
                "*********00000000000000000000000000000000*********\n" +
                "**********000000000000000000000000000000**********\n" +
                "***********0000000000000000000000000000***********\n" +
                "************00000000000000000000000000************\n" +
                "*************000000000000000000000000*************\n" +
                "***************00000000000000000000***************\n" +
                "*****************00000000000000000****************\n" +
                "*******************000000000000*******************\n" +
                "************************000***********************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n" +
                "**************************************************\n";
    }

    @Test
    public void testScan() {
        Grid2D grid = new Grid2D(100, 100, 50, 50, () -> new Vector2(0, 0));
        EyeSight eyeSight = new EyeSight(() -> new Vector2(50, 50), () -> 40.0);
        CircleObstacle obstacle = new CircleObstacle(50, 50, 10, 10);
        CircleObstacle obstacle2 = new CircleObstacle(30, 25, 2, 1);
        CircleObstacle obstacle3 = new CircleObstacle(65, 65, 5, 1);
        CircleObstacle obstacle4 = new CircleObstacle(200, 200, 5, 1);

        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(eyeSight);
        obstacles.add(obstacle);
        obstacles.add(obstacle2);
        obstacles.add(obstacle3);
        obstacles.add(obstacle4);
        long start = System.currentTimeMillis();
        grid.scan(obstacles);
        System.out.println("grid 2d scan used : " + (System.currentTimeMillis() - start) + "ms");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < grid.grid.length; i++) {
            for (int j = 0; j < grid.grid[i].length; j++) {
                if (grid.grid[i][j]) {
                    result.append("*");
                } else {
                    result.append("0");
                }
            }
            result.append("\n");
        }
//        System.out.println(result.toString());
        assert result.toString().contentEquals(scanResult);
    }


}
