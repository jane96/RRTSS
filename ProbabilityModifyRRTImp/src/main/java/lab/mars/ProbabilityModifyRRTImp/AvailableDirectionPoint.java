package lab.mars.ProbabilityModifyRRTImp;

import java.util.Objects;

public class AvailableDirectionPoint {

    double x = 0.0;
    double y = 0.0;
    double len = 0.0;

    public AvailableDirectionPoint(double x, double y, double direction, double len) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.len = len;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailableDirectionPoint that = (AvailableDirectionPoint) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.direction, direction) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, direction);
    }

    double direction;
}
