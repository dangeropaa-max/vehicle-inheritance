package java1.model;

public enum OrderStatus {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}