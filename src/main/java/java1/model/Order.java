package java1.model;

import java1.annotations.OrderType;
import java1.annotations.Validate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Order {
    @Validate(notNull = true, message = "Идентификатор заказа не может быть пустым")
    private final String id;

    @Validate(notNull = true, notEmpty = true, message = "Имя клиента не может быть пустым")
    private final String customerName;

    @Validate(notNull = true, notEmpty = true, message = "Описание продукта не может быть пустым")
    private final String productDescription;

    private final int quantity;

    @Validate(notNull = true, message = "Дата заказа не может быть нулевой")
    private final LocalDateTime orderDate;

    @OrderType(urgent = false)
    private final boolean urgent;

    private OrderStatus status;

    public Order(String id, String customerName, String productDescription, int quantity, boolean urgent) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.customerName = customerName;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.orderDate = LocalDateTime.now();
        this.urgent = urgent;
        this.status = OrderStatus.PENDING;
    }

    public Order(String customerName, String productDescription, int quantity, boolean urgent) {
        this(null, customerName, productDescription, quantity, urgent);
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Заказ{id='%s', потребитель='%s', продукт='%s', количество=%d, срочный=%s, статус=%s}",
                id, customerName, productDescription, quantity, urgent, status);
    }
}