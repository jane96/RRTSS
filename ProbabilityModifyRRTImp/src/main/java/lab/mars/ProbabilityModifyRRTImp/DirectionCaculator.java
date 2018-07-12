package lab.mars.ProbabilityModifyRRTImp;

import java.util.ArrayList;
import java.util.List;

public class DirectionCaculator {

    public static List <AvailableDirectionPoint> getNextPosList(double curX, double curY, double mainDegree, double thetaGap, double timeSlice, double velocity, int directionNum){
        ArrayList <AvailableDirectionPoint> listNextPos = new ArrayList<>();

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
