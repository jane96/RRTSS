package lab.mars.MCRRTImp.model;

import lab.mars.RRTBase.Vector;

import java.util.Objects;

public class GridCell<V extends Vector<V>> {

    V cellIdx;

    public GridCell(V cellIdx) {
        this.cellIdx = cellIdx;
    }

    @Override
    public String toString() {
        return "GridCell{" +
                "cellIdx=" + cellIdx +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        try {
            GridCell<V> gridCell = (GridCell<V>) o;
            return cellIdx.epsilonEquals(gridCell.cellIdx, 0.001);
        }catch (ClassCastException ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellIdx);
    }
}
