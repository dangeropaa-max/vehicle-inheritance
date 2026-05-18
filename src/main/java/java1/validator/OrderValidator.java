package java1.validator;

import java1.annotations.NotNull;
import java1.annotations.NotEmpty;
import java1.annotations.OrderType;
import java1.model.Order;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OrderValidator {

    public List<String> validate(Order order) {
        List<String> errors = new ArrayList<>();

        if (order == null) {
            errors.add("Заказ не может быть пустым");
            return errors;
        }

        if (order.getQuantity() <= 0) {
            errors.add("Количество товара должно быть больше нуля");
        }

        Field[] fields = Order.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(order);

                if (field.isAnnotationPresent(NotNull.class) && value == null) {
                    NotNull notNull = field.getAnnotation(NotNull.class);
                    errors.add(notNull.message());
                    continue;
                }

                if (field.isAnnotationPresent(NotEmpty.class) && value instanceof String) {
                    String strValue = (String) value;
                    if (strValue == null || strValue.trim().isEmpty()) {
                        NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
                        errors.add(notEmpty.message());
                    }
                }

                if (field.isAnnotationPresent(OrderType.class)) {
                    OrderType orderType = field.getAnnotation(OrderType.class);
                    boolean urgent = orderType.urgent();
                    if (urgent && (value == null || !(Boolean) value)) {
                        errors.add("Отсутствует флаг срочного заказа");
                    }
                }

            } catch (IllegalAccessException e) {
                errors.add("Ошибка доступа к полю: " + field.getName());
            }
        }

        return errors;
    }

    public boolean isValid(Order order) {
        if (order == null) {
            return false;
        }
        return validate(order).isEmpty();
    }

    public boolean isUrgentOrder(Order order) {
        if (order == null) {
            return false;
        }
        return order.isUrgent();
    }
}