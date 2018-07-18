package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGrid2D {

    private static String scanResult;

    private static Grid2D gridWorld ;
    @BeforeClass
    public static void scanResult() {
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

        gridWorld = new Grid2D(100, 100, 2, () -> new Vector2(0, 0));
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
        gridWorld.scan(obstacles);
        System.out.println("grid 2d scan used : " + (System.currentTimeMillis() - start) + "ms");
    }


    private class DummyOriginProvider implements Provider<Vector2> {

        Vector2 origin = new Vector2();

        @Override
        public Vector2 provide() {
            return origin;
        }
    }


    @Test
    public void testCreate() {
        Grid2D grid = new Grid2D(10, 5, 1, new DummyOriginProvider());
        assert grid.grid.length == 5;
        assert grid.grid[0].length == 10;
    }

    @Test
    public void testRecordAndCheck() {
        Grid2D grid = new Grid2D(10, 10, 1, new DummyOriginProvider());
        assert !grid.check(new Vector2(0, 0));
        grid.record(new Vector2(0, 0));
        assert grid.check(new Vector2(0, 0));
    }

    @Test
    public void testScan() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < gridWorld.grid.length; i++) {
            for (int j = 0; j < gridWorld.grid[i].length; j++) {
                if (gridWorld.grid[i][j]) {
                    result.append("*");
                } else {
                    result.append("0");
                }
            }
            result.append("\n");
        }
        System.out.println(result.toString());
        assert result.toString().contentEquals(scanResult);
    }

    @Test
    public void testSample() {
        int  i =0;
        while (i < 100) {
            Vector2 sampled = gridWorld.sample();
            System.out.println(sampled);
            assert !gridWorld.check(sampled);
            i ++;
        }
    }

    @Test
    public void testCellSize() {
        double gridCellEdgeLength = gridWorld.cellSize();
        assert MathUtil.epsilonEquals(gridCellEdgeLength, 2);
    }

    @Test
    public void testToCellCenter() {
        Vector2 test = new Vector2(50, 50);
        Vector2 centered = gridWorld.transformToCellCenter(test);
        assert MathUtil.epsilonEquals(centered.x, 51);
        assert MathUtil.epsilonEquals(centered.y, 51);

    }

    @Test
    public void testScanGUI() {
//        Application gui = new Application() {
//            @Override
//            public void start(Stage primaryStage) throws Exception {
//                HBox root = new HBox();
//                Canvas canvas = new Canvas(1024, 768);
//                root.getChildren().add(canvas);
//                Scene scene = new Scene(root, 1024, 768);
//                primaryStage.setScene(scene);
//                GraphicsContext pencil = canvas.getGraphicsContext2D();
//                Grid2D grid = new Grid2D(100, 100, 50, 100, () -> new Vector2(0, 0));
//                EyeSight eyeSight = new EyeSight(() -> new Vector2(50, 50), () -> 40.0);
//                CircleObstacle obstacle = new CircleObstacle(50, 50, 10, 10);
//                CircleObstacle obstacle2 = new CircleObstacle(30, 25, 2, 1);
//                CircleObstacle obstacle3 = new CircleObstacle(65, 65, 5, 1);
//                CircleObstacle obstacle4 = new CircleObstacle(200, 200, 5, 1);
//
//                List<Obstacle> obstacles = new ArrayList<>();
//                obstacles.add(eyeSight);
//                obstacles.add(obstacle);
//                obstacles.add(obstacle2);
//                obstacles.add(obstacle3);
//                obstacles.add(obstacle4);
//                long start = System.currentTimeMillis();
//                grid.scan(obstacles);
//                System.out.println("grid 2d scan used : " + (System.currentTimeMillis() - start) + "ms");
//
//                primaryStage.show();
//            }
//        }
    }


}
