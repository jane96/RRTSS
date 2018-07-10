package lab.mars.RRTBase;

public interface WayPoint<V extends Vector<V>> {

    boolean equals(Object other);

    int hashCode();

    String toString();
}
