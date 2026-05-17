package service;

import java1.service.OrderProcessingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessingServiceTest {

    private OrderProcessingService service;

    @BeforeEach
    void setUp() {
        service = new OrderProcessingService(2, 10);
    }

    @AfterEach
    void tearDown() {
        if (service.isRunning()) {
            service.stop();
        }
    }

    @Test
    void testServiceStart() {
        assertFalse(service.isRunning());
        service.start();
        assertTrue(service.isRunning());
    }

    @Test
    void testServiceStop() {
        service.start();
        assertTrue(service.isRunning());
        service.stop();
        assertFalse(service.isRunning());
    }

    @Test
    void testProcessOrders() throws InterruptedException {
        service.start();
        Thread.sleep(3000);
        assertTrue(service.getProcessedOrdersCount() > 0);
    }

    @Test
    void testGetQueueSize() throws InterruptedException {
        service.start();
        Thread.sleep(1000);
        assertTrue(service.getQueueSize() >= 0);
    }

    @Test
    void testMultipleConsumers() throws InterruptedException {
        OrderProcessingService multiService = new OrderProcessingService(4, 20);
        multiService.start();
        Thread.sleep(3000);
        assertTrue(multiService.getProcessedOrdersCount() > 0);
        multiService.stop();
    }

    @Test
    void testGetProcessedOrdersList() throws InterruptedException {
        service.start();
        Thread.sleep(3000);
        assertNotNull(service.getProcessedOrdersList());
    }

    @Test
    void testGetOrderById() throws InterruptedException {
        service.start();
        Thread.sleep(3000);
        var orders = service.getProcessedOrdersList();
        if (!orders.isEmpty()) {
            var order = service.getOrder(orders.get(0).getId());
            assertNotNull(order);
            assertEquals(orders.get(0).getId(), order.getId());
        }
    }

    @Test
    void testStopWhileProcessing() throws InterruptedException {
        service.start();
        Thread.sleep(500);
        service.stop();
        assertFalse(service.isRunning());
    }

    @Test
    void testStartIdempotent() {
        service.start();
        service.start();
        assertTrue(service.isRunning());
    }

    @Test
    void testStopIdempotent() {
        service.stop();
        service.stop();
        assertFalse(service.isRunning());
    }
}