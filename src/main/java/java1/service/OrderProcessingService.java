package java1.service;

import java1.consumer.OrderConsumer;
import java1.model.Order;
import java1.producer.OrderProducer;
import java1.validator.OrderValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class OrderProcessingService {
    private static final Logger logger = Logger.getLogger(OrderProcessingService.class.getName());

    private final BlockingQueue<Order> orderQueue;
    private final ConcurrentMap<String, Order> processedOrders;
    private final OrderValidator validator;
    private final ExecutorService executorService;
    private final OrderProducer producer;
    private final List<OrderConsumer> consumers;
    private final AtomicLong orderCounter;

    private volatile boolean isRunning;

    public OrderProcessingService(int numberOfConsumers, int queueCapacity) {
        this.orderQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.processedOrders = new ConcurrentHashMap<>();
        this.validator = new OrderValidator();
        this.executorService = Executors.newFixedThreadPool(numberOfConsumers + 1);
        this.orderCounter = new AtomicLong(0);
        this.consumers = new ArrayList<>();

        this.producer = new OrderProducer(orderQueue, orderCounter);

        for (int i = 1; i <= numberOfConsumers; i++) {
            consumers.add(new OrderConsumer(orderQueue, processedOrders, "Потребитель-" + i, validator));
        }

        this.isRunning = false;
    }

    public void start() {
        if (isRunning) {
            logger.warning("Служба уже запущена");
            return;
        }

        isRunning = true;
        executorService.submit(producer);

        for (OrderConsumer consumer : consumers) {
            executorService.submit(consumer);
        }

        logger.info("Запущена служба обработки заказов");
    }

    public void stop() {
        if (!isRunning) {
            logger.warning("Служба не запущена");
            return;
        }

        logger.info("Остановка обслуживания");
        isRunning = false;

        producer.stop();

        for (OrderConsumer consumer : consumers) {
            consumer.stop();
        }

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warning("Принудительное выключение");
                executorService.shutdownNow();

                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    logger.severe("Обслуживание исполнителя не было прекращено");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        logger.info("Служба обработки заказов остановлена");
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

    public boolean isServiceRunning() {
        return isRunning;
    }
}