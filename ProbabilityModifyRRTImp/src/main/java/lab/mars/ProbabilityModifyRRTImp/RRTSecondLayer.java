package lab.mars.ProbabilityModifyRRTImp;

import lab.mars.RRTBase.WayPoint;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.List;

public class RRTSecondLayer {
    int N;
    List<WayPoint2D> AreaPath;
    WayPoint2D targetPos;
    WayPoint2D currentPos;
    Attacker attacker;
    double equalR = 5;
    ArrayList<Double> thetaGap = new ArrayList();
    ArrayList<Double> normalArea = new ArrayList();
    ArrayList<Double> transformedPro = new ArrayList();
    ArrayList<Double> normalizedPro = new ArrayList();

    double mainDegree;

    public RRTSecondLayer(Attacker attacker, int N, ArrayList<WayPoint2D> AreaPath) {
        this.attacker = attacker;
        this.AreaPath = AreaPath;
        this.N = N;
        this.currentPos = AreaPath.get(0);
        AreaPath.remove(0);
        this.targetPos = AreaPath.get(0);
        AreaPath.remove(0);
    }

    public ArrayList getThetaGap(double TargetDegree, List<AvailableDirectionPoint> nextPosList) {
        double gap;
        ArrayList thetaGap = new ArrayList();
        for (AvailableDirectionPoint nextPos : nextPosList) {
            gap = nextPos.direction - TargetDegree;
            thetaGap.add(gap);
        }
        return thetaGap;
    }

    public double getThetaToX(WayPoint2D currentPos, WayPoint2D targetPos) {
        return Math.toDegrees(Math.atan2((currentPos.origin.y - targetPos.origin.y) , (currentPos.origin.x - targetPos.origin.x)));
    }

    public ArrayList toNormalArea(ArrayList<Double> thetaGap) {
        double max = 0.0;
        ArrayList<Double> normalArea = new ArrayList<>();
        double tmp;
        for (int i = 0; i < thetaGap.size(); i++)
            if (Math.abs(thetaGap.get(i)) > max)
                max = Math.abs(thetaGap.get(i));
        for (int i = 0; i < thetaGap.size(); i++) {
            tmp = 0 + 2.58 / max * thetaGap.get(i);
            normalArea.add(tmp);
        }
        return normalArea;
    }

    public double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    public boolean reachedOrNot(WayPoint2D currentPos, WayPoint2D targetPos) {
        if (getDistance(currentPos.origin.x, currentPos.origin.y, targetPos.origin.x, targetPos.origin.y) < equalR)
            return true;
        return false;
    }

    public ArrayList transformPro(ArrayList<Double> normalArea) {
        NormalDistribution normalDistribution = new NormalDistribution(0, 1);
        double tmpPro;
        for (int i = 0; i < normalArea.size(); i++) {
            if (normalArea.get(i) > 0)
                tmpPro = 1 - normalDistribution.cumulativeProbability(normalArea.get(i));
            else
                tmpPro = normalDistribution.cumulativeProbability(normalArea.get(i));
            normalArea.set(i, tmpPro);
        }

        return normalArea;
    }

    public ArrayList normalization(ArrayList<Double> transformedPro) {
        double sum = 0.0;
        double tmp;
        for (int i = 0; i < transformedPro.size(); i++)
            sum += transformedPro.get(i);
        for (int i = 0; i < transformedPro.size(); i++) {
            tmp = transformedPro.get(i) / sum;
            transformedPro.set(i, tmp);
        }
        return transformedPro;
    }

    public void chooseNextPose(List waypointSequence, List<Double> normalizedPro, List<AvailableDirectionPoint> nextPosList, double mainDegree) {
        double rand = Math.random();
        int size = normalizedPro.size();
        int i;
        double min = 0.0;
        double max = 0.0;
        int max_index = 0;
        for (i=0; i<normalizedPro.size(); i++){
            if (normalizedPro.get(i) > max){
                max = normalizedPro.get(i);
                max_index = i;
            }
        }

        waypointSequence.add(nextPosList.get(max_index));
        double alpha = nextPosList.get(max_index).direction - mainDegree;
        attacker.velocity().rotate(alpha);
        currentPos.origin.x = nextPosList.get(max_index).x;
        currentPos.origin.y = nextPosList.get(max_index).y;
//        while (i < size) {
//            max += normalizedPro.get(i);
//            if (min < rand && rand <= max) {
//                waypointSequence.add(nextPosList.get(i));
//                double alpha = nextPosList.get(i).direction - mainDegree;
//                attacker.velocity().rotate(alpha);
//                currentPos.origin.x = nextPosList.get(i).x;
//                currentPos.origin.y = nextPosList.get(i).y;
//                break;
//            }
//            min = max;
//            i += 1;
//        }
    }

    public boolean outLimitDegree(double targetDegree) {
        if (Math.abs(targetDegree) > attacker.rotationLimits() / 2)
            return true;
        return false;
    }

    public ArrayList toOutLimitNormalArea(List<Double> thetaGap) {
        double min = thetaGap.get(0);
        int minIndex = 0;
        double tmp;
        ArrayList<Double> newThetaGap = new ArrayList();
        double max = 0.0;
        double newMax = 0.0;
        ArrayList<Double> outLimitNormalArea = new ArrayList<>();
        for (int i = 1; i < thetaGap.size(); i++)
            if (Math.abs(thetaGap.get(i)) < min) {
                min = Math.abs(thetaGap.get(i));
                minIndex = i;
            }
        double closedDirection = thetaGap.get(minIndex);
        for (int i = 0; i < thetaGap.size(); i++) {
            tmp = closedDirection - thetaGap.get(i);
            newThetaGap.add(tmp);
        }
        for (int i = 0; i < newThetaGap.size(); i++)
            if (Math.abs(newThetaGap.get(i)) > newMax)
                newMax = Math.abs(newThetaGap.get(i));
        for (int i = 0; i < newThetaGap.size(); i++) {
            tmp = 0 + 2.58 / newMax * newThetaGap.get(i);
            outLimitNormalArea.add(tmp);
        }
        return outLimitNormalArea;
    }

    public List<AvailableDirectionPoint> getWaypointSequence() {
        List waypointSequence = new ArrayList();
        double targetDegree;
        double vx, vy;
        List<AvailableDirectionPoint> nextPosList;

        while (targetPos != null && waypointSequence.size() < N) {
            vx = attacker.velocity().x;
            vy = attacker.velocity().y;
            mainDegree = Math.toDegrees(Math.atan2(vy , vx));
            attacker.position().x = currentPos.origin.x;
            attacker.position().y = currentPos.origin.y;
            /**Need To Implement the velocity interface*/
            nextPosList = DirectionCalculator.getNextPosList(attacker, 1, (v, theta) -> v / (1 + theta));
            targetDegree = getThetaToX(currentPos, targetPos);
            thetaGap = getThetaGap(targetDegree, nextPosList);
            if (outLimitDegree(targetDegree))
                normalArea = toOutLimitNormalArea(thetaGap);
            else
                normalArea = toNormalArea(thetaGap);
            transformedPro = transformPro(normalArea);
            normalizedPro = normalization(transformedPro);
            chooseNextPose(waypointSequence, normalizedPro, nextPosList, mainDegree);

            if (reachedOrNot(currentPos, targetPos)) {
                if (AreaPath.size() != 0) {
                    this.targetPos = AreaPath.get(0);
                    AreaPath.remove(0);
                } else
                    this.targetPos = null;
            }
        }
        return waypointSequence;
    }

}
