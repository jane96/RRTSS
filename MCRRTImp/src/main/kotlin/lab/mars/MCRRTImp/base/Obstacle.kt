package lab.mars.MCRRTImp.base

interface Obstacle<V : Vector<V>> {

    fun contains(o : V) : Boolean
}