package lab.mars.MCRRTImp.algorithm;

import lab.mars.MCRRTImp.model.*;
import lab.mars.RRTBase.*;
import lab.mars.RRTBase.Vector;

import java.util.*;
@Deprecated
public class MCTSSampler<V extends Vector<V>> extends RRT<SimulatedVehicle<V>, V, DimensionalWayPoint<V>, DimensionalPath<DimensionalWayPoint<V>>> {

    private Space<V> spaceRestriction;

    private Applier<NTreeNode<DimensionalWayPoint<V>>> treeApplier;

    private Applier<List<NTreeNode<DimensionalWayPoint<V>>>> leafApplier;

    private Applier<List<DimensionalPath<DimensionalWayPoint<V>>>> pathListApplier;

    public MCTSSampler(double deltaTime,
                        Space<V> spaceRestriction,
                        Provider<List<Obstacle<V>>> obstacleProvider,
                        Provider<SimulatedVehicle<V>> aircraftProvider,
                        Provider<DimensionalWayPoint<V>> targetProvider,
                        Applier<DimensionalPath<DimensionalWayPoint<V>>> pathApplier,
                        Applier<NTreeNode<DimensionalWayPoint<V>>> treeApplier,
                        Applier<List<NTreeNode<DimensionalWayPoint<V>>>> leafApplier,
                        Applier<List<DimensionalPath<DimensionalWayPoint<V>>>> pathListApplier) {
        super(deltaTime, obstacleProvider, aircraftProvider, targetProvider, pathApplier);
        this.spaceRestriction = spaceRestriction;
        this.treeApplier = treeApplier;
        this.leafApplier = leafApplier;
        this.pathListApplier = pathListApplier;
    }

    private List<NTreeNode<DimensionalWayPoint<V>>> treeLeaves = new ArrayList<>();

    private boolean checkObstacles(V position) {
        for (Obstacle<V> obstacle :
                obstacles) {
            if (obstacle.contains(position)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DimensionalPath<DimensionalWayPoint<V>> algorithm() {
        NTreeNode<DimensionalWayPoint<V>> root = new NTreeNode<>(new DimensionalWayPoint<>(vehicle.position(), vehicle.velocity().len(), vehicle.velocity()));
        treeLeaves.add(root);
        List<DimensionalPath<DimensionalWayPoint<V>>> pathList = new ArrayList<>();
        Random random = new Random();
        while (pathList.size() < 5) {
            List<NTreeNode<DimensionalWayPoint<V>>> newLeaves = new ArrayList<>();
            int size = treeLeaves.size();
            int leafRandomIdx = random.nextInt(size);
            NTreeNode<DimensionalWayPoint<V>> leaf = treeLeaves.get(leafRandomIdx);
            List<Transform<V>> transforms = vehicle.simulateKinetic(leaf.getElement().velocity, deltaTime);
            size = transforms.size();
            int transformRandomIdx = random.nextInt(size);
            Transform<V> transform = transforms.get(transformRandomIdx);
            V position = transform.position.translate(leaf.getElement().origin);
            V velocity = transform.velocity;
            double radius = transform.position.len();
            NTreeNode<DimensionalWayPoint<V>> newLeaf = new NTreeNode<>(new DimensionalWayPoint<>(position, radius, velocity));
            leaf.concatChild(newLeaf);
            if (spaceRestriction.include(position) && checkObstacles(position)) {
                newLeaves.add(newLeaf);
            }
            if (target.origin.distance2(transform.position) < target.radius * target.radius) {
                List<DimensionalWayPoint<V>> trace = root.findTrace(newLeaf);
                DimensionalPath<DimensionalWayPoint<V>> path = new DimensionalPath<>();
                trace.forEach(path::add);
                pathList.add(path);
                System.out.println("add path with size : " + path.size());
            }
            if (newLeaves.size() != 0) {
                treeLeaves.remove(leafRandomIdx);
                treeLeaves.addAll(newLeaves);
            }
            if (this.leafApplier != null) {
                List<NTreeNode<DimensionalWayPoint<V>>> copied = new ArrayList<>(treeLeaves);
                this.leafApplier.apply(copied);
            }
        }
        pathList.sort(Comparator.comparingInt(DimensionalPath::size));
        if (this.pathListApplier != null) {
            this.pathListApplier.apply(pathList);
        }
        if (this.treeApplier != null) {
            this.treeApplier.apply(root);
        }
        return pathList.get(0);
    }
}
