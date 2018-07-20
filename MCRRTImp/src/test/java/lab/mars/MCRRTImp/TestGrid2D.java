package lab.mars.MCRRTImp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import lab.mars.RRTBase.Obstacle;
import lab.mars.RRTBase.Provider;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestGrid2D {

    private String scanResult;

    private List<Vector2> testGridAvailableBoundResult = new ArrayList<>();

    private Grid2D commonTestGrid;
    private List<Obstacle<Vector2>> commonTestObstacles = new ArrayList<>();
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

        commonTestGrid = new Grid2D(100, 100, 2);
        CircleBound bound = new CircleBound(50, 50, 40);
        CircleObstacle obstacle = new CircleObstacle(50, 50, 10);
        CircleObstacle obstacle2 = new CircleObstacle(30, 25, 2);
        CircleObstacle obstacle3 = new CircleObstacle(65, 65, 5);
        CircleObstacle obstacle4 = new CircleObstacle(200, 200, 5);
        commonTestObstacles.add(bound);
        commonTestObstacles.add(obstacle);
        commonTestObstacles.add(obstacle2);
        commonTestObstacles.add(obstacle3);
        commonTestObstacles.add(obstacle4);
        long start = System.currentTimeMillis();
        commonTestGrid.scan(commonTestObstacles);
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


    @Test
    public void testCreate() {
        Grid2D grid = new Grid2D(10, 5, 1);
        assert grid.grid.length == 10;
        assert grid.grid[0].length == 5;
    }

    @Test
    public void testRecordAndCheck() {
        Grid2D grid = new Grid2D(10, 10, 1);
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
        Grid2D testGrid = new Grid2D(10, 20, 1);
        List<Obstacle<Vector2>> commonTestObstacles = new ArrayList<>();
        commonTestObstacles.add(new CircleObstacle(0, 10, 10));
        testGrid.scan(commonTestObstacles);
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
        Grid2D testGrid = new Grid2D(10, 20, 1);
        List<Obstacle<Vector2>> commonTestObstacles = new ArrayList<>();
        commonTestObstacles.add(new CircleObstacle(0, 10, 10));
        testGrid.scan(commonTestObstacles);
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
        System.out.println(centered);
        assert MathUtil.epsilonEquals(centered.x, 51);
        assert MathUtil.epsilonEquals(centered.y, 51);
    }

    @Test
    public void testIterator() {
        List<Vector2> foreach = new ArrayList<>();
        commonTestGrid.forEach(foreach::add);
        assert foreach.size() == 2500;
    }



}
