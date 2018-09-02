//package lab.mars.MCRRTImp;
//
//import lab.mars.MCRRTImp.model.Path;
//import lab.mars.MCRRTImp.model.WayPoint;
//import org.junit.Test;
//
//public class TestPath2D {
//
//    @Test
//    public void testAddRemove() {
//        Path<WayPoint> path = new Path<>();
//
//        path.add(new WayPoint(0, 0, 1, 0, 0, 0));
//        assert path.end().equals(path.start());
//        assert path.start().equals(new WayPoint(0, 0, 1, 0, 0, 0));
//
//        path.remove(new WayPoint(0, 0, 1, 0, 0, 0));
//        assert path.childrenSize() == 0 && path.empty();
//    }
//
//    @Test
//    public void testIdenticalElementAdd() {
//        Path<WayPoint> path = new Path<>();
//        WayPoint wayPoint = new WayPoint(0, 0, 1, 0, 0, 0);
//        for (int i = 0; i < 100; i++) {
//            path.add(wayPoint);
//        }
//        assert path.childrenSize() == 100;
//        path.removeAt(0);
//        assert path.childrenSize() == 99;
//        wayPoint.radius = 100;
//        assert path.end().radius == 100;
//    }
//
//    @Test
//    public void testPerformance() {
//        long time = System.currentTimeMillis();
//        Path<WayPoint> path = new Path<>();
//        for (int i = 0; i < 100000; i++) {
//            path.add(new WayPoint(100, 100, 100, 0, 0, 0));
//        }
//        long addFinish = System.currentTimeMillis();
//        assert path.childrenSize() == 100000;
//        System.out.println("translate spends " + (addFinish - time) + " ms");
//        time = System.currentTimeMillis();
//        for (int i = 0; i < 100000; i++) {
//            path.removeAt(0);
//        }
//        long removeFinish = System.currentTimeMillis();
//        assert path.empty();
//        System.out.println("delete spends " + (removeFinish - time) + " ms");
//    }
//
//}
