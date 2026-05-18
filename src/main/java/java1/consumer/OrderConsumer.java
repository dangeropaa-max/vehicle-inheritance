package java1.consumer;

import java1.model.Order;
import java1.model.OrderStatus;
import java1.validator.OrderValidator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class OrderConsumer implements Runnable {
    private final BlockingQueue<Order> queue;
    private final ConcurrentMap<String, Order> processedOrders;
    private final OrderValidator validator;
    private final String consumerId;
    private volatile boolean running = true;

    public OrderConsumer(BlockingQueue<Order> queue, ConcurrentMap<String, Order> processedOrders,
                         String consumerId, OrderValidator validator) {
        this.queue = queue;
        this.processedOrders = processedOrders;
        this.consumerId = consumerId;
        this.validator = validator;
    }

    @Override
    public void run() {
        System.out.println(consumerId + " запущен");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Order order = queue.poll(1, TimeUnit.SECONDS);
                if (order != null) {
                    System.out.println(consumerId + " получил заказ " + order.getId());
                    processOrder(order);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(consumerId + " остановлен");
    }

    private void processOrder(Order order) {
        order.setStatus(OrderStatus.PROCESSING);
        List<String> errors = validator.validate(order);

        if (errors.isEmpty()) {
            try {
                Thread.sleep(200);
                order.setStatus(OrderStatus.COMPLETED);
                processedOrders.put(order.getId(), order);
                System.out.println(consumerId + " успешно обработал заказ " + order.getId());
            } catch (InterruptedException e) {
                order.setStatus(OrderStatus.FAILED);
                Thread.currentThread().interrupt();
            }
        } else {
            order.setStatus(OrderStatus.FAILED);
            System.out.println(consumerId + " заказ " + order.getId() + " не прошел валидацию: " + String.join(", ", errors));
        }
    }

    public void stop() {
        running = false;
    }

    public String getConsumerId() {
        return consumerId;
    }
}