package java1.service;

import java1.consumer.OrderConsumer;
import java1.model.Order;
import java1.producer.OrderProducer;
import java1.validator.OrderValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class OrderProcessingService {
    private final BlockingQueue<Order> orderQueue;
    private final ConcurrentMap<String, Order> processedOrders;
    private final OrderValidator validator;
    private final OrderProducer producer;
    private final List<OrderConsumer> consumers;
    private final ExecutorService executorService;
    private final AtomicLong orderCounter;
    private volatile boolean isRunning;

    public OrderProcessingService(int numberOfConsumers, int queueCapacity) {
        this.orderQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.processedOrders = new ConcurrentHashMap<>();
        this.validator = new OrderValidator();
        this.orderCounter = new AtomicLong(0);
        this.consumers = new ArrayList<>();
        this.producer = new OrderProducer(orderQueue, orderCounter);

        for (int i = 0; i < numberOfConsumers; i++) {
            consumers.add(new OrderConsumer(orderQueue, processedOrders, "Потребитель-" + (i + 1), validator));
        }

        this.executorService = Executors.newFixedThreadPool(numberOfConsumers + 1);
        this.isRunning = false;
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        executorService.submit(producer);
        for (OrderConsumer consumer : consumers) {
            executorService.submit(consumer);
        }
        System.out.println("Сервис обработки заказов запущен");
    }

    public void stop() {
        if (!isRunning) return;
        isRunning = false;
        producer.stop();
        for (OrderConsumer consumer : consumers) {
            consumer.stop();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Сервис обработки заказов остановлен");
    }

    public ConcurrentMap<String, Order> getProcessedOrders() {
        return new ConcurrentHashMap<>(processedOrders);
    }

    public int getQueueSize() {
        return orderQueue.size();
    }

    public int getProcessedOrdersCount() {
        return processedOrders.size();
    }

    public List<Order> getProcessedOrdersList() {
        return new ArrayList<>(processedOrders.values());
    }

    public Order getOrder(String orderId) {
        return processedOrders.get(orderId);
    }

    public boolean isRunning() {
        return isRunning;
    }
}