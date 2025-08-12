package locser.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

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
     * Consumer factory configuration for receiving JSON messages.
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // Sử dụng ErrorHandlingDeserializer để xử lý lỗi deserialization
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
        
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
     * Kafka listener container factory for consuming messages with BATCH PROCESSING.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // Enable batch processing
        factory.setBatchListener(true);
        
        // Configure batch settings
        factory.getContainerProperties().setPollTimeout(3000); // 3 seconds timeout
        factory.setConcurrency(3); // Number of consumer threads
        
        return factory;
    }
}
