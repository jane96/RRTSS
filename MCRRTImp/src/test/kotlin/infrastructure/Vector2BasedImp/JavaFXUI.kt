package infrastructure.Vector2BasedImp

import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import infrastructure.model.Attacker
import infrastructure.model.World
import lab.mars.MCRRTImp.algorithm.MCRRT
import lab.mars.MCRRTImp.model.*
import infrastructure.ui.GUIBase
import infrastructure.ui.Pencil
import javafx.application.Application
import lab.mars.MCRRTImp.base.*

import java.util.ArrayList

class JavaFXUI : GUIBase() {

    private var stage: Stage? = null
    private var world: World<Vector2>? = null
    private lateinit var rrt: MCRRT<Vector2>

    private val mapWidth = 1920

    private val mapHeight = 1080

    private fun randomObstacles(count: Int, redZoneRadius: Double, maxRadius: Double, vararg redZoneOrigins: Vector2): List<Obstacle<Vector2>> {
        val obstacles = ArrayList<Obstacle<Vector2>>()
        var i = 0
        while (i < count) {
            val x = 0.0 random mapWidth.toDouble()
            val y = 0.0 random mapHeight.toDouble()
            val radius = 0.0 random maxRadius
            val origin = Vector2(x, y)
            var flag = false
            for (redZone in redZoneOrigins) {
                if (redZone.distance(origin) < radius + redZoneRadius) {
                    flag = true
                    break
                }
            }
            if (flag) {
                continue
            }
            i++
            obstacles.add(CircleObstacle(x, y, radius))
            println(String.format("obstacles.translate(new CircleObstacle(%f, %f, %f));", x, y, radius))
        }
        return obstacles
    }


    private fun drawTarget(attacker: Attacker<Vector2>, pencil: Pencil) {
        val target = attacker.target()
        pencil.filled().color(Color.YELLOWGREEN).circle(target!!.origin, target.radius)
    }

    private fun drawUAV(attacker: Attacker<Vector2>, pencil: Pencil) {
        pencil.filled().color(Color(1.0, 0.0, 0.0, 0.3)).circle(attacker.position, 10.0)
    }

    private fun drawObstacles(pencil: Pencil) {
        world!!.allObstacles().forEach { obs ->
            if (obs is CircleObstacle) {
                pencil.filled().color(Color.LIGHTBLUE).circle(obs.origin, obs.radius)
                pencil.stroked(1.0).color(Color.DARKBLUE).circle(obs.origin, obs.radius)
            }
        }
        pencil.stroked(5.0).color(Color.BLUE).rect(Vector2((mapWidth / 2f).toDouble(), (mapHeight / 2f).toDouble()), Vector2(mapWidth.toDouble(), mapHeight.toDouble()))
    }

    private fun drawLeaves(attacker: Attacker<Vector2>, pencil: Pencil) {
        val leaves = attacker.leaves
        leaves.forEach { leaf ->
            val wayPoint = leaf.element
            pencil.filled().color(Color.RED).circle(wayPoint.origin, 1.0)
        }
    }

    private fun drawAreaPath(attacker: Attacker<Vector2>, pencil: Pencil) {
        val paths = attacker.areaPath()
        if (paths.size != 0) {
            val path: Path<WayPoint<Vector2>> = if (paths.size != 1) {
                paths.poll()
            } else {
                paths.peek()
            }
            val cellSize = Vector2(path[0].radius, path[0].radius)
            for (wayPoint in path) {
                pencil.filled().color(Color.LIGHTBLUE).rect(wayPoint.origin.cpy().translate(Vector2(cellSize.x() / 2, cellSize.y() / 2)), cellSize)
                pencil.stroked(1.0).color(Color.BLUE).rect(wayPoint.origin.cpy().translate(Vector2(cellSize.x() / 2, cellSize.y() / 2)), cellSize)
            }
        }
    }

    private fun drawPath(attacker: Attacker<Vector2>, pencil: Pencil) {

        val paths = attacker.actualPath()
        var min = Double.MAX_VALUE
        var max = Double.MIN_VALUE
        var maximumPathIdx = -1
        paths.forEachIndexed { idx, it ->
            if (it.utility < min) {
                min = it.utility
            }
            if (it.utility > max) {
                max = it.utility
                if (it.finished ) {
                    maximumPathIdx = idx
                }
            }
        }
        paths.forEachIndexed { idx, path ->
            if (path.size != 0) {
                val last = attacker.position.cpy()
                var lineWidth = 1.0
                var color = Color.BLACK.interpolate(Color.color(0.8,0.8,0.8), if (max == min) 1.0 else (path.utility - min) / (max - min))
                for (i in (0 until path.size)) {
                    val wayPoint2D = path[i]
                    if (path.finished) {
                        lineWidth = 1.0
                        color = Color.RED
                    }
                    pencil.filled().color(color).box(last.cpy(), wayPoint2D.radius / 10)
                    pencil.stroked(lineWidth * scaleBase).color(color).line(last.cpy(), last.set(wayPoint2D.origin))
                }
            }
        }
    }

