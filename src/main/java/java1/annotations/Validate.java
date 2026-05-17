package java1.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validate {
    boolean notNull() default false;
    boolean notEmpty() default false;
    String regex() default "";
    String message() default "Ошибка валидации";
}