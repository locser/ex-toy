package locser.controller.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require specific permissions for controller methods.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * Required permission name(s).
     */
    String[] value();
    
    /**
     * Whether user needs ALL permissions (true) or ANY permission (false).
     * Default is false (ANY).
     */
    boolean requireAll() default false;
    
    /**
     * Resource type for ownership check.
     */
    String resourceType() default "";
    
    /**
     * Parameter name that contains resource ID for ownership check.
     */
    String resourceIdParam() default "";
    
    /**
     * Allow resource owner even without explicit permission.
     */
    boolean allowOwner() default false;
}