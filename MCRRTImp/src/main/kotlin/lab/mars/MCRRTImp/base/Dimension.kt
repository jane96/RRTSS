package lab.mars.MCRRTImp.base

class Dimension(var value : Double = 0.0) {

    infix fun assign(double: Double) {
        this.value = double
    }
}