package lab.mars.MCRRTImp.Vector2BasedImp

import javafx.application.Application
import javafx.scene.paint.Color
import infrastructure.Vector2BasedImp.CircleObstacle
import infrastructure.Vector2BasedImp.Vector2
import infrastructure.ui.GUIBase
import infrastructure.ui.Pencil
import lab.mars.MCRRTImp.base.Obstacle
import lab.mars.MCRRTImp.base.random
import lab.mars.MCRRTImp.model.DiscreteWorld
import lab.mars.MCRRTImp.model.GridCell
import lab.mars.MCRRTImp.model.Space

import java.util.ArrayList
import java.util.HashSet

class TestScaledGridDraw : GUIBase() {

    override fun draw(pencil: Pencil) {
        pencil.stroked(5.0).rect(Vector2((mapWidth / 2).toDouble(), (mapHeight / 2).toDouble()), Vector2(mapWidth.toDouble(), mapHeight.toDouble()))
        val cellSize = scaledGrid.cellSize
        scaledGrid.forEach { v ->
            val color: Color
            if (scaledGrid.contains(v)) {
                color = Color.RED
            } else {
                color = Color(0.2, 0.7, 0.0, 1.0)
            }
            val rectCentroid = Vector2(v.x() + cellSize.x() / 2, v.y() + cellSize.y() / 2)
            pencil.filled().color(color).rect(rectCentroid, cellSize)
            pencil.stroked(0.5).color(Color.BLUE).rect(rectCentroid, cellSize)
        }
        obstacles.forEach { obs ->
            val circleObstacle = obs as CircleObstacle
            pencil.filled().color(Color.LIGHTBLUE).circle(circleObstacle.origin, circleObstacle.radius)
        }
    }

    companion object {

        internal var mapWidth = 1280

        internal var mapHeight = 800

        private fun generateRandomObstacle(count: Int, space: Space<Vector2>, radiusUpper: Double): List<Obstacle<Vector2>> {
            val obstacles = ArrayList<Obstacle<Vector2>>()
            for (i in 0 until count) {
                val sampled = space.sample()
                val radius = (0.0 random radiusUpper)
                obstacles.add(CircleObstacle(sampled.x(), sampled.y(), radius))
            }
            return obstacles
        }

        internal lateinit var scaledGrid: DiscreteWorld<Vector2>
        internal lateinit var originalSpace: Space<Vector2>
        internal lateinit var obstacles: List<Obstacle<Vector2>>

        fun testCreate() {
            originalSpace = Space(Vector2(mapWidth.toDouble(), mapHeight.toDouble()), Vector2(0.0, 0.0))
            scaledGrid = DiscreteWorld(originalSpace, 20.0)
        }

        fun testScan() {
            obstacles = generateRandomObstacle(200, originalSpace, 30.0)
            val start = System.currentTimeMillis()
            scaledGrid.scan(obstacles)
            println((System.currentTimeMillis() - start).toString() + "ms")
            val gridString = HashSet<String>()
            var count = 0
            for (c in scaledGrid.grid) {
                count++
                gridString.add(c.toString())
            }
            assert(count == gridString.size)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            testCreate()
            testScan()
            Application.launch(*args)
        }
    }
}
