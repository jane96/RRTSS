package lab.mars.MCRRTImp.base

open class WayPoint<V : Vector<V>>(val origin : V, val radius : Double, val velocity : V) {



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WayPoint<V>

        if (!(origin epsilonEquals other.origin)) return false
        if (!(radius epsilonEquals other.radius)) return false
        if (!(velocity epsilonEquals other.velocity)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + radius.hashCode()
        result = 31 * result + velocity.hashCode()
        return result
    }

    override fun toString(): String {
        return "WayPoint($origin, $radius, $velocity)"
    }

}