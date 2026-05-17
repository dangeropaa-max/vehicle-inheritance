package model;

import java1.model.Order;
import java1.model.OrderStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testDefaultConstructor() {
        Order order = new Order();
        assertNotNull(order.getId());
        assertNotNull(order.getOrderDate());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testParameterizedConstructor() {
        Order order = new Order("123", "Иван", "Товар", 5, true);
        assertEquals("123", order.getId());
        assertEquals("Иван", order.getCustomerName());
        assertEquals("Товар", order.getProductDescription());
        assertEquals(5, order.getQuantity());
        assertTrue(order.isUrgent());
    }

    @Test
    void testOrderEquality() {
        Order order1 = new Order("same-id", "Иван", "Товар", 1, false);
        Order order2 = new Order("same-id", "Мария", "Другой", 2, true);
        assertEquals(order1, order2);
    }

    @Test
    void testOrderInequality() {
        Order order1 = new Order("id1", "Иван", "Товар", 1, false);
        Order order2 = new Order("id2", "Иван", "Товар", 1, false);
        assertNotEquals(order1, order2);
    }

    @Test
    void testSettersAndGetters() {
        Order order = new Order();
        order.setId("456");
        order.setCustomerName("Мария");
        order.setProductDescription("Телефон");
        order.setQuantity(10);
        order.setUrgent(true);
        order.setStatus(OrderStatus.COMPLETED);
        assertEquals("456", order.getId());
        assertEquals("Мария", order.getCustomerName());
        assertEquals("Телефон", order.getProductDescription());
        assertEquals(10, order.getQuantity());
        assertTrue(order.isUrgent());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }
}