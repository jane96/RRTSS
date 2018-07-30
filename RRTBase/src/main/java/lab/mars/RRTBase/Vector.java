package lab.mars.RRTBase;

import java.util.Iterator;
import java.util.function.Consumer;

public abstract class Vector<T extends Vector<T>> {

    protected Dimension[] dimensions;

    protected int dimensionCount;

    public Vector(double ... values) {
        dimensions = new Dimension[values.length];
        this.dimensionCount = values.length;
        for (int i = 0; i < values.length;i ++) {
            this.dimensions[i] = new Dimension(values[i]);
        }
    }

    public abstract double distance(T o);

    public abstract double distance2(T o);

    public abstract T normalize();

    public abstract T cpy();

    public abstract double len();

    public abstract double len2();

    public abstract T set(T o);

    public abstract T translate(T v);

    public abstract double dot(T v);

    public abstract T scale(double scalar);

    public abstract T scale(T v);

    public abstract T lerp(T target, double coefficient);

    public abstract boolean equals(Object o);

    public abstract boolean epsilonEquals(T other, double epsilon);

    public abstract String toString();

    public abstract T rotate(double angle);

    public abstract T zero();

    public abstract double angle(T o);

    public abstract T reverse();

    public int dimensionCount() {
        return dimensionCount;
    }

    public Dimension[] dimensions() {
        return dimensions;
    }

    public T forEachDim(Consumer<Dimension> action) {
        for (int i = 0; i < this.dimensions.length; i++) {
            action.accept(dimensions[i]);
        }
        return (T) this;
    }

}
