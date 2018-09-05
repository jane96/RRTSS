package lab.mars.MCRRTImp.base

abstract class Vector<T : Vector<T>>(vararg values : Double) : Iterable<Dimension>{
    var dimensions : Array<Dimension> protected set

    var dimensionCount : Int protected set

    init {
        dimensionCount = values.size
        dimensions = Array(dimensionCount, { Dimension() })
        values.forEachIndexed { idx, d -> dimensions[idx] assign d }
    }

    abstract fun set(o : T) : T

    abstract fun distance(o : T) : Double

    abstract fun distance2(o : T) : Double

    abstract fun normalize() : T

    abstract fun cpy() : T

    abstract fun len() : Double

    abstract fun len2() : Double

    abstract fun translate(v : T) : T

    abstract fun dot(v : T) : Double

    abstract fun scale(scalar : Double) : T

    abstract fun scale(v : T) : T

    abstract override fun equals(other : Any?) : Boolean

    abstract override fun hashCode() : Int

    abstract override fun toString() : String

    abstract infix fun epsilonEquals(other : T) : Boolean

    abstract fun rotate(angle : Double) : T

    abstract fun zero() : T

    abstract fun angle(other : T) : Double

    abstract fun reverse() : T

    override fun iterator(): Iterator<Dimension> {
        return dimensions.iterator()
    }

    operator fun get(idx : Int) : Double {
        return dimensions[idx].value
    }

    abstract fun crs(other : T): Double
}