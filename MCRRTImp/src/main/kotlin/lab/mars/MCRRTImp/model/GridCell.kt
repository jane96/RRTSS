package lab.mars.MCRRTImp.model

import lab.mars.MCRRTImp.base.Vector

class GridCell<V : Vector<V>>(val cellIdx : V) {



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GridCell<V>

        if (!(cellIdx epsilonEquals other.cellIdx)) return false

        return true
    }

    override fun hashCode(): Int {
        return cellIdx.hashCode()
    }

    override fun toString(): String {
        return "GridCell(cellIdx=$cellIdx)"
    }
}