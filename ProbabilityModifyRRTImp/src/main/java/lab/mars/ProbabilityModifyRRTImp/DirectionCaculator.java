package lab.mars.ProbabilityModifyRRTImp;

import java.util.ArrayList;
import java.util.List;

public class DirectionCaculator {

    public static List <AvailableDirectionPoint> getNextPosList(double curX, double curY, double mainDegree, double thetaGap, double timeSlice, double velocity){
        ArrayList <AvailableDirectionPoint> listNextPos = new ArrayList<>();

        for(int i=2; i>=-2; i--){
            AvailableDirectionPoint nextPos = new AvailableDirectionPoint();
            double newX = curX;
            double newY = curY;
            double alpha = mainDegree + i * thetaGap;
            nextPos.direction = alpha;

            for(int j=1;j<101;j++){
                double tmpDegree = mainDegree + (i * thetaGap / 100) * j;
                newX += Math.cos(Math.toRadians(tmpDegree)) * velocity * (timeSlice / 100);
                newY += Math.sin(Math.toRadians(tmpDegree)) * velocity * (timeSlice / 100);
            }

            nextPos.x = newX;
            nextPos.y = newY;
            listNextPos.add(nextPos);
        }


        return listNextPos;
    }
}
