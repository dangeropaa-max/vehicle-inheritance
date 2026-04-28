import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Seaport {
    private static final int BERTH_COUNT = 3;
    private static final int SHIP_COUNT = 10;

    private final Semaphore berths = new Semaphore(BERTH_COUNT, true);
    private final AtomicInteger shipsServed = new AtomicInteger(0);

    public static void main(String[] args) {
        Seaport port = new Seaport();
        port.startSimulation();
    }

    private void startSimulation() {
        System.out.println("Port open. Available berths: " + BERTH_COUNT);

        for (int i = 1; i <= SHIP_COUNT; i++) {
            Ship ship = new Ship(i);
            new Thread(ship).start();
        }

        while (shipsServed.get() < SHIP_COUNT) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("All ships served. Port closing.");
    }

    class Ship implements Runnable {
        private final int id;

        Ship(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            System.out.printf("Ship %d arrived and waiting for berth.%n", id);

            try {
                berths.acquire();

                System.out.printf("Ship %d docked. Free berths left: %d.%n",
                        id, berths.availablePermits());

                int processingTime = ThreadLocalRandom.current().nextInt(300, 1500);
                Thread.sleep(processingTime);

                System.out.printf("Ship %d finished operations in %d ms.%n",
                        id, processingTime);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                berths.release();
                shipsServed.incrementAndGet();
                System.out.printf("Ship %d left. Free berths: %d.%n",
                        id, berths.availablePermits());
            }
        }
    }
}