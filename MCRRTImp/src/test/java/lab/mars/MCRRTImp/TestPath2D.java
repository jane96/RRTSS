package lab.mars.MCRRTImp;

import lab.mars.MCRRTImp.model.Path2D;
import lab.mars.MCRRTImp.model.WayPoint2D;
import org.junit.Test;

public class TestPath2D {

    @Test
    public void testAddRemove() {
        Path2D<WayPoint2D> path = new Path2D<>();

        path.add(new WayPoint2D(0, 0, 1, 0, 0));
        assert path.end().equals(path.start());
        assert path.start().equals(new WayPoint2D(0, 0, 1, 0, 0));

        path.remove(new WayPoint2D(0, 0, 1, 0, 0));
        assert path.size() == 0 && path.empty();
    }

    @Test
    public void testIdenticalElementAdd() {
        Path2D<WayPoint2D> path = new Path2D<>();
        WayPoint2D wayPoint = new WayPoint2D(0, 0, 1, 0, 0);
        for (int i = 0; i < 100; i++) {
            path.add(wayPoint);
        }
        assert path.size() == 100;
        path.removeAt(0);
        assert path.size() == 99;
        wayPoint.radius = 100;
        assert path.end().radius == 100;
    }

    @Test
    public void testPerformance() {
        long time = System.currentTimeMillis();
        Path2D<WayPoint2D> path = new Path2D<>();
        for (int i = 0; i < 100000; i++) {
            path.add(new WayPoint2D(100, 100, 100, 0, 0));
        }
        long addFinish = System.currentTimeMillis();
        assert path.size() == 100000;
        System.out.println("add spends " + (addFinish - time) + " ms");
        time = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            path.removeAt(0);
        }
        long removeFinish = System.currentTimeMillis();
        assert path.empty();
        System.out.println("delete spends " + (removeFinish - time) + " ms");
    }

}
