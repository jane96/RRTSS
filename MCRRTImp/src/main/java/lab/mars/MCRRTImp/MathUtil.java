package lab.mars.MCRRTImp;

public class MathUtil {

    public static boolean epsilonEquals(double d1, double d2) {
        return epsilonEquals(d1, d2, 0.001);
    }

    public static boolean epsilonEquals(double d1, double d2, double epsilon) {
        if (Math.abs(d1 - d2) <= epsilon) {
            return true;
        }
        return false;
    }

}
