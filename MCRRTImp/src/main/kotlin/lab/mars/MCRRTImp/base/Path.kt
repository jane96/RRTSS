package lab.mars.MCRRTImp.base

import java.util.*


class Path<W : WayPoint<*>> : Iterable<W> {

    var from = 0

    var to = 0

    operator fun get(idx: Int): W {
        return pathStorage[idx]
    }

    val start: W
        get() = pathStorage.peekFirst()
    val end: W
        get() = pathStorage.peekLast()

    var finished = false

    var utility = 0.0

    fun isEmpty(): Boolean {
        return pathStorage.isEmpty()
    }

    fun nextOf(current: W): W {
        return pathStorage[pathStorage.indexOf(current) + 1]
    }

    fun lastOf(current: W): W {
        return pathStorage[pathStorage.indexOf(current) - 1]
    }

    private val pathStorage = LinkedList<W>()

    val size: Int
        get() = pathStorage.size

    fun add(wayPoint: W) {
        pathStorage.addLast(wayPoint)
    }

    fun remove(current: W) {
        pathStorage.removeFirstOccurrence(current)
    }

    fun cpy(): Path<W> {
        val ret = Path<W>()
        ret.utility = this.utility
        this.forEach { ret.add(it) }
        return ret
    }

    fun clear() {
        this.pathStorage.clear()
    }


    override fun iterator(): Iterator<W> {
        return pathStorage.iterator()
    }

}



