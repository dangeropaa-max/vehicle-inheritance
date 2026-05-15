package test.model;

import java1.model.Order;
import java1.model.OrderStatus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderCreation() {
        Order order = new Order("test-id", "John Doe", "Laptop", 2, true);

        assertEquals("test-id", order.getId());
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("Laptop", order.getProductDescription());
        assertEquals(2, order.getQuantity());
        assertTrue(order.isUrgent());
        assertNotNull(order.getOrderDate());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testOrderCreationWithAutoId() {
        Order order = new Order("Jane Doe", "Tablet", 1, false);

        assertNotNull(order.getId());
        assertFalse(order.getId().isEmpty());
        assertEquals("Jane Doe", order.getCustomerName());
        assertFalse(order.isUrgent());
    }

    @Test
    void testStatusChange() {
        Order order = new Order("test-id", "John Doe", "Product", 1, false);

        order.setStatus(OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());

        order.setStatus(OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());

        order.setStatus(OrderStatus.FAILED);
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }

    @Test
    void testOrderEquality() {
        Order order1 = new Order("same-id", "John Doe", "Product", 1, false);
        Order order2 = new Order("same-id", "Jane Smith", "Different", 2, true);

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testOrderInequality() {
        Order order1 = new Order("id1", "John Doe", "Product", 1, false);
        Order order2 = new Order("id2", "John Doe", "Product", 1, false);

        assertNotEquals(order1, order2);
    }
}