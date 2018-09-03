package lab.mars.MCRRTImp.base


const val D2R = Math.PI  / 180.0
const val R2D = 180.0 / Math.PI

infix fun Double.epsilonEquals(o: Double): Boolean {
    return Math.abs(this - o) <= 0.0000000000001
}


infix fun Double.random(up: Double): Double {
    return if (this == up) this
    else Math.random() * (up - this) + this
}