package lab.mars.RRTBase;

public interface Vector<T extends Vector<T>> {

    double distance(T o) throws Exception;

    double distance2(T o) throws Exception;

    T normalize();

    T cpy();

    double len();

    double len2();

    T set(T o);

    T translate(T v);

    double dot(T v);

    T scale(double scalar);

    T lerp(T target, double coefficient);

    boolean equals(Object o);

    boolean epsilonEquals(T other, double epsilon);

    String toString();

    T rotate(double angle);

    T zero();

    T sample();

}
