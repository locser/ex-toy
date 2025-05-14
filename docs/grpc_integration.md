# gRPC Integration Guide

## Overview

This document provides information on how to use and configure the gRPC integration in the Toy Exchange application. The gRPC integration enables high-performance, language-agnostic communication between services.

## Features

- High-performance binary serialization with Protocol Buffers
- Bidirectional streaming capabilities
- Strong typing with code generation
- Profile-based activation
- Client and server implementations

## Configuration

### Dependencies

The gRPC integration relies on the following dependencies:

```xml
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>${grpc.version}</version>
</dependency>
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>${protobuf.version}</version>
</dependency>
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>
```

### Plugin Configuration

The gRPC code generation requires the following Maven plugin:

```xml
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.6.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${protoc.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
        <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Application Properties

Add the following properties to your `application.yml` or `application.properties` file:

```yaml
grpc:
  server:
    port: 9090
    host: localhost
```

### Enabling gRPC

The gRPC integration is disabled by default. To enable it, you need to activate the `grpc` profile for the server and `grpc-client` for the client:

```bash
# When running the server
java -jar toy-starter.jar --spring.profiles.active=grpc

# When running the client
java -jar toy-starter.jar --spring.profiles.active=grpc-client

# Or in application.yml
spring:
  profiles:
    active: grpc,grpc-client
```

## Usage

### Defining Services

Services are defined in Protocol Buffer (`.proto`) files. For example:

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "locser.infrastructure.grpc";
option java_outer_classname = "ToyServiceProto";

package toy;

service ToyService {
  rpc GetToy (ToyRequest) returns (ToyResponse) {}
  rpc CreateToy (CreateToyRequest) returns (ToyResponse) {}
  // More methods...
}

// Message definitions...
```

### Implementing a gRPC Service

To implement a gRPC service, extend the generated service base class:

```java
@Service
@Profile("grpc")
public class ToyGrpcService extends ToyServiceGrpc.ToyServiceImplBase {
    private final ToyApplicationService toyApplicationService;

    public ToyGrpcService(ToyApplicationService toyApplicationService) {
        this.toyApplicationService = toyApplicationService;
    }

    @Override
    public void getToy(ToyRequest request, StreamObserver<ToyResponse> responseObserver) {
        // Implementation...
    }

    // More method implementations...
}
```

### Using the gRPC Client

To use the gRPC client, inject the `ToyGrpcClient` into your service:

```java
@Service
public class YourService {
    private final ToyGrpcClient toyGrpcClient;

    public YourService(ToyGrpcClient toyGrpcClient) {
        this.toyGrpcClient = toyGrpcClient;
    }

    public void doSomething(long toyId) {
        ToyResponse toy = toyGrpcClient.getToy(toyId);
        // Process the response...
    }
}
```

## Best Practices

1. **Service Definition**: Keep service definitions clean and focused on a single domain.
2. **Error Handling**: Use proper error handling with status codes and error messages.
3. **Versioning**: Version your API by including version information in the package name.
4. **Security**: Use TLS for secure communication in production.
5. **Timeouts**: Configure appropriate timeouts for gRPC calls.

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure the gRPC server is running and accessible at the configured host and port.
2. **Service Not Found**: Verify that the service is properly registered with the gRPC server.
3. **Serialization Errors**: Check that the message definitions match between client and server.

### Debugging

Enable debug logging for gRPC:

```yaml
logging:
  level:
    io.grpc: DEBUG
```

## Testing gRPC Services

For testing gRPC services, you can use the in-process server:

```java
@Test
public void testToyService() {
    // Create an in-process server
    String serverName = InProcessServerBuilder.generateName();
    grpcCleanup.register(InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(new ToyGrpcService(toyApplicationService))
        .build()
        .start());

    // Create a client
    ToyServiceGrpc.ToyServiceBlockingStub blockingStub = ToyServiceGrpc.newBlockingStub(
        grpcCleanup.register(InProcessChannelBuilder
            .forName(serverName)
            .directExecutor()
            .build()));

    // Test the service
    ToyResponse response = blockingStub.getToy(ToyRequest.newBuilder().setId(1L).build());
    assertEquals(1L, response.getId());
}
```

## References

- [gRPC Documentation](https://grpc.io/docs/)
- [Protocol Buffers Documentation](https://developers.google.com/protocol-buffers/docs/overview)
- [gRPC-Java GitHub Repository](https://github.com/grpc/grpc-java)
