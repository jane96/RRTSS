//package lab.mars.MCRRTImp;
//
//import lab.mars.MCRRTImp.model.DimensionalPath;
//import lab.mars.MCRRTImp.model.DimensionalWayPoint;
//import org.junit.Test;
//
//public class TestPath2D {
//
//    @Test
//    public void testAddRemove() {
//        DimensionalPath<DimensionalWayPoint> path = new DimensionalPath<>();
//
//        path.add(new DimensionalWayPoint(0, 0, 1, 0, 0, 0));
//        assert path.end().equals(path.start());
//        assert path.start().equals(new DimensionalWayPoint(0, 0, 1, 0, 0, 0));
//
//        path.remove(new DimensionalWayPoint(0, 0, 1, 0, 0, 0));
//        assert path.size() == 0 && path.empty();
//    }
//
//    @Test
//    public void testIdenticalElementAdd() {
//        DimensionalPath<DimensionalWayPoint> path = new DimensionalPath<>();
//        DimensionalWayPoint wayPoint = new DimensionalWayPoint(0, 0, 1, 0, 0, 0);
//        for (int i = 0; i < 100; i++) {
//            path.add(wayPoint);
//        }
//        assert path.size() == 100;
//        path.removeAt(0);
//        assert path.size() == 99;
//        wayPoint.radius = 100;
//        assert path.end().radius == 100;
//    }
//
//    @Test
//    public void testPerformance() {
//        long time = System.currentTimeMillis();
//        DimensionalPath<DimensionalWayPoint> path = new DimensionalPath<>();
//        for (int i = 0; i < 100000; i++) {
//            path.add(new DimensionalWayPoint(100, 100, 100, 0, 0, 0));
//        }
//        long addFinish = System.currentTimeMillis();
//        assert path.size() == 100000;
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
