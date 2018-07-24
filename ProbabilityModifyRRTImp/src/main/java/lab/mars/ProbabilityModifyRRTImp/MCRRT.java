package lab.mars.ProbabilityModifyRRTImp;


import lab.mars.RRTBase.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D> {


    public MCRRT(float deltaTime,
                 Provider<List<Obstacle<Vector2>>> obstacleProvider,
                 Provider<Attacker> aircraftProvider,
                 Provider<WayPoint2D> targetProvider,
                 Applier<Path2D> pathApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
    }


    @Override
    public Path2D algorithm() {
        ArrayList <CircleObstacle> listObstacle = new ArrayList<CircleObstacle>();
        CircleObstacle ob1 = new CircleObstacle(500, 600, 150, 160);
        listObstacle.add(ob1);
        CircleObstacle ob2 = new CircleObstacle(800, 400, 100, 120);
        listObstacle.add(ob2);

        WayPoint2D startPoint = new WayPoint2D(100,900, 0, 0);
        WayPoint2D targetPoint = new WayPoint2D(900, 100, 0, 0);
        ArrayList <WayPoint2D> flightTrail = new ArrayList<>();
        flightTrail.add(startPoint);

        int n_iters = 100;
        int step = 2;
        float main_degree = -45;
        float [] available_directions = {main_degree + 5, main_degree + 2.5f, main_degree, main_degree - 2.5f, main_degree - 5};
        float [] defalut_probability = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};

        int i_step = 0;
        int n_steps = 1000;

        while(i_step < n_steps){
            // 每次循环前先初始化概率值
            float [] probability = defalut_probability;

            for(CircleObstacle ob: listObstacle){

            }

            i_step += 1;
        }




        return null;
 }
}