    private fun leftUpAttacker(): Attacker<Vector2> {
        val attackerPosition = Vector2(5.0, 5.0)
        val targetPosition = Vector2(1800.0, 080.0)
        val target = WayPoint(targetPosition, 5.0, Vector2())
        val attackerLeftUp = Attacker(attackerPosition, Vector2(1.0, 1.0).normalize().scale(4.1666667 / 1000.0 * 6), 10.0, 5, 200.0, 50.0, 5.0, target, world!!.area()) { world!!.allObstacles() }
        attackerLeftUp.setDesignatedTarget(target)
        return attackerLeftUp
    }

    private fun middleLeftAttacker(): Attacker<Vector2> {
        val attackerPosition = Vector2(5.0, 925.0)
        val targetPosition = Vector2(1200.0, 780.0)
        val target = WayPoint(targetPosition, 5.0, Vector2())
        val attackerLeftUp = Attacker(attackerPosition, Vector2(1.0, 1.0).normalize().scale(4.1666667), 10.0, 30, 200.0, 50.0, 5.0, target, world!!.area()) { world!!.allObstacles() }
        attackerLeftUp.setDesignatedTarget(target)
        return attackerLeftUp
    }

    private fun rightUpAttacker(): Attacker<Vector2> {
        val attackerPosition = Vector2(1955.0, 5.0)
        val targetPosition = Vector2(1200.0, 780.0)
        val target = WayPoint(targetPosition, 5.0, Vector2())
        val attackerLeftUp = Attacker(attackerPosition, Vector2(1.0, 1.0).normalize().scale(4.1666667), 10.0, 30, 200.0, 50.0, 5.0, target, world!!.area()) { world!!.allObstacles() }
        attackerLeftUp.setDesignatedTarget(target)
        return attackerLeftUp
    }

    private fun rightUpMiddleAttacker(): Attacker<Vector2> {
        val attackerPosition = Vector2(1125.0, 265.0)
        val targetPosition = Vector2(1200.0, 980.0)
        val target = WayPoint(targetPosition, 5.0, Vector2())
        val attackerLeftUp = Attacker(attackerPosition, Vector2(1.0, 1.0).normalize().scale(4.1666667), 10.0, 30, 200.0, 50.0, 5.0, target, world!!.area()) { world!!.allObstacles() }
        attackerLeftUp.setDesignatedTarget(target)
        return attackerLeftUp
    }

    fun buildWorld() {
        val circleObstacles = randomObstacles(150, 20.0, 30.0, Vector2(5.0, 5.0), Vector2(1200.0, 780.0))
        val attackers = ArrayList<Attacker<Vector2>>()
        world = World(attackers, circleObstacles, Space(Vector2(mapWidth.toDouble(), mapHeight.toDouble()), Vector2()))
        for (i in 0..0) {
            attackers.add(leftUpAttacker())
        }
        //        attackers.add(middleLeftAttacker());
        //        attackers.add(rightUpAttacker());
        //        attackers.add(rightUpMiddleAttacker());
    }

    init {
        buildWorld()
    }

    override fun draw(pencil: Pencil) {
        //        for (Attacker<Vector2> attacker : world.attacker()) {
        //            drawGrid(attacker, pencil);
        //        }
        drawObstacles(pencil)
        for (attacker in world!!.attacker()) {
            drawAreaPath(attacker, pencil)
            drawPath(attacker, pencil)
            drawTarget(attacker, pencil)
            drawUAV(attacker, pencil)
            drawLeaves(attacker, pencil)
        }
    }

    private fun drawGrid(attacker: Attacker<Vector2>, pencil: Pencil) {
        val scaledGrid = attacker.gridWorld() ?: return
        pencil.stroked(5.0).rect(Vector2((mapWidth / 2f).toDouble(), (mapHeight / 2f).toDouble()), Vector2(mapWidth.toDouble(), mapHeight.toDouble()))
        val cellSize = scaledGrid.cellSize
        scaledGrid.forEach { v ->
            val color: Color = if (scaledGrid.contains(v)) {
                Color.RED
            } else {
                Color(0.2, 0.7, 0.0, 1.0)
            }
            val rectCentroid = Vector2(v.x() + cellSize.x() / 2, v.y() + cellSize.y() / 2)
            pencil.filled().color(color).rect(rectCentroid, cellSize)
            pencil.stroked(0.5).color(Color.BLUE).rect(rectCentroid, cellSize)
        }
    }

    override fun initializeComponents(primaryStage: Stage, scene: Scene, root: Pane, canvas: Canvas) {
        super.initializeComponents(primaryStage, scene, root, canvas)
        this.height = mapHeight
        this.width = mapWidth
        primaryStage.title = "Test Flight"
        stage = primaryStage
        val menu = ContextMenu()
        val solve = MenuItem("Start")
        menu.items.add(solve)
        canvas.setOnContextMenuRequested { event -> menu.show(canvas, event.screenX, event.screenY) }
        solve.setOnAction { event -> world!!.attacker().forEach { it.startAlgorithm() } }

    }

}

fun main(args: Array<String>) {
    Application.launch(JavaFXUI::class.java, *args)
}
