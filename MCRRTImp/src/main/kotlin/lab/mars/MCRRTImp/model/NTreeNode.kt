package lab.mars.MCRRTImp.model

import java.util.*

class NTreeNode<E>(val element: E) : Iterable<NTreeNode<E>> {

    var parent: NTreeNode<E>? = null

    private val children = LinkedList<NTreeNode<E>>()

    constructor(element: E, parent: NTreeNode<E>) : this(element) {
        this.parent = parent
    }

    fun add(vararg child: NTreeNode<E>) {
        child.forEach { this += it }
    }

    fun add(vararg element: E) {
        element.forEach { this += it }
    }

    operator fun plusAssign(child: NTreeNode<E>) {
        child.parent = this
        this.children.add(child)
    }

    operator fun plusAssign(element: E) {
        this.children.add(NTreeNode(element, this))
    }

    operator fun get(idx: Int): NTreeNode<E> {
        return children[idx]
    }

    private class VisitRecorder<E>(val node: NTreeNode<E>, var childVisited: Int = 0)

    fun nearestOf(element: E, distanceFunc: (E, E) -> Double): NTreeNode<E> {
        var minimum: NTreeNode<E>? = null
        var minDistance = Double.MAX_VALUE
        this.forEach {
            val distance = distanceFunc(it.element, element)
            if (distance <= minDistance) {
                minDistance = distance
                minimum = it
            }
        }
        return minimum!!
    }

    fun traceTo(element: E): List<E> {
        val stack = Stack<VisitRecorder<E>>()
        stack.push(VisitRecorder(this))
        while (true) {
            val now = stack.peek()
            if (now.node.children.isEmpty() || now.childVisited == now.node.children.size) {
                stack.pop()
                continue
            }
            val child = now.node.children[now.childVisited++]
            stack.push(VisitRecorder(child))
            if (child.element == element) {
                break
            }
        }
        return stack.map { it.node.element }
    }

    fun traceTo(child: NTreeNode<E>): List<E> {
        return traceTo(child.element)
    }

    override fun iterator(): Iterator<NTreeNode<E>> {
        return NTreeIterator(this)
    }

    private class NTreeIterator<E>(root: NTreeNode<E>) : Iterator<NTreeNode<E>> {

        val stack = Stack<VisitRecorder<E>>()

        init {
            stack.push(VisitRecorder(root))
        }

        override fun hasNext(): Boolean {
            return stack.isNotEmpty()
        }

        override fun next(): NTreeNode<E> {
            while (stack.isNotEmpty()) {
                val now = stack.peek()
                if (now.node.children.isEmpty()) {
                    return stack.pop().node
                }
                if (now.childVisited == now.node.children.size) {
                    return stack.pop().node
                }
                stack.push(VisitRecorder(now.node.children[now.childVisited++]))
            }
            throw RuntimeException("NTreeNode iteration bug")
        }

    }
}