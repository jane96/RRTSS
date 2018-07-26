package lab.mars.RRTBase;

import java.util.Iterator;

public class Space<V extends Vector<V>> implements Iterable<V> {

    private V lowerBound;

    private V upperBound;

    private V step;

    private int dimensionCount = 0;
    /**
     * include
     */
    private double[] lowestValues;

    /**
     * exclude
     */
    private double[] highestValues;

    private double[] stepValues;

    private boolean iterable = true;

    public Space(V upperBound, V lowerBound, V step) {
        this.lowerBound = lowerBound.cpy();
        this.upperBound = upperBound.cpy();
        this.step = step;
        this.dimensionCount = step.dimensionCount;
        lowestValues = new double[lowerBound.dimensionCount];
        highestValues = new double[upperBound.dimensionCount];
        for (int i = 0; i < dimensionCount; i++) {
            lowestValues[i] = lowerBound.dimensions[i].value;
            highestValues[i] = upperBound.dimensions[i].value;
        }
        this.stepValues = new double[step.dimensionCount];
        for (int i = 0; i < dimensionCount; i++) {
            double value = step.dimensions[i].value;
            if (value == 0) {
                iterable = false;
            }
            this.stepValues[i] = value;
        }
    }

    public Space(V upperBound, V lowerBound) {
        this.lowerBound = lowerBound.cpy();
        this.upperBound = upperBound.cpy();
        this.dimensionCount = this.upperBound.dimensionCount;
        lowestValues = new double[lowerBound.dimensionCount];
        highestValues = new double[upperBound.dimensionCount];
        for (int i = 0; i < dimensionCount; i++) {
            lowestValues[i] = lowerBound.dimensions[i].value;
            highestValues[i] = upperBound.dimensions[i].value;
        }
        this.stepValues = new double[step.dimensionCount];
        for (int i = 0; i < dimensionCount; i++) {
            this.stepValues[i] = 1;
        }
        this.iterable = false;
    }

    public boolean include(V point) {
        Dimension[] dimensions = point.dimensions;
        for (int i = 0; i < dimensionCount; i++) {
            double value =dimensions[i].value;
            if (value < lowestValues[i] || value > highestValues[i]) {
                return false;
            }
        }
        return true;
    }

    public V centroid() {
        V centroid = lowerBound.cpy();
        for (int i = 0; i < dimensionCount; i++) {
            centroid.dimensions[i].value = ((int)((highestValues[i] + lowestValues[i]) / (2.0 * stepValues[i]))) * stepValues[i];
        }
        return centroid;
    }

    public V sample() {
        V sampled = lowerBound.cpy();
        for (int i = 0; i < dimensionCount; i++) {
            double dimensionDelta = (highestValues[i] - lowestValues[i]) / stepValues[i];
            sampled.dimensions[i].value = ((int)MathUtil.random(0, dimensionDelta)) * stepValues[i] + lowestValues[i];
        }
        return sampled;
    }

    public Space<V> cpy() {
        if (step != null) {
            return new Space<>(upperBound, lowerBound, step);
        } else {
            return new Space<>(upperBound, lowerBound);
        }
    }

    public V getStep() {
        return step;
    }

    public Space<V> setStep(V step) {
        this.step = step.cpy();
        this.stepValues = new double[dimensionCount];
        for (int i = 0; i < dimensionCount; i++) {
            double value = step.dimensions[i].value;
            if (value == 0) {
                this.iterable = false;
            }
            this.stepValues[i] = value;
        }
        return this;
    }

    @Override
    public Iterator<V> iterator() {
        if (!iterable) {
            throw new IllegalArgumentException("cannot iterate vectors in a continuous space (stepValues not all set to non-zero)");
        }
        return new VectorIterator();
    }

    private class VectorIterator implements Iterator<V> {

        private V cursor;


        private double stepLength2 = step.len2();

        VectorIterator() {
            cursor = lowerBound.cpy();
        }

        @Override
        public boolean hasNext() {
            return cursor.distance2(upperBound) <= stepLength2;
        }

        @Override
        public V next() {
            for (int i = dimensionCount - 1 ; i >=0; i++) {
                cursor.dimensions[i].value += stepValues[i];
                if (cursor.dimensions[i].value >= highestValues[i]) {
                    cursor.dimensions[i].value = lowestValues[i];
                    continue;
                }
                break;
            }
            return cursor;
        }
    }
}
