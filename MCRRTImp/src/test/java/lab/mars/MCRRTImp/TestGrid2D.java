package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGrid2D {

    private String scanResult;

    private List<Vector2> testGridAvailableBoundResult = new ArrayList<>();

    private Grid2D commonTestGrid;
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

        commonTestGrid = new Grid2D(100, 100, 2, Vector2::new);
        EyeSight eyeSight = new EyeSight(new Vector2(0, 50), new Vector2(0, 1), 40, 10);
        CircleObstacle obstacle = new CircleObstacle(50, 50, 10);
        CircleObstacle obstacle2 = new CircleObstacle(30, 25, 2);
        CircleObstacle obstacle3 = new CircleObstacle(65, 65, 5);
        CircleObstacle obstacle4 = new CircleObstacle(200, 200, 5);

        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(eyeSight);
        obstacles.add(obstacle);
        obstacles.add(obstacle2);
        obstacles.add(obstacle3);
        obstacles.add(obstacle4);
        long start = System.currentTimeMillis();
        commonTestGrid.scan(obstacles);
        System.out.println("common test grid 2d scan used : " + (System.currentTimeMillis() - start) + "ms");
        testGridAvailableBoundResult.add(new Vector2(5.5,0.5));
        testGridAvailableBoundResult.add(new Vector2(5.5,19.5));
        testGridAvailableBoundResult.add(new Vector2(6.5,0.5));
        testGridAvailableBoundResult.add(new Vector2(6.5,19.5));
        testGridAvailableBoundResult.add(new Vector2(7.5,0.5));
        testGridAvailableBoundResult.add(new Vector2(7.5,19.5));
        testGridAvailableBoundResult.add(new Vector2(8.5,0.5));
        testGridAvailableBoundResult.add(new Vector2(8.5,19.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,0.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,19.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,1.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,2.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,3.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,4.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,15.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,16.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,17.5));
        testGridAvailableBoundResult.add(new Vector2(9.5,18.5));

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
        assert grid.grid.length == 10;
        assert grid.grid[0].length == 5;
    }

    @Test
    public void testRecordAndCheck() {
        Grid2D grid = new Grid2D(10, 10, 1, new DummyOriginProvider());
        assert !grid.check(new Vector2(0, 0));
        grid.record(new Vector2(0, 0));
        assert grid.check(new Vector2(0, 0));
    }

    @Test
    public void testGridCenter() {
        Vector2 center = commonTestGrid.gridCenter();
        assert  center.epsilonEquals(new Vector2(50, 50), 0.001);
    }

    @Test
    public void testTransformToCellCenter() {
        Vector2 centerFromGridCell = commonTestGrid.transformToCellCenter(new GridCell(20, 20));
        Vector2 centerFromXY = commonTestGrid.transformToCellCenter(20, 20);
        assert centerFromGridCell.epsilonEquals(new Vector2(41, 41), 0.001);
        assert centerFromXY.epsilonEquals(new Vector2(41, 41), 0.001);
    }

    @Test
    public void testGetGridAvailableBound() {
        List<Vector2> availableBound = commonTestGrid.gridAvailableBound();
        assert availableBound.size() == 0;
        Grid2D testGrid = new Grid2D(10, 20, 1, Vector2::new);
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new CircleObstacle(0, 10, 10));
        testGrid.scan(obstacles);
        String stringed = testGrid.toString();
        System.out.println(stringed);
        availableBound = testGrid.gridAvailableBound();
        System.out.println(availableBound.size());
        assert availableBound.size() == 18;
        for (int i = 0; i < availableBound.size(); i++) {
            assert availableBound.get(i).equals(testGridAvailableBoundResult.get(i));
        }
    }

    @Test
    public void testFindNearestGridCenter() {
        Grid2D testGrid = new Grid2D(10, 20, 1, Vector2::new);
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new CircleObstacle(0, 10, 10));
        testGrid.scan(obstacles);
        String stringed = testGrid.toString();
        System.out.println(stringed);
        Vector2 nearest = testGrid.findNearestGridCenter(new Vector2(-1, 11));
        System.out.println(nearest);
        assert nearest.epsilonEquals(new Vector2(5.5, 19.5), 0.001);
        nearest = testGrid.findNearestGridCenter(new Vector2(1, 10));
        assert nearest == null;
        nearest = testGrid.findNearestGridCenter(new Vector2(5.5, 19.5));
        assert nearest.epsilonEquals(new Vector2(5.5, 19.5), 0.001);
    }

    @Test
    public void testScanAndToString() {
        String stringGrid = commonTestGrid.toString();
        System.out.println(stringGrid);
        assert stringGrid.contentEquals(scanResult);
    }

    @Test
    public void testSample() {
        int  i =0;
        while (i < 100) {
            Vector2 sampled = commonTestGrid.sample();
            System.out.println(sampled);
            assert !commonTestGrid.check(sampled);
            i ++;
        }
    }

    @Test
    public void testCellSize() {
        double gridCellEdgeLength = commonTestGrid.cellSize();
        assert MathUtil.epsilonEquals(gridCellEdgeLength, 2);
    }

    @Test
    public void testToCellCenter() {
        Vector2 test = new Vector2(50, 50);
        Vector2 centered = commonTestGrid.transformToCellCenter(test);
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
