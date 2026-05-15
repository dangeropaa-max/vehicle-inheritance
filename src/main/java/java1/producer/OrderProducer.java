package java1.producer;

import java1.model.Order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class OrderProducer implements Runnable {
    private static final Logger logger = Logger.getLogger(OrderProducer.class.getName());
    private final BlockingQueue<Order> orderQueue;
    private final AtomicLong orderCounter;
    private volatile boolean running;
    private final String[] customers = {"Иван Иванов", "Петр Петров", "Мария Сидорова", "Анна Козлова", "Сергей Смирнов"};
    private final String[] products = {"Ноутбук", "Смартфон", "Планшет", "Наушники", "Клавиатура", "Мышь"};

    public OrderProducer(BlockingQueue<Order> orderQueue, AtomicLong orderCounter) {
        this.orderQueue = orderQueue;
        this.orderCounter = orderCounter;
        this.running = true;
    }

    @Override
    public void run() {
        logger.info("Producer started");

        while (running) {
            try {
                Order order = generateOrder();
                orderQueue.put(order);
                logger.info(String.format("[PRODUCER] Created order: %s", order));

                Thread.sleep(500 + (int)(Math.random() * 500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning("Producer interrupted");
                break;
            }
        }

        logger.info("Producer stopped");
    }

    private Order generateOrder() {
        String customer = customers[(int)(Math.random() * customers.length)];
        String product = products[(int)(Math.random() * products.length)];
        int quantity = 1 + (int)(Math.random() * 10);
        boolean urgent = Math.random() < 0.3;

        long orderNumber = orderCounter.incrementAndGet();
        String orderId = String.format("ORD-%05d", orderNumber);

        return new Order(orderId, customer, product, quantity, urgent);
    }

    public void stop() {
        running = false;
    }
}