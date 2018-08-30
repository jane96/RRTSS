package lab.mars.MCRRTImp.model

import lab.mars.MCRRTImp.base.Vector
import lab.mars.MCRRTImp.base.epsilonEquals
import lab.mars.MCRRTImp.base.random

class Space<V : Vector<V>> : Iterable<V> {

    private var lowerBound: V

    private var upperBound: V

    private var stepDelta: V

    private var dimensionCount = 0
    /**
     * contains
     */
    private var lowestValues: DoubleArray

    /**
     * exclude
     */
    private var highestValues: DoubleArray

    private var stepValues: DoubleArray

    private var iterable = true

    constructor(upperBound: V, lowerBound: V, step: V) {
        this.lowerBound = lowerBound.cpy()
        this.upperBound = upperBound.cpy()
        this.stepDelta = step.cpy()
        this.dimensionCount = step.dimensionCount
        lowestValues = DoubleArray(lowerBound.dimensionCount)
        highestValues = DoubleArray(upperBound.dimensionCount)
        for (i in 0 until dimensionCount) {
            lowestValues[i] = lowerBound.dimensions[i].value
            highestValues[i] = upperBound.dimensions[i].value
        }
        this.stepValues = DoubleArray(step.dimensionCount)
        for (i in 0 until dimensionCount) {
            val value = step.dimensions[i].value
            if (value == 0.0) {
                iterable = false
            }
            this.stepValues[i] = value
        }
    }

    constructor(upperBound: V, lowerBound: V) {
        this.lowerBound = lowerBound.cpy()
        this.upperBound = upperBound.cpy()
        this.stepDelta = upperBound.cpy().zero()
        this.dimensionCount = this.upperBound.dimensionCount
        lowestValues = DoubleArray(lowerBound.dimensionCount)
        highestValues = DoubleArray(upperBound.dimensionCount)
        for (i in 0 until dimensionCount) {
            lowestValues[i] = lowerBound.dimensions[i].value
            highestValues[i] = upperBound.dimensions[i].value
        }
        this.stepValues = DoubleArray(this.dimensionCount)
        for (i in 0 until dimensionCount) {
            this.stepValues[i] = 1.0
        }
        this.iterable = false
    }

    fun contains(point: V): Boolean {
        val dimensions = point.dimensions
        for (i in 0 until dimensionCount) {
            val value = dimensions[i].value
            if (value < lowestValues[i] || value > highestValues[i] || (value epsilonEquals highestValues[i])) {
                return false
            }
        }
        return true
    }

    val centroid : V
    get() {
        val centroid = lowerBound.cpy()
        if (iterable) {
            for (i in 0 until dimensionCount) {
                centroid.dimensions[i].value = ((highestValues[i] - lowestValues[i]) / (2.0 * stepValues[i])).toInt() * stepValues[i]
            }
        } else {
            for (i in 0 until dimensionCount) {
                centroid.dimensions[i].value = (highestValues[i] - lowestValues[i]) / 2.0
            }
        }
        return centroid
    }

    fun sample(): V {
        val sampled = lowerBound.cpy()
        for (i in 0 until dimensionCount) {
            val dimensionDelta = (highestValues[i] - lowestValues[i]) / stepValues[i]
            sampled.dimensions[i].value = (0.0 random dimensionDelta).toInt() * stepValues[i] + lowestValues[i]
        }
        return sampled
    }

    infix fun formalize(position: V): V {
        if (!contains(position)) {
            throw IllegalArgumentException("position to be formalized not included in this space")
        }
        if (!iterable) {
            return position.cpy()
        }
        val formalized = position.cpy()
        for (i in 0 until dimensionCount) {
            formalized.dimensions[i].value = (position.dimensions[i].value / stepValues[i]).toInt() * stepValues[i]
        }
        return formalized
    }

    fun cpy(): Space<V> {
        return Space(upperBound, lowerBound, stepDelta)
    }

    var step : V
    get() = stepDelta.cpy()
    set(value) {
        this.stepDelta = value.cpy()
        this.stepValues = DoubleArray(dimensionCount)
        this.iterable = true
        for (i in 0 until dimensionCount) {
            val dimensionValue = step.dimensions[i].value
            if (dimensionValue == 0.0) {
                this.iterable = false
            }
            this.stepValues[i] = dimensionValue
        }
    }


    override fun iterator(): Iterator<V> {
        if (!iterable) {
            throw IllegalArgumentException("cannot iterate vectors in a continuous space (stepValues not all set to non-zero)")
        }
        return VectorIterator()
    }

    val size: Long
    get() {
        var sum: Long = 1
        for (i in 0 until dimensionCount) {
            sum *= Math.ceil((highestValues[i] - lowestValues[i]) / stepValues[i]).toLong()
        }
        return sum
    }

    override fun toString(): String {
        return "Space{" +
                "lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                ", stepDelta=" + stepDelta +
                '}'.toString()
    }

    private inner class VectorIterator internal constructor() : Iterator<V> {

        private val cursor: V = lowerBound.cpy()

        private var hasNext = true

        override fun hasNext(): Boolean {
            return hasNext
        }

        override fun next(): V {
            val ret = cursor.cpy()
            (dimensionCount - 1 downTo -1).run {
                forEach { i ->
                    if (i < 0) {
                        hasNext = false
                        return@run
                    }
                    cursor.dimensions[i].value += stepValues[i]
                    if (cursor.dimensions[i].value >= highestValues[i]) {
                        cursor.dimensions[i].value = lowestValues[i]
                        return@forEach
                    }
                    return@run
                }
            }
            return ret
        }
    }
}
