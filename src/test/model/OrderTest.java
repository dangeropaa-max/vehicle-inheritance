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

    @Test
    void testConstructorWithAutoId() {
        Order order = new Order("Иван", "Товар", 3, false);
        assertNotNull(order.getId());
        assertEquals("Иван", order.getCustomerName());
        assertEquals("Товар", order.getProductDescription());
        assertEquals(3, order.getQuantity());
        assertFalse(order.isUrgent());
    }

    @Test
    void testHashCode() {
        Order order1 = new Order("same-id", "Иван", "Товар", 1, false);
        Order order2 = new Order("same-id", "Мария", "Другой", 2, true);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testToString() {
        Order order = new Order("123", "Иван", "Товар", 1, false);
        String str = order.toString();
        assertTrue(str.contains("123"));
        assertTrue(str.contains("Иван"));
    }

    @Test
    void testStatusTransition() {
        Order order = new Order();
        assertEquals(OrderStatus.PENDING, order.getStatus());

        order.setStatus(OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());

        order.setStatus(OrderStatus.COMPLETED);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    void testOrderImmutabilityAfterCreation() {
        Order order = new Order("123", "Customer", "Product", 5, true);

        assertEquals("123", order.getId());
        assertEquals("Customer", order.getCustomerName());
        assertEquals("Product", order.getProductDescription());
        assertEquals(5, order.getQuantity());
        assertTrue(order.isUrgent());
    }

    @Test
    void testOrderDateNotNull() {
        Order order = new Order();
        assertNotNull(order.getOrderDate());
    }

    @Test
    void testOrderDateIsRecent() {
        Order order = new Order();
        long now = System.currentTimeMillis();
        long orderTime = order.getOrderDate().toEpochSecond(java.time.ZoneOffset.UTC) * 1000;

        assertTrue(now - orderTime < 1000, "Order date should be within last second");
    }

    @Test
    void testToStringDoesNotThrow() {
        Order order = new Order("123", "Customer", "Product", 5, true);
        assertDoesNotThrow(order::toString);
        assertNotNull(order.toString());
    }

    @Test
    void testEqualsWithNull() {
        Order order1 = new Order("1", "Customer", "Product", 1, false);
        assertNotEquals(null, order1);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Order order1 = new Order("1", "Customer", "Product", 1, false);
        assertNotEquals("some string", order1);
    }

    @Test
    void testHashCodeConsistency() {
        Order order1 = new Order("1", "Customer", "Product", 1, false);
        Order order2 = new Order("1", "Customer", "Product", 1, false);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testConstructorWithNullId() {
        Order order = new Order(null, "Customer", "Product", 1, false);
        assertNotNull(order.getId());
    }

    @Test
    void testEqualsWithSameIdDifferentObjects() {
        Order order1 = new Order("same", "Customer1", "Product1", 1, false);
        Order order2 = new Order("same", "Customer2", "Product2", 2, true);
        assertEquals(order1, order2);
    }

    @Test
    void testHashCodeWithSameId() {
        Order order1 = new Order("same", "Customer1", "Product1", 1, false);
        Order order2 = new Order("same", "Customer2", "Product2", 2, true);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void testConstructorWithNullCustomerName() {
        Order order = new Order(null, "Product", 1, false);
        assertNull(order.getCustomerName());
    }

    @Test
    void testConstructorWithNullProductDescription() {
        Order order = new Order("Customer", null, 1, false);
        assertNull(order.getProductDescription());
    }

}