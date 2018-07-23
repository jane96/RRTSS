package lab.mars.ProbabilityModifyRRTImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DirectionCaculator {

//    public static List <AvailableDirectionPoint> getNextPosList(double curX, double curY, double mainDegree, double thetaGap, double timeSlice, double velocity, int directionNum){
//        ArrayList <AvailableDirectionPoint> listNextPos = new ArrayList<>();
//
//        for(int i=(directionNum - 1)/2; i>=-(directionNum - 1)/2; i--){
//            double newX = curX;
//            double newY = curY;
//            double alpha = mainDegree + i * thetaGap;
//
//            for(int j=1;j<101;j++){
//                double tmpDegree = mainDegree + (i * thetaGap / 100) * j;
//                newX += Math.cos(Math.toRadians(tmpDegree)) * velocity * (timeSlice / 100);
//                newY += Math.sin(Math.toRadians(tmpDegree)) * velocity * (timeSlice / 100);
//            }
//
//            AvailableDirectionPoint nextPos = new AvailableDirectionPoint(newX, newY, alpha);
//            listNextPos.add(nextPos);
//        }
//
//
//        return listNextPos;
//    }

    public static List <AvailableDirectionPoint> getNextPosList(Attacker attacker, double timeSlice){
        ArrayList <AvailableDirectionPoint> listNextPos = new ArrayList<>();
        Vector2 o = new Vector2(1.0,0.0);
        double curX = attacker.position().x;
        double curY = attacker.position().y;
        double mainDegree = attacker.velocity().angle(o);
        double velocity = attacker.velocity().len();
        int directionNum = attacker.rotationGraduation();
        double thetaGap = attacker.rotationLimits() / (directionNum - 1);

        for(int i=(directionNum - 1)/2; i>=-(directionNum - 1)/2; i--){
            double newX = curX;
            double newY = curY;
            double alpha = mainDegree + i * thetaGap;

            for(int j=1;j<101;j++){
                double tmpDegree = mainDegree + (i * thetaGap / 100) * j;
                newX += Math.cos(Math.toRadians(tmpDegree)) * velocity * (timeSlice / 100);
                newY += Math.sin(Math.toRadians(tmpDegree)) * velocity * (timeSlice / 100);
            }

            AvailableDirectionPoint nextPos = new AvailableDirectionPoint(newX, newY, alpha);
            listNextPos.add(nextPos);
        }


        return listNextPos;
    }
}
