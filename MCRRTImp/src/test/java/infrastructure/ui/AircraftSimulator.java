package infrastructure.ui;

public class AircraftSimulator {

    private static double _deltaTime;
    private static Thread thread = new Thread(AircraftSimulator::run);

    private static void run() {
//        while (true) {
//            List<Attacker> attackers = World.attacker();
//            long startTime = System.currentTimeMillis();
//            for (Attacker attacker : attackers) {
//                int action = 0;
//                DimensionalPath<DimensionalWayPoint> path = attacker.actualPath();
//                Vector2 position = attacker.position();
//                Vector2 velocity = attacker.velocity();
//                while (true) {
//                    if (path != null && path.size() != 0) {
//                        DimensionalWayPoint incomingWayPoint = path.start();
//                        action = incomingWayPoint.actionIdx;
//                        if (incomingWayPoint.origin.distance2(position) <= incomingWayPoint.radius * incomingWayPoint.radius) {
//                            path.removeAt(0);
//                            continue;
//                        }
//                    }
//                    break;
//                }
//                List<Transform> transforms = attacker.simulateKinetic(position, velocity, _deltaTime);
//                Vector2 newPosition = transforms.get(action).position.cpy();
//                Vector2 newVelocity = transforms.get(action).velocity.cpy();
//                attacker.setPosition(newPosition);
//                attacker.setVelocity(newVelocity);
//
//            }
//            long usedTime = System.currentTimeMillis() - startTime;
//            if (usedTime >= _deltaTime * 1000) {
//                System.out.println("simulation time exceeded");
//                continue;
//            }
//            try {
//                Thread.sleep((long) (_deltaTime * 1000 - usedTime));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static void start(double deltaTime) {
        _deltaTime = deltaTime;
        thread.start();
    }


}
