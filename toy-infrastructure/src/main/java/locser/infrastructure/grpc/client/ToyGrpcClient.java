package locser.infrastructure.grpc.client;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import locser.infrastructure.grpc.temp.CreateToyRequest;
import locser.infrastructure.grpc.temp.DeleteToyResponse;
import locser.infrastructure.grpc.temp.ListToysRequest;
import locser.infrastructure.grpc.temp.ListToysResponse;
import locser.infrastructure.grpc.temp.ToyRequest;
import locser.infrastructure.grpc.temp.ToyResponse;
import locser.infrastructure.grpc.temp.ToyServiceGrpc;
import locser.infrastructure.grpc.temp.UpdateToyRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Client for the gRPC ToyService.
 * This component is only active when the "grpc-client" profile is active.
 */
@Component
@Profile("grpc-client")
@Slf4j
public class ToyGrpcClient {

    @Value("${grpc.server.host:localhost}")
    private String grpcHost;

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private ManagedChannel channel;
    private ToyServiceGrpc.ToyServiceBlockingStub blockingStub;

    /**
     * Initializes the gRPC channel and stub.
     */
    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(grpcHost, grpcPort)
                .usePlaintext() // For development only, use TLS in production
                .build();

        blockingStub = ToyServiceGrpc.newBlockingStub(channel);

        log.info("gRPC client initialized for server at {}:{}", grpcHost, grpcPort);
    }

    /**
     * Shuts down the gRPC channel.
     */
    @PreDestroy
    public void shutdown() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            log.info("gRPC client shut down successfully");
        } catch (InterruptedException e) {
            log.error("Error shutting down gRPC client", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Gets a toy by ID.
     *
     * @param id The ID of the toy
     * @return The toy response
     */
    public ToyResponse getToy(long id) {
        log.info("Sending gRPC request to get toy with ID: {}", id);

        ToyRequest request = ToyRequest.newBuilder()
                .setId(id)
                .build();

        ToyResponse response = blockingStub.getToy(request);

        log.info("Received gRPC response for toy with ID: {}", id);

        return response;
    }

    /**
     * Creates a new toy.
     *
     * @param userId               The ID of the user creating the toy
     * @param name                 The name of the toy
     * @param description          The description of the toy
     * @param category             The category of the toy
     * @param condition            The condition of the toy
     * @param campaignId           The ID of the campaign (optional)
     * @param desiredExchangeItems The desired exchange items
     * @return The created toy response
     */
    public ToyResponse createToy(long userId, String name, String description, String category,
            int condition, long campaignId, String desiredExchangeItems) {
        log.info("Sending gRPC request to create toy for user ID: {}", userId);

        CreateToyRequest request = CreateToyRequest.newBuilder()
                .setUserId(userId)
                .setName(name)
                .setDescription(description != null ? description : "")
                .setCategory(category)
                .setCondition(condition)
                .setCampaignId(campaignId)
                .setDesiredExchangeItems(desiredExchangeItems != null ? desiredExchangeItems : "")
                .build();

        ToyResponse response = blockingStub.createToy(request);

        log.info("Received gRPC response for created toy with ID: {}", response.getId());

        return response;
    }

    /**
     * Updates an existing toy.
     *
     * @param id                   The ID of the toy to update
     * @param name                 The updated name of the toy
     * @param description          The updated description of the toy
     * @param category             The updated category of the toy
     * @param condition            The updated condition of the toy
     * @param status               The updated status of the toy
     * @param campaignId           The updated campaign ID
     * @param desiredExchangeItems The updated desired exchange items
     * @return The updated toy response
     */
    public ToyResponse updateToy(long id, String name, String description, String category,
            int condition, int status, long campaignId, String desiredExchangeItems) {
        log.info("Sending gRPC request to update toy with ID: {}", id);

        UpdateToyRequest request = UpdateToyRequest.newBuilder()
                .setId(id)
                .setName(name)
                .setDescription(description != null ? description : "")
                .setCategory(category)
                .setCondition(condition)
                .setStatus(status)
                .setCampaignId(campaignId)
                .setDesiredExchangeItems(desiredExchangeItems != null ? desiredExchangeItems : "")
                .build();

        ToyResponse response = blockingStub.updateToy(request);

        log.info("Received gRPC response for updated toy with ID: {}", id);

        return response;
    }

    /**
     * Deletes a toy.
     *
     * @param id The ID of the toy to delete
     * @return True if the deletion was successful, false otherwise
     */
    public boolean deleteToy(long id) {
        log.info("Sending gRPC request to delete toy with ID: {}", id);

        ToyRequest request = ToyRequest.newBuilder()
                .setId(id)
                .build();

        DeleteToyResponse response = blockingStub.deleteToy(request);

        log.info("Received gRPC response for delete toy with ID: {}, success: {}", id, response.getSuccess());

        return response.getSuccess();
    }

    /**
     * Lists toys with pagination.
     *
     * @param page          The page number (0-based)
     * @param limit         The page size
     * @param userId        The user ID filter (optional)
     * @param status        The status filter (optional)
     * @param campaignId    The campaign ID filter (optional)
     * @param sortBy        The field to sort by
     * @param sortDirection The sort direction (asc or desc)
     * @return The list of toys
     */
    public List<ToyResponse> listToys(int page, int limit, Long userId, Integer status,
            Long campaignId, String sortBy, String sortDirection) {
        log.info("Sending gRPC request to list toys with page: {}, limit: {}", page, limit);

        ListToysRequest.Builder requestBuilder = ListToysRequest.newBuilder()
                .setPage(page)
                .setLimit(limit)
                .setSortBy(sortBy != null ? sortBy : "id")
                .setSortDirection(sortDirection != null ? sortDirection : "desc");

        if (userId != null) {
            requestBuilder.setUserId(userId);
        }

        if (status != null) {
            requestBuilder.setStatus(status);
        }

        if (campaignId != null) {
            requestBuilder.setCampaignId(campaignId);
        }

        ListToysResponse response = blockingStub.listToys(requestBuilder.build());

        log.info("Received gRPC response for list toys, total: {}", response.getTotalRecord());

        return response.getToysList();
    }
}
