package locser.toy.domain.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator cho annotation ValidId.
 */
public class ValidIdValidator implements ConstraintValidator<ValidId, Long> {
    
    private String entity;
    
    @Override
    public void initialize(ValidId constraintAnnotation) {
        this.entity = constraintAnnotation.entity();
    }
    
    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        if (id == null) {
            return false;
        }
        
        if (id <= 0) {
            // Tùy chỉnh thông báo lỗi
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(entity + " ID phải lớn hơn 0")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
