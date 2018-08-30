package infrastructure.ui;

import infrastructure.Vector2BasedImp.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Pencil {

    enum ShapeType {
        LineShape,
        FilledShape
    }

    private double scaling = 1;

    private GraphicsContext actualPencil;

    private ShapeType type = ShapeType.LineShape;

    private double globalShiftX = 0;

    private double globalShiftY = 0;

    public Pencil(GraphicsContext javafxPencil) {
        actualPencil = javafxPencil;
    }

    public Pencil filled() {
        this.type = ShapeType.FilledShape;
        return this;
    }

    public Pencil stroked(double lineWidth) {
        this.type = ShapeType.LineShape;
        this.actualPencil.setLineWidth(lineWidth);
        return this;
    }

    public Pencil scale(double scalar) {
        this.scaling = scalar;
        return this;
    }

    public Pencil pixelOffset(double x, double y) {
        this.globalShiftX = x;
        this.globalShiftY = y;
        return this;
    }

    public Pencil color(Color color) {
        actualPencil.setFill(color);
        actualPencil.setStroke(color);
        return this;
    }

    public Pencil circle(Vector2 centroid, double radius) {
        switch (type) {
            case LineShape:
                actualPencil.strokeOval((centroid.x() - radius) * scaling + globalShiftX, (centroid.y() - radius) * scaling + globalShiftY, radius * 2 * scaling, radius * 2 * scaling);
                break;
            case FilledShape:
                actualPencil.fillOval((centroid.x() - radius) * scaling  + globalShiftX, (centroid.y() - radius) * scaling+ globalShiftY, radius * 2 * scaling, radius * 2 * scaling);
                break;
        }
        return this;
    }

    public Pencil line(Vector2 start, Vector2 end) {
        actualPencil.strokeLine(start.x() * scaling  + globalShiftX, start.y() * scaling + globalShiftY, end.x() * scaling  + globalShiftX, end.y() * scaling + globalShiftY);
        return this;
    }

    public Pencil box(Vector2 centroid, double edgeLength) {
        switch (type) {
            case FilledShape:
                actualPencil.fillRect((centroid.x() - edgeLength / 2) * scaling  + globalShiftX, (centroid.y() - edgeLength / 2) * scaling + globalShiftY, edgeLength * scaling, edgeLength * scaling);
                break;
            case LineShape:
                actualPencil.strokeRect((centroid.x() - edgeLength / 2) * scaling  + globalShiftX, (centroid.y() - edgeLength / 2) * scaling + globalShiftY, edgeLength * scaling, edgeLength * scaling);
                break;
        }
        return this;
    }

    public Pencil rect(Vector2 centroid, Vector2 size) {
        switch (type) {
            case FilledShape:
                actualPencil.fillRect((centroid.x() - size.x() / 2) * scaling  + globalShiftX, (centroid.y() - size.y() / 2) * scaling + globalShiftY, size.x() * scaling, size.y() * scaling);
                break;
            case LineShape:
                actualPencil.strokeRect((centroid.x() - size.x() / 2) * scaling  + globalShiftX, (centroid.y() - size.y() / 2) * scaling + globalShiftY, size.x() * scaling, size.y() * scaling);
                break;
        }
        return this;
    }

}
