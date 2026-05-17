package java1.model;

import java1.annotations.OrderType;
import java1.annotations.Validate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Order {
    @Validate(notNull = true, notEmpty = true, message = "Order ID cannot be null")
    private String id;

    @Validate(notNull = true, notEmpty = true, message = "Customer name cannot be empty")
    private String customerName;

    @Validate(notNull = true, notEmpty = true, message = "Product description cannot be empty")
    private String productDescription;

    private int quantity;

    @OrderType(urgent = false)
    private boolean urgent;

    private LocalDateTime orderDate;
    private OrderStatus status;

    public Order() {
        this.id = UUID.randomUUID().toString();
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(String id, String customerName, String productDescription, int quantity, boolean urgent) {
        this.id = (id != null && !id.trim().isEmpty()) ? id : UUID.randomUUID().toString();
        this.customerName = customerName;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.urgent = urgent;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(String customerName, String productDescription, int quantity, boolean urgent) {
        this(null, customerName, productDescription, quantity, urgent);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
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
        return "Заказ{" +
                "id='" + id + '\'' +
                ", клиент='" + customerName + '\'' +
                ", товар='" + productDescription + '\'' +
                ", количество=" + quantity +
                ", срочный=" + (urgent ? "да" : "нет") +
                ", статус=" + status.getDescription() +
                '}';
    }
}