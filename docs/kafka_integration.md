# Kafka Integration Guide

## Overview

This document provides information on how to use and configure the Kafka integration in the Toy Exchange application. The Kafka integration is designed to handle event-driven communication between different parts of the application and potentially with external systems.

## Features

- Event publishing for toy-related events (creation, updates, status changes, etc.)
- Event consumption and processing
- Configurable topics and consumer groups
- Profile-based activation

## Configuration

### Dependencies

The Kafka integration relies on the following dependencies:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### Application Properties

Add the following properties to your `application.yml` or `application.properties` file:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: toy-exchange
      auto-offset-reset: earliest
    topics:
      toy-events: toy-events
      exchange-events: exchange-events
```

### Enabling Kafka

The Kafka integration is disabled by default. To enable it, you need to activate the `kafka` profile:

```bash
# When running the application
java -jar toy-starter.jar --spring.profiles.active=kafka

# Or in application.yml
spring:
  profiles:
    active: kafka
```

## Usage

### Publishing Events

To publish events to Kafka, inject the `ToyEventProducer` into your service:

```java
@Service
public class YourService {
    private final ToyEventProducer toyEventProducer;

    public YourService(ToyEventProducer toyEventProducer) {
        this.toyEventProducer = toyEventProducer;
    }

    public void createToy(Toy toy) {
        // Business logic...
        
        // Publish event
        toyEventProducer.publishToyCreatedEvent(
            toy.getId(),
            toy.getUserId(),
            toy.getName()
        );
    }
}
```

### Consuming Events

Events are automatically consumed by the `ToyEventConsumer` when the Kafka profile is active. The consumer listens to the configured topics and processes events based on their type.

To add custom event processing logic, modify the `ToyEventConsumer` class or create a new consumer class with the `@KafkaListener` annotation:

```java
@Component
@Profile("kafka")
public class CustomEventConsumer {
    @KafkaListener(topics = "${spring.kafka.topics.custom-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCustomEvent(CustomEvent event) {
        // Process the event
    }
}
```

## Event Types

The following event types are supported:

- `CREATED`: A toy has been created
- `UPDATED`: A toy has been updated
- `DELETED`: A toy has been deleted
- `STATUS_CHANGED`: A toy's status has changed
- `ADDED_TO_CAMPAIGN`: A toy has been added to a campaign
- `REMOVED_FROM_CAMPAIGN`: A toy has been removed from a campaign

## Best Practices

1. **Error Handling**: Always handle exceptions in event consumers to prevent message processing failures.
2. **Idempotency**: Design event handlers to be idempotent, as messages might be delivered more than once.
3. **Monitoring**: Set up monitoring for Kafka topics and consumers to track message processing.
4. **Testing**: Test event producers and consumers with embedded Kafka for integration testing.

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure Kafka is running and accessible at the configured bootstrap servers.
2. **Serialization Errors**: Check that the event classes are properly serializable.
3. **Consumer Not Processing**: Verify that the consumer group ID is correct and the consumer is subscribed to the right topic.

### Debugging

Enable debug logging for Kafka:

```yaml
logging:
  level:
    org.apache.kafka: DEBUG
    org.springframework.kafka: DEBUG
```

## Setting Up Kafka Locally

1. Download Kafka from [Apache Kafka website](https://kafka.apache.org/downloads)
2. Extract the downloaded file
3. Start ZooKeeper:
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```
4. Start Kafka server:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```
5. Create the required topics:
   ```bash
   bin/kafka-topics.sh --create --topic toy-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   bin/kafka-topics.sh --create --topic exchange-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```

## References

- [Spring for Apache Kafka Documentation](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
