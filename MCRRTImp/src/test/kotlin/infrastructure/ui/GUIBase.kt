package infrastructure.ui

import infrastructure.Vector2BasedImp.Vector2
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage

abstract class GUIBase : Application() {

    protected var height = 2000
    protected var width = 2000
    protected var cycleSleepTime = 1 / 60.0
    private lateinit var userPencil: Pencil
    private lateinit var pencil: GraphicsContext

    protected var scrollZoomBase = 1.0
    private var lastMouseX = -1.0
    private var lastMouseY = -1.0
    protected var scaleBase = 1.0

    protected var shiftX = 1.0
    protected var shiftY = 1.0

    protected abstract fun draw(pencil: Pencil)


    protected open fun initializeComponents(primaryStage: Stage, scene: Scene, root: Pane, canvas: Canvas) {
        primaryStage.widthProperty().addListener { ob, ov, nv ->
            this.width = (nv as Double).toInt()
            canvas.width = nv

        }
        primaryStage.heightProperty().addListener { ob, ov, nv ->
            this.height = (nv as Double).toInt()
            canvas.height = nv

        }
        primaryStage.widthProperty().addListener { ob, ov, nv -> scaleBase = nv as Double * scrollZoomBase / width }
        canvas.setOnScroll { event ->
            val deltaY = event.deltaY
            if (deltaY < 0) {
                scrollZoomBase = if (scrollZoomBase > 0.2) scrollZoomBase - 0.1 else 0.2
            } else if (deltaY > 0) {
                scrollZoomBase = if (scrollZoomBase < 100) scrollZoomBase + 0.1 else 100.0
            }
            scaleBase = scene.width * scrollZoomBase / width
        }
        canvas.setOnMouseDragged { event ->
            val mouseX = event.x
            val mouseY = event.y
            if (lastMouseX == -1.0) {
                lastMouseX = mouseX
                lastMouseY = mouseY
                return@setOnMouseDragged
            }
            val deltaX = mouseX - lastMouseX
            val deltaY = mouseY - lastMouseY
            shiftX += deltaX
            shiftY += deltaY
            lastMouseX = mouseX
            lastMouseY = mouseY
        }
        canvas.setOnMouseMoved { event ->
            lastMouseX = event.x
            lastMouseY = event.y
        }
        primaryStage.heightProperty().addListener { ob, ov, nv -> scaleBase = nv as Double / height }
        primaryStage.setOnCloseRequest { event -> System.exit(0) }
    }

    private fun thread() {
        while (true) {
            try {
                Thread.sleep((1000 * cycleSleepTime).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Platform.runLater {
                pencil.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
                userPencil.scale(scaleBase).pixelOffset(shiftX, shiftY)
                userPencil.filled().color(Color.BLACK).rect(Vector2(width / 2.0, height / 2.0), Vector2(width.toDouble(), height.toDouble()))
                draw(userPencil)
            }
        }
    }

    override fun start(primaryStage: Stage) {
        val root = HBox()
        val canvas = Canvas(width.toDouble(), height.toDouble())
        root.children.add(canvas)
        val scene = Scene(root, width.toDouble(), height.toDouble())
        primaryStage.scene = scene
        pencil = canvas.graphicsContext2D
        userPencil = Pencil(pencil)
        initializeComponents(primaryStage, scene, root, canvas)
        val redrawTrigger = Thread(Runnable { this.thread() })
        redrawTrigger.start()
        primaryStage.show()
    }

}
