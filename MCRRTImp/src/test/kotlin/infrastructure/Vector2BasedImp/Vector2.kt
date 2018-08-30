package infrastructure.Vector2BasedImp

import lab.mars.MCRRTImp.base.*

import java.util.Objects


class Vector2 @JvmOverloads constructor(x: Double = 0.0, y: Double = 0.0) : Vector<Vector2>(x, y) {

    private val x: Dimension = dimensions[0]

    private val y: Dimension = dimensions[1]


    fun x(): Double {
        return x.value
    }

    fun y(): Double {
        return y.value
    }

    override fun distance(o: Vector2): Double {
        val dx = x.value - o.x.value
        val dy = y.value - o.y.value
        return Math.sqrt(dx * dx + dy * dy)
    }

    override fun distance2(o: Vector2): Double {
        val dx = x.value - o.x.value
        val dy = y.value - o.y.value
        return dx * dx + dy * dy
    }

    override fun normalize(): Vector2 {
        val length = len()
        x.value = x.value / length
        y.value = y.value / length
        return this
    }

    override fun cpy(): Vector2 {
        return Vector2(x.value, y.value)
    }

    override fun len(): Double {
        return Math.sqrt(x.value * x.value + y.value * y.value)
    }

    override fun len2(): Double {
        return x.value * x.value + y.value * y.value
    }

    override fun set(o: Vector2): Vector2 {
        this.x.value = o.x.value
        this.y.value = o.y.value
        return this
    }

    fun subtract(v: Vector2): Vector2 {
        x.value = x.value - v.x.value
        y.value = y.value - v.y.value
        return this
    }

    override fun translate(v: Vector2): Vector2 {
        x.value = x.value + v.x.value
        y.value = y.value + v.y.value
        return this
    }

    override fun dot(v: Vector2): Double {
        return x.value * v.x.value + y.value * v.y.value
    }

    override fun scale(scalar: Double): Vector2 {
        this.x.value = x.value * scalar
        this.y.value = y.value * scalar
        return this
    }

    fun scale(x: Double, y: Double): Vector2 {
        this.x.value *= x
        this.y.value *= y
        return this
    }

    override fun scale(v: Vector2): Vector2 {
        this.x.value = x.value * v.x.value
        this.y.value = y.value * v.y.value
        return this
    }

    override fun angle(other: Vector2): Double {
        var value = this.dot(other) / (len() * other.len())
        if (value < -1) {
            value = -1.0
        } else {
            if (value > 1) {
                value = 1.0
            }
        }
        return Math.acos(value) * R2D
    }

    override fun reverse(): Vector2 {
        this.x.value = -this.x.value
        this.y.value = -this.y.value
        return this
    }

    override fun rotate(angle: Double): Vector2 {
        val cos = Math.cos(angle * D2R)
        val sin = Math.sin(angle * D2R)
        val xN = x.value * cos - y.value * sin
        val yN = x.value * sin + y.value * cos
        x.value = xN
        y.value = yN
        return this
    }

    fun lerp(target: Vector2, coefficient: Double): Vector2 {
        val invert = 1.0f - coefficient
        this.x.value = x.value * invert + target.x.value * coefficient
        this.y.value = y.value * invert + target.y.value * coefficient
        return this
    }

    fun epsilonEquals(other: Vector2?, epsilon: Double): Boolean {
        if (other == null) return false
        if (Math.abs(other.x.value - x.value) > epsilon) return false
        return Math.abs(other.y.value - y.value) <= epsilon
    }

    override fun zero(): Vector2 {
        this.x.value = 0.0
        this.y.value = 0.0
        return this
    }

    override fun toString(): String {
        return "Vector2{" +
                "x.value=" + x.value +
                ", y.value=" + y.value +
                '}'.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val vector2 = other as Vector2?
        return (vector2!!.x.value epsilonEquals x.value) && (vector2.y.value epsilonEquals y.value)
    }

    override fun hashCode(): Int {

        return Objects.hash(x.value, y.value)
    }

    operator fun set(x: Double, y: Double): Vector2 {
        this.x.value = x
        this.y.value = y
        return this
    }

    override fun epsilonEquals(other: Vector2): Boolean {
        return epsilonEquals(other, 0.0001)
    }
}
