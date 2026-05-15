package java1.consumer;

import java1.model.Order;
import java1.model.OrderStatus;
import java1.validator.OrderValidator;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class OrderConsumer implements Runnable {
    private static final Logger logger = Logger.getLogger(OrderConsumer.class.getName());
    private final BlockingQueue<Order> orderQueue;
    private final ConcurrentMap<String, Order> processedOrders;
    private final OrderValidator validator;
    private final String consumerId;
    private volatile boolean running;

    public OrderConsumer(BlockingQueue<Order> orderQueue, ConcurrentMap<String, Order> processedOrders,
                         String consumerId, OrderValidator validator) {
        this.orderQueue = orderQueue;
        this.processedOrders = processedOrders;
        this.consumerId = consumerId;
        this.validator = validator;
        this.running = true;
    }

    @Override
    public void run() {
        logger.info(String.format("[%s] Consumer started", consumerId));

        while (running) {
            try {
                Order order = orderQueue.take();
                logger.info(String.format("[%s] Received order: %s", consumerId, order.getId()));

                processOrder(order);

                Thread.sleep(1000 + (int)(Math.random() * 500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning(String.format("[%s] Consumer interrupted", consumerId));
                break;
            }
        }

        logger.info(String.format("[%s] Consumer stopped", consumerId));
    }

    private void processOrder(Order order) {
        order.setStatus(OrderStatus.PROCESSING);

        List<String> validationErrors = validator.validate(order);

        if (validationErrors.isEmpty()) {
            try {
                Thread.sleep(500 + (int)(Math.random() * 500));
                order.setStatus(OrderStatus.COMPLETED);
                processedOrders.put(order.getId(), order);
                logger.info(String.format("[%s] Успешно обработанный заказ %s (urgent: %s)",
                        consumerId, order.getId(), order.isUrgent()));
            } catch (InterruptedException e) {
                order.setStatus(OrderStatus.FAILED);
                logger.warning(String.format("[%s] Не удалось обработать заказ %s: %s",
                        consumerId, order.getId(), e.getMessage()));
                Thread.currentThread().interrupt();
            }
        } else {
            order.setStatus(OrderStatus.FAILED);
            logger.warning(String.format("[%s] Заказ %s не удалось подтвердить: %s",
                    consumerId, order.getId(), String.join(", ", validationErrors)));
        }
    }

    public void stop() {
        running = false;
    }
}