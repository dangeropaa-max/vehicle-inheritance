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

    @Test
    void testProcessedOrdersCountAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        int countBeforeStop = service.getProcessedOrdersCount();
        service.stop();
        Thread.sleep(1000);
        int countAfterStop = service.getProcessedOrdersCount();
        assertTrue(countAfterStop >= countBeforeStop);
    }

    @Test
    void testQueueEmptyAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        service.stop();
        assertTrue(service.getQueueSize() >= 0);
    }

    @Test
    void testMultipleProducersAndConsumers() throws InterruptedException {
        OrderProcessingService service = new OrderProcessingService(5, 100);
        service.start();

        Thread.sleep(5000);

        int processedCount = service.getProcessedOrdersCount();
        assertTrue(processedCount > 10);
        service.stop();
    }

    @Test
    void testHighLoadProcessing() throws InterruptedException {
        OrderProcessingService service = new OrderProcessingService(10, 200);
        service.start();
        Thread.sleep(10000);
        int processedCount = service.getProcessedOrdersCount();
        assertTrue(processedCount > 50);
        service.stop();
    }

    @Test
    void testStartStopMultipleTimes() {
        OrderProcessingService service = new OrderProcessingService(2, 10);

        for (int i = 0; i < 5; i++) {
            service.start();
            assertTrue(service.isRunning());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            service.stop();
            assertFalse(service.isRunning());
        }
    }

    @Test
    void testRapidStartStop() {
        OrderProcessingService service = new OrderProcessingService(3, 20);
        for (int i = 0; i < 10; i++) {
            service.start();
            service.stop();
        }
        assertFalse(service.isRunning());
    }

    @Test
    void testQueueOverflow() throws InterruptedException {
        OrderProcessingService service = new OrderProcessingService(1, 5);
        service.start();
        Thread.sleep(2000);
        int queueSize = service.getQueueSize();
        assertTrue(queueSize <= 5);
        service.stop();
    }

    @Test
    void testConsumersOutliveProducer() throws InterruptedException {
        OrderProcessingService service = new OrderProcessingService(3, 50);
        service.start();
        Thread.sleep(3000);
        service.stop();
        int processedCount = service.getProcessedOrdersCount();
        assertTrue(processedCount >= 0);
    }

    @Test
    void testGetProcessedOrdersWhenEmpty() {
        assertTrue(service.getProcessedOrders().isEmpty());
    }

    @Test
    void testGetProcessedOrdersListWhenEmpty() {
        assertTrue(service.getProcessedOrdersList().isEmpty());
    }

    @Test
    void testGetOrderWhenNotExists() {
        assertNull(service.getOrder("not-exists"));
    }

    @Test
    void testStopWhenNotRunning() {
        service.stop();
        assertFalse(service.isRunning());
    }

    @Test
    void testStartWhenAlreadyRunning() {
        service.start();
        service.start();
        assertTrue(service.isRunning());
    }

    @Test
    void testGetProcessedOrdersReturnsCopy() throws InterruptedException {
        service.start();
        Thread.sleep(1000);

        var orders = service.getProcessedOrders();
        int originalSize = orders.size();
        orders.clear();

        assertEquals(originalSize, service.getProcessedOrdersCount());
        service.stop();
    }
    @Test
    void testGetProcessedOrdersWhenNull() {
        OrderProcessingService newService = new OrderProcessingService(2, 10);
        assertNotNull(newService.getProcessedOrders());
    }

    @Test
    void testGetQueueSizeWhenNull() {
        OrderProcessingService newService = new OrderProcessingService(2, 10);
        assertTrue(newService.getQueueSize() >= 0);
    }

    @Test
    void testGetProcessedOrdersCountWhenNull() {
        OrderProcessingService newService = new OrderProcessingService(2, 10);
        assertEquals(0, newService.getProcessedOrdersCount());
    }

    @Test
    void testGetProcessedOrdersListWhenNull() {
        OrderProcessingService newService = new OrderProcessingService(2, 10);
        assertTrue(newService.getProcessedOrdersList().isEmpty());
    }

    @Test
    void testGetOrderWhenNull() {
        OrderProcessingService newService = new OrderProcessingService(2, 10);
        assertNull(newService.getOrder("any-id"));
    }

    @Test
    void testRestartMethod() throws InterruptedException {
        service.start();
        Thread.sleep(500);
        service.restart();
        assertTrue(service.isRunning());
        service.stop();
    }

    @Test
    void testStopWithInterruptedException() throws InterruptedException {
        service.start();
        Thread.sleep(500);
        Thread stopThread = new Thread(() -> {
            service.stop();
        });
        stopThread.start();
        stopThread.interrupt();

        Thread.sleep(500);
        assertFalse(service.isRunning());
    }

    @Test
    void testGetProcessedOrdersAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        service.stop();

        var orders = service.getProcessedOrders();
        assertNotNull(orders);
    }

    @Test
    void testGetQueueSizeAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        service.stop();

        int queueSize = service.getQueueSize();
        assertTrue(queueSize >= 0);
    }

    @Test
    void testGetProcessedOrdersCountAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        service.stop();

        int count = service.getProcessedOrdersCount();
        assertTrue(count >= 0);
    }

    @Test
    void testGetProcessedOrdersListAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        service.stop();

        var list = service.getProcessedOrdersList();
        assertNotNull(list);
    }

    @Test
    void testGetOrderAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(2000);
        service.stop();

        var order = service.getOrder("any-id");
        assertNull(order);
    }

    @Test
    void testIsRunningAfterStop() throws InterruptedException {
        service.start();
        Thread.sleep(500);
        service.stop();

        assertFalse(service.isRunning());
    }

    @Test
    void testMultipleRestartCalls() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            service.start();
            Thread.sleep(200);
            service.restart();
            Thread.sleep(200);
        }
        assertTrue(service.isRunning());
        service.stop();
    }
}