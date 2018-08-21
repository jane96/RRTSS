package infrastructure.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class GUIBase extends Application {

    protected int height = 2000;
    protected int width = 2000;
    protected double cycleSleepTime = 1 / 60.0;
    private Pencil userPencil;
    private GraphicsContext pencil;

    protected double scrollZoomBase = 1;
    private double lastMouseX = -1;
    private double lastMouseY = -1;
    protected double scaleBase = 1;

    protected double shiftX = 1;
    protected double shiftY = 1;

    protected abstract void draw(Pencil pencil);


    protected void initializeComponents(Stage primaryStage, Scene scene, Pane root, Canvas canvas){
        primaryStage.widthProperty().addListener((ob, ov, nv) -> {
            this.width = (int)(double) nv;
            canvas.setWidth((double) nv);

        });
        primaryStage.heightProperty().addListener((ob, ov, nv) -> {
            this.height = (int)(double) nv;
            canvas.setHeight((double) nv);

        });
        primaryStage.widthProperty().addListener((ob, ov, nv) -> scaleBase = ((double) nv * scrollZoomBase) / width);
        canvas.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                scrollZoomBase = scrollZoomBase > 0.2 ? scrollZoomBase - 0.1 : 0.2;
            } else if (deltaY > 0) {
                scrollZoomBase = scrollZoomBase < 100 ? scrollZoomBase + 0.1 : 100;
            }
            scaleBase = (scene.getWidth() * scrollZoomBase) / width;
        });
        canvas.setOnMouseDragged(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            if (lastMouseX == -1) {
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                return;
            }
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            shiftX += deltaX;
            shiftY += deltaY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        });
        canvas.setOnMouseMoved(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
        });
        primaryStage.heightProperty().addListener((ob, ov, nv) -> scaleBase = (double) nv / height);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    private void thread(){
        while (true) {
            try {
                Thread.sleep((long) (1000 * cycleSleepTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                pencil.clearRect(0, 0, width, height);
                userPencil.scale(scaleBase).pixelOffset(shiftX, shiftY);
                draw(userPencil);
            });
        }
    }

    @Override
    public final void start(Stage primaryStage) {
        HBox root = new HBox();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        pencil = canvas.getGraphicsContext2D();
        userPencil = new Pencil(pencil);
        initializeComponents(primaryStage,scene , root, canvas);
        Thread redrawTrigger = new Thread(this::thread);
        redrawTrigger.start();
        primaryStage.show();
    }

}
