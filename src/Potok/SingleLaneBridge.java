import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class SingleLaneBridge {
    private static final int NORTH = 0;
    private static final int SOUTH = 1;

    private final Semaphore bridge = new Semaphore(1, true);
    private int currentDirection = -1;
    private int carsOnBridge = 0;

    public static void main(String[] args) {
        SingleLaneBridge bridge = new SingleLaneBridge();
        bridge.startSimulation();
    }

    private void startSimulation() {
        for (int i = 1; i <= 10; i++) {
            Car northCar = new Car(i, NORTH);
            Car southCar = new Car(i + 10, SOUTH);
            new Thread(northCar).start();
            new Thread(southCar).start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Car implements Runnable {
        private final int id;
        private final int direction;

        Car(int id, int direction) {
            this.id = id;
            this.direction = direction;
        }

        private String directionName() {
            return direction == NORTH ? "NORTH" : "SOUTH";
        }

        @Override
        public void run() {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 800));

                System.out.printf("Car %d (%s) arrived at bridge.%n", id, directionName());

                while (true) {
                    bridge.acquire();

                    if (currentDirection == -1 || currentDirection == direction) {
                        currentDirection = direction;
                        carsOnBridge++;
                        bridge.release();
                        break;
                    }

                    bridge.release();
                    Thread.sleep(50);
                }

                System.out.printf("Car %d (%s) is crossing.%n", id, directionName());
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1200));

                bridge.acquire();
                carsOnBridge--;
                if (carsOnBridge == 0) {
                    currentDirection = -1;
                }
                bridge.release();

                System.out.printf("Car %d (%s) crossed.%n", id, directionName());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}