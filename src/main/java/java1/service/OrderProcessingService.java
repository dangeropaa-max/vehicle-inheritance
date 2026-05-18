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
    private BlockingQueue<Order> orderQueue;
    private ConcurrentMap<String, Order> processedOrders;
    private final OrderValidator validator;
    private OrderProducer producer;
    private List<OrderConsumer> consumers;
    private ExecutorService executorService;
    private final AtomicLong orderCounter;
    private volatile boolean isRunning;
    private final int numberOfConsumers;
    private final int queueCapacity;

    public OrderProcessingService(int numberOfConsumers, int queueCapacity) {
        this.numberOfConsumers = numberOfConsumers;
        this.queueCapacity = queueCapacity;
        this.validator = new OrderValidator();
        this.orderCounter = new AtomicLong(0);
        this.isRunning = false;
        initComponents();
    }

    private void initComponents() {
        this.orderQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.processedOrders = new ConcurrentHashMap<>();
        this.consumers = new ArrayList<>();
        this.producer = new OrderProducer(orderQueue, orderCounter);

        for (int i = 0; i < numberOfConsumers; i++) {
            consumers.add(new OrderConsumer(orderQueue, processedOrders, "Потребитель-" + (i + 1), validator));
        }

        this.executorService = Executors.newFixedThreadPool(numberOfConsumers + 1);
    }

    public void start() {
        if (isRunning) {
            return;
        }

        if (executorService == null || executorService.isShutdown()) {
            initComponents();
        }

        isRunning = true;
        executorService.submit(producer);
        for (OrderConsumer consumer : consumers) {
            executorService.submit(consumer);
        }
        System.out.println("Сервис обработки заказов запущен");
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

        System.out.println("Остановка сервиса");
        isRunning = false;

        if (producer != null) {
            producer.stop();
        }

        if (consumers != null) {
            for (OrderConsumer consumer : consumers) {
                if (consumer != null) {
                    consumer.stop();
                }
            }
        }

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Сервис обработки заказов остановлен");
    }

    public void restart() {
        stop();
        initComponents();
        start();
    }

    public ConcurrentMap<String, Order> getProcessedOrders() {
        if (processedOrders == null) {
            return new ConcurrentHashMap<>();
        }
        return new ConcurrentHashMap<>(processedOrders);
    }

    public int getQueueSize() {
        if (orderQueue == null) {
            return 0;
        }
        return orderQueue.size();
    }

    public int getProcessedOrdersCount() {
        if (processedOrders == null) {
            return 0;
        }
        return processedOrders.size();
    }

    public List<Order> getProcessedOrdersList() {
        if (processedOrders == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(processedOrders.values());
    }

    public Order getOrder(String orderId) {
        if (processedOrders == null) {
            return null;
        }
        return processedOrders.get(orderId);
    }

    public boolean isRunning() {
        return isRunning;
    }
}