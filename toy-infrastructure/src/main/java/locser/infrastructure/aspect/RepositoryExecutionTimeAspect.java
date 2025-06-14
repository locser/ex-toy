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

    @Around("execution(* locser.toy.domain.repository.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            if (executionTime > SLOW_QUERY_THRESHOLD) {
                logger.warn("Slow repository method detected! {}.{} took {}ms",
                        className, methodName, executionTime);
            } else {
                logger.debug("Repository method {}.{} took {}ms",
                        className, methodName, executionTime);
            }
        }
    }
}