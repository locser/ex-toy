package locser.infrastructure.grpc.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the gRPC ToyService.
 * This service is only active when the "grpc" profile is active.
 *
 * NOTE: This is a placeholder class. The actual implementation will be
 *
 * available
 *
 * after compiling the proto files. This class should be replaced with the
 *
 * proper
 * implementation that extends the generated ToyServiceGrpc.ToyServiceImplBase
 * class.
 */
@Service
@Profile("grpc")
@Slf4j
public class ToyGrpcService {

    /**
     * This is a placeholder method that will be called when the gRPC server starts.
     * It logs a message indicating that the service is not fully implemented yet.
     */
    public void initialize() {
        log.info("ToyGrpcService is a placeholder. Implement the actual service after compiling proto files.");
        log.info("To implement the service, extend ToyServiceGrpc.ToyServiceImplBase and override its methods.");
    }
}
