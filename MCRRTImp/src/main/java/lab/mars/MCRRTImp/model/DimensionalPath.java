package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Path;
import lab.mars.RRTBase.WayPoint;

import java.util.Iterator;
import java.util.LinkedList;

public class DimensionalPath< W extends WayPoint> implements Path<W> {

    public boolean ended = false;

    public long utility = 0;

    private LinkedList<W> pathStorage = new LinkedList<>();

    public W next(W current) {
        int idx = pathStorage.indexOf(current);
        if (idx >= pathStorage.size() - 1) {
            return null;
        }
        return pathStorage.get(idx + 1);
    }

    public W last(W current) {
        int idx = pathStorage.indexOf(current);
        if (idx == 1) {
            return null;
        }
        return pathStorage.get(idx - 1);
    }

    public int indexOf(W w) {
        return pathStorage.indexOf(w);
    }

    public DimensionalPath<W> cpy() {
        DimensionalPath<W> ret = new DimensionalPath<>();
        this.forEach(ret::add);
        return ret;
    }

    @Override
    public int size() {
        return pathStorage.size();
    }

    @Override
    public void add(W W) {
        pathStorage.addLast(W);
    }


    public void add(int idx, W wayPoint) {
        pathStorage.set(idx, wayPoint);
    }

    public W get(int idx) {
        return pathStorage.get(idx);
    }

    @Override
    public boolean empty() {
        return pathStorage.isEmpty();
    }

    public W removeAt(int idx) {
        return pathStorage.remove(idx);
    }

    @Override
    public void remove(W current) {
        pathStorage.removeFirstOccurrence(current);
    }

    @Override
    public W start() {
        return pathStorage.peekFirst();
    }

    @Override
    public W end() {
        return pathStorage.peekLast();
    }

    @Override
    public Iterator<W> iterator() {
        return pathStorage.iterator();
    }
}
