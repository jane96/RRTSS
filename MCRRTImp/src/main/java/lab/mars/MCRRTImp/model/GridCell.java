package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Vector;

public class GridCell<V extends Vector<V>> {

    V cellIdx;

    public GridCell(V cellIdx) {
        this.cellIdx = cellIdx;
    }
}
