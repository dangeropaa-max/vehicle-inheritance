package java1.producer;

import java1.model.Order;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class OrderProducer implements Runnable {
    private final BlockingQueue<Order> queue;
    private final AtomicLong orderCounter;
    private volatile boolean running = true;

    private final List<String> customers = List.of(
            "Иван Иванов", "Петр Петров", "Мария Сидорова", "Анна Козлова", "Сергей Смирнов"
    );

    private final List<String> products = List.of(
            "Ноутбук", "Смартфон", "Планшет", "Наушники", "Клавиатура", "Мышь"
    );

    public OrderProducer(BlockingQueue<Order> queue, AtomicLong orderCounter) {
        this.queue = queue;
        this.orderCounter = orderCounter;
    }

    @Override
    public void run() {
        System.out.println("Производитель запущен");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Order order = createOrder();
                queue.put(order);
                System.out.println("Создан заказ: " + order);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("Производитель остановлен");
    }

    private Order createOrder() {
        String customer = customers.get((int) (Math.random() * customers.size()));
        String product = products.get((int) (Math.random() * products.size()));
        int quantity = 1 + (int) (Math.random() * 5);
        boolean urgent = Math.random() < 0.3;
        long id = orderCounter.incrementAndGet();
        return new Order(String.valueOf(id), customer, product, quantity, urgent);
    }

    public void stop() {
        running = false;
    }
}