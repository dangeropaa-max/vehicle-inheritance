package java1.validator;

import java1.annotations.Validate;
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
            if (field.isAnnotationPresent(Validate.class)) {
                Validate validation = field.getAnnotation(Validate.class);
                field.setAccessible(true);

                try {
                    Object value = field.get(order);

                    if (validation.notNull() && value == null) {
                        errors.add(validation.message());
                        continue;
                    }

                    if (validation.notEmpty() && value instanceof String) {
                        String strValue = (String) value;
                        if (strValue == null || strValue.trim().isEmpty()) {
                            errors.add(validation.message());
                        }
                    }

                    if (!validation.regex().isEmpty() && value instanceof String) {
                        String strValue = (String) value;
                        if (!strValue.matches(validation.regex())) {
                            errors.add(validation.message());
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Ошибка доступа к полю: " + field.getName());
                }
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
}