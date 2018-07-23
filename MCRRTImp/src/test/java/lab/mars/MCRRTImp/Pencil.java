package lab.mars.MCRRTImp;

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

    public Pencil color(Color color) {
        actualPencil.setFill(color);
        actualPencil.setStroke(color);
        return this;
    }

    public Pencil circle(Vector2 centroid, double radius) {
        switch (type) {
            case LineShape:
                actualPencil.strokeOval((centroid.x - radius) * scaling, (centroid.y - radius) * scaling, radius * 2 * scaling, radius * 2 * scaling);
                break;
            case FilledShape:
                actualPencil.fillOval((centroid.x - radius) * scaling, (centroid.y - radius) * scaling, radius * 2 * scaling, radius * 2 * scaling);
                break;
        }
        return this;
    }

    public Pencil line(Vector2 start, Vector2 end) {
        actualPencil.strokeLine(start.x * scaling, start.y * scaling, end.x * scaling, end.y * scaling);
        return this;
    }

    public Pencil box(Vector2 centroid, double edgeLength) {
        switch (type) {
            case FilledShape:
                actualPencil.fillRect((centroid.x - edgeLength / 2) * scaling, (centroid.y - edgeLength / 2) * scaling, edgeLength * scaling, edgeLength * scaling);
                break;
            case LineShape:
                actualPencil.strokeRect((centroid.x - edgeLength / 2) * scaling, (centroid.y - edgeLength / 2) * scaling, edgeLength * scaling, edgeLength * scaling);
                break;
        }
        return this;
    }

}
