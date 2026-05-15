package java1.validator;

import java1.annotations.Validate;
import java1.model.Order;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class OrderValidator {

    public List<String> validate(Order order) {
        List<String> errors = new ArrayList<>();

        if (order == null) {
            errors.add("Order cannot be null");
            return errors;
        }

        Field[] fields = Order.class.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Validate.class)) {
                Validate validate = field.getAnnotation(Validate.class);
                field.setAccessible(true);

                try {
                    Object value = field.get(order);

                    if (validate.notNull() && value == null) {
                        errors.add(validate.message() + " (field: " + field.getName() + ")");
                        continue;
                    }

                    if (validate.notEmpty() && value instanceof String && ((String) value).trim().isEmpty()) {
                        errors.add(validate.message() + " (field: " + field.getName() + ")");
                    }

                    if (!validate.regex().isEmpty() && value instanceof String) {
                        Pattern pattern = Pattern.compile(validate.regex());
                        if (!pattern.matcher((String) value).matches()) {
                            errors.add(validate.message() + " (field: " + field.getName() + ")");
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Failed to validate field: " + field.getName());
                }
            }
        }

        return errors;
    }

    public boolean isValid(Order order) {
        return validate(order).isEmpty();
    }
}