package test.service;

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
        if (service.isServiceRunning()) {
            service.stop();
        }
    }

    @Test
    void testServiceStartAndStop() {
        assertFalse(service.isServiceRunning());

        service.start();
        assertTrue(service.isServiceRunning());

        service.stop();
        assertFalse(service.isServiceRunning());
    }

    @Test
    void testStartServiceOnlyOnce() {
        service.start();
        assertTrue(service.isServiceRunning());

        service.start();
        assertTrue(service.isServiceRunning());
    }

    @Test
    void testStopServiceWhenNotRunning() {
        assertFalse(service.isServiceRunning());
        service.stop();
        assertFalse(service.isServiceRunning());
    }

    @Test
    void testProcessedOrdersCollection() throws InterruptedException {
        service.start();

        Thread.sleep(3000);

        int processedCount = service.getProcessedOrdersCount();
        assertTrue(processedCount >= 0);

        var processedOrders = service.getProcessedOrders();
        assertNotNull(processedOrders);
    }

    @Test
    void testQueueSize() throws InterruptedException {
        service.start();

        Thread.sleep(2000);

        int queueSize = service.getQueueSize();
        assertTrue(queueSize >= 0);
    }

    @Test
    void testGetProcessedOrdersList() throws InterruptedException {
        service.start();

        Thread.sleep(3000);

        var orders = service.getProcessedOrdersList();
        assertNotNull(orders);
    }
}