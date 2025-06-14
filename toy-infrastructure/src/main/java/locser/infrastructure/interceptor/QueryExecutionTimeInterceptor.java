package locser.infrastructure.interceptor;

import org.hibernate.EmptyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QueryExecutionTimeInterceptor extends EmptyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutionTimeInterceptor.class);
    private static final long SLOW_QUERY_THRESHOLD = 500; // 500ms
    private final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();

    public String onPrepareStatement(String sql) {
        long startTime = System.currentTimeMillis();
        startTimeHolder.set(startTime);

        // Format SQL for better readability
        String formattedSql = sql.replaceAll("\\s+", " ")
                .replaceAll("(?i)select", "\nSELECT")
                .replaceAll("(?i)from", "\nFROM")
                .replaceAll("(?i)where", "\nWHERE")
                .replaceAll("(?i)join", "\nJOIN")
                .replaceAll("(?i)group by", "\nGROUP BY")
                .replaceAll("(?i)order by", "\nORDER BY")
                .replaceAll("(?i)having", "\nHAVING");

        logger.debug("Executing SQL:\n{}", formattedSql);
        return sql;
    }

    @Override
    public void afterTransactionCompletion(org.hibernate.Transaction tx) {
        long endTime = System.currentTimeMillis();
        Long startTime = startTimeHolder.get();

        if (startTime != null) {
            long executionTime = endTime - startTime;
            if (executionTime > SLOW_QUERY_THRESHOLD) {
                logger.warn("Slow query detected! Execution time: {}ms", executionTime);
            } else {
                logger.debug("Query execution time: {}ms", executionTime);
            }
            startTimeHolder.remove();
        }
    }
}