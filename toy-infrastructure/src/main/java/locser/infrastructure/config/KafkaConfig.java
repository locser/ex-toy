package locser.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;


/**
 * Configuration class for Kafka.
 * This class is only active when the "kafka" profile is active.
 */
@Configuration
@Profile("kafka")
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:toy-exchange}")
    private String groupId;

    @Value("${spring.kafka.topics.toy-events:toy-events}")
    private String toyEventsTopic;

    @Value("${spring.kafka.topics.exchange-events:exchange-events}")
    private String exchangeEventsTopic;

    // ## 2. Kafka Consumer Configuration tối ưu - Advanced Settings
    @Value("${kafka.consumer.fetch-min-size:1024}")
    private int fetchMinSize; // Minimum data size to fetch (1KB)

    @Value("${kafka.consumer.fetch-max-wait:500}")
    private int fetchMaxWait; // Maximum wait time for fetch (500ms)

    @Value("${kafka.consumer.max-poll-records:500}")
    private int maxPollRecords; // Maximum records per poll

    @Value("${kafka.consumer.session-timeout:30000}")
    private int sessionTimeout; // Session timeout (30s)

    @Value("${kafka.consumer.heartbeat-interval:10000}")
    private int heartbeatInterval; // Heartbeat interval (10s)

    @Value("${kafka.consumer.max-poll-interval:300000}")
    private int maxPollInterval; // Max poll interval (5 minutes)

    @Value("${kafka.consumer.concurrency:3}")
    private int consumerConcurrency; // Number of consumer threads

    @Value("${kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${kafka.consumer.enable-auto-commit:false}")
    private boolean enableAutoCommit; // Manual commit for better control


    /**
     * Kafka admin client configuration.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * Creates the toy events topic.
     */
    @Bean
    public NewTopic toyEventsTopic() {
        return new NewTopic(toyEventsTopic, 1, (short) 1);
    }

    /**
     * Creates the exchange events topic.
     */
    @Bean
    public NewTopic exchangeEventsTopic() {
        return new NewTopic(exchangeEventsTopic, 1, (short) 1);
    }

    /**
     * Producer factory configuration for sending JSON messages.
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Kafka template for sending messages.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * ## 2. Kafka Consumer Configuration tối ưu
     * Consumer factory configuration for receiving JSON messages with optimized
     * settings.
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        // Basic configuration
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // ## Performance Optimization Settings ##

        // 1. Fetch Configuration - Tối ưu network round trips
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinSize); // Min 1KB per fetch
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, fetchMaxWait); // Max wait 500ms
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords); // Max 500 records per poll

        // 2. Session Management - Tối ưu consumer group stability
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout); // 30s session timeout
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval); // 10s heartbeat
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval); // 5min max poll interval

        // 3. Offset Management - Manual commit for better control
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit); // Disable auto commit
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset); // Start from earliest

        // 4. Memory and Buffer Optimization
        props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 65536); // 64KB receive buffer
        props.put(ConsumerConfig.SEND_BUFFER_CONFIG, 131072); // 128KB send buffer

        // 5. Connection Optimization
        props.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 540000); // 9 minutes idle timeout
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 30s request timeout
        props.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, 100); // 100ms retry backoff

        // 6. Partition Assignment Strategy - Tối ưu load balancing
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                "org.apache.kafka.clients.consumer.CooperativeStickyAssignor"); // Cooperative rebalancing

        // Sử dụng ErrorHandlingDeserializer để xử lý lỗi deserialization
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");

        // Cấu hình delegate deserializers
        props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
        props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);

        // Cấu hình JSON deserializer
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "locser.toy.domain.model.*,locser.infrastructure.kafka.event.*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "locser.infrastructure.kafka.event.ToyEvent");
        props.put(JsonDeserializer.TYPE_MAPPINGS, "toyEvent:locser.infrastructure.kafka.event.ToyEvent");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * ## 1. Tối ưu Batch Size động + ## 2. Kafka Consumer Configuration tối ưu
     * Kafka listener container factory with dynamic batch processing and optimized
     * settings.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // ## 1. Dynamic Batch Processing Configuration ##
        factory.setBatchListener(true);

        // ## 2. Advanced Container Properties ##
        ContainerProperties containerProps = factory.getContainerProperties();

        // Polling and Timeout Configuration
        containerProps.setPollTimeout(3000); // 3 seconds poll timeout
        containerProps.setIdleBetweenPolls(100); // 100ms idle between polls

        // Manual Acknowledgment for better control
        containerProps.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Error Handling and Retry Configuration
        containerProps.setDeliveryAttemptHeader(true); // Track delivery attempts

        // Concurrency and Threading
        factory.setConcurrency(consumerConcurrency); // Configurable consumer threads

        // ## Advanced Performance Settings ##

        // 1. Consumer Thread Configuration

        // 2. Shutdown Configuration
        factory.getContainerProperties().setShutdownTimeout(10000); // 10s graceful shutdown

        // 3. Monitoring and Metrics
        factory.getContainerProperties().setMicrometerEnabled(true); // Enable Micrometer metrics

        // 4. Log Configuration
        factory.getContainerProperties().setLogContainerConfig(true); // Log container config

        // ## Kafka Listener Container Factory Ready ##

        return factory;
    }

    /**
     * Retry template for failed message processing
     */
    @Bean
    public RetryTemplate kafkaRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Retry Policy - Retry up to 3 times
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        // Backoff Policy - Fixed 1 second delay between retries
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000); // 1 second
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
