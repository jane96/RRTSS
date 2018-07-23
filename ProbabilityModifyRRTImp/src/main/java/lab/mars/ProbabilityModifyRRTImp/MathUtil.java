package lab.mars.ProbabilityModifyRRTImp;

public class MathUtil {

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

    /**
     * returns two tangent lines's theta to the x axis.
     */
    public static double [] get_tangent_ks(float circle_x, float circle_y, float circle_r, float x, float y){
        double [] ks = {0, 0};

        double tan_co = Math.atan( y / x);
        double del_rad = Math.asin(circle_r / Math.sqrt(Math.pow((circle_x - x), 2) + Math.pow((circle_y - y), 2)));

        double k_left = Math.toDegrees(tan_co + del_rad);
        double k_right = Math.toDegrees(tan_co - del_rad);
        ks[0] = k_left;
        ks[1] = k_right;

        return ks;
    }

}
