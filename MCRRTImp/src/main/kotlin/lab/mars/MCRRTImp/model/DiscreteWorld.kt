package lab.mars.MCRRTImp.model

import lab.mars.MCRRTImp.base.Obstacle
import lab.mars.MCRRTImp.base.Vector

class DiscreteWorld<V : Vector<V>> : Iterable<V> {

    constructor(origin : V, bound : V, cellSize : V)  {
        this.scaledSpace = Space(origin, bound, cellSize)
        this.gridSize = cellSize.cpy()
    }

    constructor(originalWorld : Space<V>, scalar : Double) {
        this.gridSize = originalWorld.step.cpy().apply { forEach { dim -> dim.value = scalar } }
        this.scaledSpace = originalWorld.cpy().apply { step = gridSize }
    }

    constructor(originalWorld: Space<V>, cellSize: V) {
        this.scaledSpace = originalWorld.cpy().apply { step = cellSize }
        this.gridSize = cellSize.cpy()
    }


    val grid = HashSet<GridCell<V>>()

    private val scaledSpace : Space<V>

    private val gridSize : V

    val cellSize : V
    get() = gridSize.cpy()

    val size : Long
    get() = scaledSpace.size


    val gridCenter : V
    get() = scaledSpace.centroid

    fun transform(position : V) : GridCell<V> {
        if (!scaledSpace.contains(position)) {
            throw IndexOutOfBoundsException("failed to transform out-bound position $position because grid is defined as $scaledSpace")
        }
        return GridCell(scaledSpace.formalize(position))
    }

    fun formalize(position : V) : V {
        return scaledSpace.formalize(position)
    }

    fun scan(obstacles : List<Obstacle<V>>) {
        val cursorDelta = gridSize.cpy().scale(0.1)
        scaledSpace.forEach {cell ->
            val upper = cell.cpy().translate(cellSize)
            Space(upper, cell, cursorDelta).apply {
                forEach {step ->
                    var obstacleTaken = false
                    for (it in obstacles) {
                        if (it.contains(step)) {
                            record(step)
                            obstacleTaken = true
                            break
                        }
                    }
                    if (obstacleTaken) {
                        return@apply
                    }
                }
            }
        }
    }

    fun record(position : V) {
        val transformed = transform(position)
        grid.add(transformed)
    }

    fun insideObstacle(position : V) : Boolean {
        return try {
            val transformed = transform(position)
            grid.contains(transformed)
        } catch (exp : IndexOutOfBoundsException) {
            true
        }
    }

    fun sample() : V {
        while(true) {
            val sampled = GridCell(scaledSpace.sample())
            if (!grid.contains(sampled)) {
                return sampled.cellIdx
            }
        }
    }


    override fun iterator(): Iterator<V> {
        return scaledSpace.iterator()
    }

}