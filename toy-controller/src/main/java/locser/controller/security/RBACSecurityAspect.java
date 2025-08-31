package locser.controller.security;

import locser.toy.domain.service.RBACService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Aspect for handling RBAC security annotations.
 */
@Aspect
@Component
@Order(1)
public class RBACSecurityAspect {
    
    private final RBACService rbacService;
    
    public RBACSecurityAspect(RBACService rbacService) {
        this.rbacService = rbacService;
    }
    
    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        
        // Get current user ID from request or security context
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new SecurityException("User not authenticated");
        }
        
        // Check permissions
        String[] requiredPermissions = requirePermission.value();
        boolean hasPermission;
        
        if (requirePermission.requireAll()) {
            hasPermission = rbacService.hasAllPermissions(userId, requiredPermissions);
        } else {
            hasPermission = rbacService.hasAnyPermission(userId, requiredPermissions);
        }
        
        // Check ownership if allowed
        if (!hasPermission && requirePermission.allowOwner()) {
            hasPermission = checkOwnership(joinPoint, requirePermission, userId);
        }
        
        if (!hasPermission) {
            throw new SecurityException("Access denied. Required permissions: " + String.join(", ", requiredPermissions));
        }
        
        return joinPoint.proceed();
    }
    
    private boolean checkOwnership(ProceedingJoinPoint joinPoint, RequirePermission requirePermission, Long userId) {
        if (requirePermission.resourceType().isEmpty() || requirePermission.resourceIdParam().isEmpty()) {
            return false;
        }
        
        try {
            Long resourceId = getResourceIdFromParams(joinPoint, requirePermission.resourceIdParam());
            if (resourceId != null) {
                return rbacService.isResourceOwner(userId, requirePermission.resourceType(), resourceId);
            }
        } catch (Exception e) {
            // Log error but don't fail the ownership check
            System.err.println("Error checking ownership: " + e.getMessage());
        }
        
        return false;
    }
    
    private Long getResourceIdFromParams(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                Object value = args[i];
                if (value instanceof Long) {
                    return (Long) value;
                } else if (value instanceof String) {
                    try {
                        return Long.parseLong((String) value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        
        return null;
    }
    
    private Long getCurrentUserId() {
        // Try to get from request parameter first
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // Check for userId in request parameters
            String userIdParam = request.getParameter("userId");
            if (userIdParam != null) {
                try {
                    return Long.parseLong(userIdParam);
                } catch (NumberFormatException e) {
                    // Ignore and try other methods
                }
            }
            
            // Check for userId in headers
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null) {
                try {
                    return Long.parseLong(userIdHeader);
                } catch (NumberFormatException e) {
                    // Ignore and try other methods
                }
            }
        }
        
        // TODO: Integrate with Spring Security when authentication is implemented
        // For now, return a default user ID for testing
        return 1L; // This should be replaced with actual authentication
    }
}