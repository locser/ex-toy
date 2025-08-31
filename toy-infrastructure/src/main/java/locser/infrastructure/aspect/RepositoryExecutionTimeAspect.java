package locser.infrastructure.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryExecutionTimeAspect {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryExecutionTimeAspect.class);
    private static final long SLOW_QUERY_THRESHOLD = 1000; // 1 second

    @Around("execution(* locser.persistence.mapper.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            if (executionTime > SLOW_QUERY_THRESHOLD) {
                logger.warn("Slow database operation detected! {}.{} took {}ms",
                        className, methodName, executionTime);
                // Consider removing System.exit(1) in production - it's too aggressive
                // System.exit(1);
            } else {
                logger.debug("Database operation {}.{} took {}ms",
                        className, methodName, executionTime);
            }
        }
    }
}