package infrastructure.PolarBasedImp

import lab.mars.MCRRTImp.base.Dimension
import lab.mars.MCRRTImp.base.Vector
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.Objects


class Polar @JvmOverloads constructor(r: Double = 0.0, theta: Double = 0.0) : Vector<Polar>(r, (theta + 360) % 360) {
    override fun crs(other: Polar): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun epsilonEquals(other: Polar): Boolean {
        return epsilonEquals(other, 0.001)
    }

    private val _theta: Dimension = dimensions[1]
    private val _r: Dimension = dimensions[0]

    fun theta(): Double {
        return _theta.value
    }

    fun r(): Double {
        return _r.value
    }

    override fun angle(other: Polar): Double {
        return Math.abs(this._theta.value - other._theta.value)
    }

    override fun reverse(): Polar {
        this._r.value = -_r.value
        this._theta.value = 360 - _theta.value
        return this
    }

    override fun distance(o: Polar): Double {
        return Math.sqrt(this._r.value * this._r.value + o._r.value * o._r.value - 2.0 * this._r.value * o._r.value * Math.cos(Math.toRadians(angle(o))))
    }

    override fun distance2(o: Polar): Double {
        return this._r.value * this._r.value + o._r.value * o._r.value - 2.0 * this._r.value * o._r.value * Math.cos(Math.toRadians(angle(o)))
    }

    override fun normalize(): Polar {
        _r.value = 1.0
        return this
    }

    override fun cpy(): Polar {
        return Polar(_r.value, _theta.value)
    }

    override fun len(): Double {
        return _r.value
    }

    override fun len2(): Double {
        return _r.value * _r.value
    }

    override fun set(o: Polar): Polar {
        this._r.value = o._r.value
        this._theta.value = (_theta.value + 360) % 360
        return this
    }

    override fun translate(v: Polar): Polar {
        var thisX = _r.value * Math.cos(Math.toRadians(_theta.value))
        var thisY = _r.value * Math.sin(Math.toRadians(_theta.value))
        val otherX = v._r.value * Math.cos(Math.toRadians(v._theta.value))
        val otherY = v._r.value * Math.sin(Math.toRadians(v._theta.value))
        thisX += otherX
        thisY += otherY
        _r.value = Math.sqrt(thisX * thisX + thisY * thisY)
        _theta.value = Math.toDegrees(Math.atan2(thisY, thisX))
        return this
    }

    override fun dot(v: Polar): Double {
        throw NotImplementedException()
    }

    override fun scale(scalar: Double): Polar {
        this._r.value *= scalar
        return this
    }

    override fun scale(v: Polar): Polar {
        this._r.value *= v._r.value
        this._theta.value *= v._theta.value
        return this
    }

    fun lerp(target: Polar, coefficient: Double): Polar {
        throw NotImplementedException()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val polar = o as Polar?
        return epsilonEquals(polar, 0.001)
    }

    override fun hashCode(): Int {

        return Objects.hash(_theta.value, _r.value)
    }

    fun epsilonEquals(other: Polar?, epsilon: Double): Boolean {
        return if (other == null) false else Math.abs(this._r.value - other._r.value) <= epsilon && Math.abs(this._theta.value - other._theta.value) <= epsilon
    }

    override fun toString(): String {
        return "Polar{" +
                "_theta=" + _theta.value +
                ", _r=" + _r.value +
                '}'.toString()
    }

    override fun rotate(angle: Double): Polar {
        this._theta.value += angle
        return this
    }

    override fun zero(): Polar {
        this._theta.value = 0.0
        this._r.value = 0.0
        return this
    }
}
