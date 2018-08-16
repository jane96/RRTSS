package lab.mars.MCRRTImp.infrastructure.ui;

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
    protected double cycleSleepTime = 1 / 24.f;
    private Pencil userPencil;
    private GraphicsContext pencil;

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
                draw(userPencil);
            });
        }
    }

    @Override
    public final void start(Stage primaryStage) throws Exception{
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
