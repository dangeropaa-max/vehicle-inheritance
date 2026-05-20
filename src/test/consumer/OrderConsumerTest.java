package consumer;

import java1.model.Order;
import java1.consumer.OrderConsumer;
import java1.model.OrderStatus;
import java1.validator.OrderValidator;
import org.junit.jupiter.api.Test;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import static org.junit.jupiter.api.Assertions.*;

class OrderConsumerExtraTest {

    @Test
    void testProcessOrderWithInterruption() throws InterruptedException {
        BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<String, Order> processed = new ConcurrentHashMap<>();
        OrderValidator validator = new OrderValidator();

        Order order = new Order("1", "Customer", "Product", 1, false);
        queue.put(order);

        OrderConsumer consumer = new OrderConsumer(queue, processed, "Test", validator);
        Thread thread = new Thread(consumer);
        thread.start();

        Thread.sleep(50);
        thread.interrupt();

        Thread.sleep(500);
        assertFalse(thread.isAlive());
    }

    @Test
    void testProcessOrderWithFailedValidation() throws InterruptedException {
        BlockingQueue<Order> queue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<String, Order> processed = new ConcurrentHashMap<>();
        OrderValidator validator = new OrderValidator();

        Order invalidOrder = new Order();
        invalidOrder.setId(null);
        invalidOrder.setCustomerName(null);
        invalidOrder.setProductDescription(null);
        queue.put(invalidOrder);

        OrderConsumer consumer = new OrderConsumer(queue, processed, "Test", validator);
        Thread thread = new Thread(consumer);
        thread.start();

        Thread.sleep(500);

        assertTrue(processed.isEmpty());

        consumer.stop();
        thread.interrupt();
    }
}