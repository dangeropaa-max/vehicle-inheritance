package java1.model;

public enum OrderStatus {
    PENDING("Ожидает обработки"),
    PROCESSING("В обработке"),
    COMPLETED("Завершен"),
    FAILED("Ошибка обработки"),
    CANCELLED("Отменен");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}