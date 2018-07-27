package lab.mars.RRTBase;

public class MathUtil {


    public static final double D2R = Math.PI / 180.0;

    public static final double R2D = 180.0 / Math.PI;

    public static boolean epsilonEquals(double d1, double d2) {
        return epsilonEquals(d1, d2, 0.0001);
    }

    public static boolean epsilonEquals(double d1, double d2, double epsilon) {
        if (Math.abs(d1 - d2) < epsilon) {
            return true;
        }
        return false;
    }

    /**
     * returns a random number in [low, up)
     *
     * @param low
     * @param up
     * @return
     */
    public static double random(double low, double up) {
        if (low == up) {
            return low;
        }
        return Math.random() * (up - low) + low;
    }

}
