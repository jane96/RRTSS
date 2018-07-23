package lab.mars.MCRRTImp;

import lab.mars.RRTBase.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCRRT extends RRT<Attacker, Vector2, WayPoint2D, Path2D<WayPoint2D>> {

    private float width;

    private float height;

    private Applier<Grid2D> grid2DApplier;

    public MCRRT(float deltaTime,
                 float w, float h,
                 Provider<List<Obstacle<Vector2>>> obstacleProvider,
                 Provider<Attacker> aircraftProvider,
                 Provider<WayPoint2D> targetProvider,
                 Applier<Path2D<WayPoint2D>> pathApplier,
                 Applier<Grid2D> grid2DApplier
    ) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.width = w;
        this.height = h;
        this.grid2DApplier = grid2DApplier;
    }

    private Path2D<Cell2D> firstLevelRRT() {
        double aircraftViewRange = aircraft.viewDistance();
        double aircraftViewAngle = aircraft.viewAngle();
        Vector2 aircraftPosition = aircraft.position();
        Vector2 aircraftDirection = aircraft.velocity();
        Grid2D gridWorld;
        int scaleBase = 50;
        NTreeNode<Cell2D> pathRoot;
        double gridCellEdgeLength;
        Vector2 targetPositionInGridWorld;
        while (true) {
            gridWorld = new Grid2D((int) width, (int) height, scaleBase);
            gridWorld.scan(obstacles);
            Vector2 gridAircraft = gridWorld.transformToCellCenter(aircraftPosition);
            gridCellEdgeLength = gridWorld.cellSize();
            pathRoot = new NTreeNode<>(new Cell2D(gridAircraft, gridCellEdgeLength));
            targetPositionInGridWorld = gridWorld.findNearestGridCenter(target.origin);
            grid2DApplier.apply(gridWorld);
            if (targetPositionInGridWorld == null) {
                scaleBase -= 1;
                if (scaleBase == 4) {
                    scaleBase = 5;
                }
                continue;
            }
            if (scaleBase == 5) {
                throw new RuntimeException("cannot solve the map");
            }
            int step_count = 0;
            while (step_count < 1000) {
                Cell2D sampled = new Cell2D(gridWorld.sample(), gridCellEdgeLength);
                NTreeNode<Cell2D> nearestNode = pathRoot.findNearest(sampled, (c1, c2) -> c1.centroid.distance2(c2.centroid));
                Vector2 direction = sampled.centroid.cpy().subtract(nearestNode.getElement().centroid);
                Vector2 stepped = nearestNode.getElement().centroid.cpy().add(direction.normalize().scale(gridCellEdgeLength));
                if (gridWorld.check(stepped)) {
                    continue;
                }
                sampled.centroid.set(gridWorld.transformToCellCenter(stepped));
                nearestNode.createChild(sampled);
                if (sampled.centroid.epsilonEquals(targetPositionInGridWorld, 0.001)) {
                    List<Cell2D> path = pathRoot.findTrace(sampled);
                    Path2D<Cell2D> cellPath = new Path2D<>();
                    path.forEach(cellPath::add);
                    return cellPath;
                }
                step_count++;
            }
            scaleBase -= 1;
            if (scaleBase == 4) {
                scaleBase = 5;
            }
        }

    }

    private Path2D<WayPoint2D> secondLevelRRT() {
        throw  new NotImplementedException();
    }

    @Override
    public Path2D<WayPoint2D> algorithm() {
        Path2D<Cell2D> areaPath = firstLevelRRT();
        Path2D<WayPoint2D> ret = new Path2D<>();

        return ret;
    }
}
