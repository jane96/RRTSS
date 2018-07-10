package lab.mars.RRTBase;

public interface Vector<T extends Vector<T>> {

    double distance(T o);

    double distance2(T o);

    T normalize();

    T cpy();

    double len();

    double len2();

    T set(T o);

    T subtract(T v);

    T add(T v);

    double dot(T v);

    T scale(double scalar);

    T scale(T v);

    T lerp(T target, double coefficient);

    boolean equals(Object o);

    boolean epsilonEquals(T other, double epsilon);

    String toString();

    T zero();


}
