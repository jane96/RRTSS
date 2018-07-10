package lab.mars.MCRRTImp;

import lab.mars.RRTBase.Path;

import java.util.LinkedList;

public class Path2D implements Path<WayPoint2D> {


    private LinkedList<WayPoint2D> pathStorage = new LinkedList<>();

    public WayPoint2D next(WayPoint2D current) {
        int idx = pathStorage.indexOf(current);
        if (idx >= pathStorage.size() - 1) {
            return null;
        }
        return pathStorage.get(idx + 1);
    }

    public WayPoint2D last(WayPoint2D current) {
        int idx = pathStorage.indexOf(current);
        if (idx == 1) {
            return null;
        }
        return pathStorage.get(idx - 1);
    }

    @Override
    public int size() {
        return pathStorage.size();
    }

    @Override
    public void add(WayPoint2D wayPoint2D) {
        pathStorage.addLast(wayPoint2D);
    }


    public void add(int idx, WayPoint2D wayPoint) {
        pathStorage.set(idx, wayPoint);
    }

    public WayPoint2D get(int idx) {
        return pathStorage.get(idx);
    }

    @Override
    public boolean empty() {
        return pathStorage.isEmpty();
    }

    public WayPoint2D removeAt(int idx) {
        return pathStorage.remove(idx);
    }

    @Override
    public void remove(WayPoint2D current) {
        pathStorage.removeFirstOccurrence(current);
    }

    @Override
    public WayPoint2D start() {
        return pathStorage.peekFirst();
    }

    @Override
    public WayPoint2D end() {
        return pathStorage.peekLast();
    }
}
