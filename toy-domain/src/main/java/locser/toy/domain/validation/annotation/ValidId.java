package locser.toy.domain.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Annotation để kiểm tra ID có hợp lệ không (phải lớn hơn 0).
 */
@Documented
@Constraint(validatedBy = ValidIdValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {
    String message() default "ID phải lớn hơn 0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String entity() default "Entity";
}
