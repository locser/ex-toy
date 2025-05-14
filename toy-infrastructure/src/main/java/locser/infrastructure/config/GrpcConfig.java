package locser.infrastructure.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import locser.infrastructure.grpc.service.ToyGrpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for gRPC server.
 * This class is only active when the "grpc" profile is active.
 */
@Configuration
@Profile("grpc")
@Slf4j
public class GrpcConfig {

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    /**
     * Creates and configures the gRPC server.
     * The server will be started by the GrpcServerRunner bean.
     *
     * @param toyGrpcService The placeholder gRPC service
     * @return The configured gRPC server
     */
    @Bean
    public Server grpcServer(ToyGrpcService toyGrpcService) {
        // Initialize the placeholder service
        toyGrpcService.initialize();

        log.info("Creating gRPC server on port {} (placeholder implementation)", grpcPort);
        log.info("To enable full gRPC functionality:");
        log.info("1. Compile proto files using Maven: mvn clean compile");
        log.info("2. Implement proper service classes that extend the generated *Grpc.BaseImplBase classes");
        log.info("3. Register services with ServerBuilder.addService() before building the server");

        return ServerBuilder.forPort(grpcPort)
                // Services will be registered when they are implemented and compiled
                .build();
    }

    /**
     * Bean to manage the lifecycle of the gRPC server.
     * It will start the server when the application starts and
     * shut it down when the application stops.
     */
    @Bean
    public GrpcServerRunner grpcServerRunner(Server grpcServer) {
        return new GrpcServerRunner(grpcServer);
    }

    /**
     * Helper class to manage the gRPC server lifecycle.
     */
    public static class GrpcServerRunner {
        private final Server server;

        public GrpcServerRunner(Server server) {
            this.server = server;
        }

        /**
         * Starts the gRPC server.
         */
        public void start() throws IOException {
            server.start();
            log.info("gRPC Server started, listening on port {}", server.getPort());

            // Add shutdown hook to stop the server when the JVM is shutting down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down gRPC server...");
                this.stop();
                log.info("gRPC server shut down successfully");
            }));
        }

        /**
         * Stops the gRPC server.
         */
        public void stop() {
            if (server != null) {
                server.shutdown();
            }
        }

        /**
         * Blocks until the server is shut down.
         */
        public void blockUntilShutdown() throws InterruptedException {
            if (server != null) {
                server.awaitTermination();
            }
        }
    }
}
