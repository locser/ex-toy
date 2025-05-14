package locser.infrastructure.grpc.adapter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import locser.infrastructure.grpc.temp.CreateToyRequest;
import locser.infrastructure.grpc.temp.ToyResponse;
import locser.infrastructure.grpc.temp.UpdateToyRequest;
import locser.toy.domain.model.dto.ToyDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * Adapter for converting between gRPC DTOs and domain DTOs.
 * This follows the Adapter pattern to isolate the domain model from the gRPC implementation.
 */
@Component
@Slf4j
public class GrpcToyAdapter {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    
    /**
     * Converts a gRPC ToyResponse to a domain ToyDTO.
     *
     * @param grpcResponse The gRPC ToyResponse
     * @return The domain ToyDTO
     */
    public ToyDTO toDomainDto(ToyResponse grpcResponse) {
        if (grpcResponse == null) {
            return null;
        }
        
        ToyDTO dto = new ToyDTO();
        dto.setId(grpcResponse.getId());
        dto.setUserId(grpcResponse.getUserId());
        dto.setCampaignId(grpcResponse.getCampaignId() > 0 ? grpcResponse.getCampaignId() : null);
        dto.setName(grpcResponse.getName());
        dto.setDescription(grpcResponse.getDescription());
        dto.setCategory(grpcResponse.getCategory());
        dto.setCondition(grpcResponse.getCondition());
        dto.setStatus(grpcResponse.getStatus());
        dto.setDesiredExchangeItems(grpcResponse.getDesiredExchangeItems());
        
        // Parse dates if they are not empty
        if (grpcResponse.getCreatedAt() != null && !grpcResponse.getCreatedAt().isEmpty()) {
            try {
                dto.setCreatedAt(Instant.from(ISO_FORMATTER.parse(grpcResponse.getCreatedAt())));
            } catch (DateTimeParseException e) {
                log.warn("Failed to parse createdAt date: {}", grpcResponse.getCreatedAt(), e);
            }
        }
        
        if (grpcResponse.getUpdatedAt() != null && !grpcResponse.getUpdatedAt().isEmpty()) {
            try {
                dto.setUpdatedAt(Instant.from(ISO_FORMATTER.parse(grpcResponse.getUpdatedAt())));
            } catch (DateTimeParseException e) {
                log.warn("Failed to parse updatedAt date: {}", grpcResponse.getUpdatedAt(), e);
            }
        }
        
        return dto;
    }
    
    /**
     * Converts a domain ToyDTO to a gRPC CreateToyRequest.Builder.
     *
     * @param domainDto The domain ToyDTO
     * @return The gRPC CreateToyRequest.Builder
     */
    public CreateToyRequest.Builder toGrpcCreateRequestBuilder(ToyDTO domainDto) {
        if (domainDto == null) {
            return null;
        }
        
        CreateToyRequest.Builder builder = CreateToyRequest.newBuilder()
                .setUserId(domainDto.getUserId() != null ? domainDto.getUserId() : 0L)
                .setName(domainDto.getName() != null ? domainDto.getName() : "")
                .setDescription(domainDto.getDescription() != null ? domainDto.getDescription() : "")
                .setCategory(domainDto.getCategory() != null ? domainDto.getCategory() : "")
                .setCondition(domainDto.getCondition() != null ? domainDto.getCondition() : 0);
        
        if (domainDto.getCampaignId() != null && domainDto.getCampaignId() > 0) {
            builder.setCampaignId(domainDto.getCampaignId());
        }
        
        if (domainDto.getDesiredExchangeItems() != null) {
            builder.setDesiredExchangeItems(domainDto.getDesiredExchangeItems());
        }
        
        return builder;
    }
    
    /**
     * Converts a domain ToyDTO to a gRPC UpdateToyRequest.Builder.
     *
     * @param domainDto The domain ToyDTO
     * @return The gRPC UpdateToyRequest.Builder
     */
    public UpdateToyRequest.Builder toGrpcUpdateRequestBuilder(ToyDTO domainDto) {
        if (domainDto == null) {
            return null;
        }
        
        UpdateToyRequest.Builder builder = UpdateToyRequest.newBuilder()
                .setId(domainDto.getId() != null ? domainDto.getId() : 0L)
                .setName(domainDto.getName() != null ? domainDto.getName() : "")
                .setDescription(domainDto.getDescription() != null ? domainDto.getDescription() : "")
                .setCategory(domainDto.getCategory() != null ? domainDto.getCategory() : "")
                .setCondition(domainDto.getCondition() != null ? domainDto.getCondition() : 0)
                .setStatus(domainDto.getStatus() != null ? domainDto.getStatus() : 0);
        
        if (domainDto.getCampaignId() != null && domainDto.getCampaignId() > 0) {
            builder.setCampaignId(domainDto.getCampaignId());
        }
        
        if (domainDto.getDesiredExchangeItems() != null) {
            builder.setDesiredExchangeItems(domainDto.getDesiredExchangeItems());
        }
        
        return builder;
    }
    
    /**
     * Converts a domain CreateToyRequest to a gRPC CreateToyRequest.Builder.
     *
     * @param domainRequest The domain CreateToyRequest
     * @return The gRPC CreateToyRequest.Builder
     */
    public CreateToyRequest.Builder toGrpcCreateRequestBuilder(locser.toy.domain.model.dto.CreateToyRequest domainRequest) {
        if (domainRequest == null) {
            return null;
        }
        
        CreateToyRequest.Builder builder = CreateToyRequest.newBuilder()
                .setName(domainRequest.getName() != null ? domainRequest.getName() : "")
                .setDescription(domainRequest.getDescription() != null ? domainRequest.getDescription() : "")
                .setCategory(domainRequest.getCategory() != null ? domainRequest.getCategory() : "")
                .setCondition(domainRequest.getCondition() != null ? domainRequest.getCondition() : 0);
        
        if (domainRequest.getCampaignId() != null && domainRequest.getCampaignId() > 0) {
            builder.setCampaignId(domainRequest.getCampaignId());
        }
        
        if (domainRequest.getDesiredExchangeItems() != null) {
            builder.setDesiredExchangeItems(domainRequest.getDesiredExchangeItems());
        }
        
        return builder;
    }
    
    /**
     * Converts a domain UpdateToyRequest to a gRPC UpdateToyRequest.Builder.
     *
     * @param domainRequest The domain UpdateToyRequest
     * @param toyId         The ID of the toy
     * @return The gRPC UpdateToyRequest.Builder
     */
    public UpdateToyRequest.Builder toGrpcUpdateRequestBuilder(locser.toy.domain.model.dto.UpdateToyRequest domainRequest, Long toyId) {
        if (domainRequest == null) {
            return null;
        }
        
        UpdateToyRequest.Builder builder = UpdateToyRequest.newBuilder()
                .setId(toyId != null ? toyId : 0L);
        
        if (domainRequest.getName() != null) {
            builder.setName(domainRequest.getName());
        }
        
        if (domainRequest.getDescription() != null) {
            builder.setDescription(domainRequest.getDescription());
        }
        
        if (domainRequest.getCategory() != null) {
            builder.setCategory(domainRequest.getCategory());
        }
        
        if (domainRequest.getCondition() != null) {
            builder.setCondition(domainRequest.getCondition());
        }
        
        if (domainRequest.getStatus() != null) {
            builder.setStatus(domainRequest.getStatus());
        }
        
        if (domainRequest.getCampaignId() != null) {
            builder.setCampaignId(domainRequest.getCampaignId());
        }
        
        if (domainRequest.getDesiredExchangeItems() != null) {
            builder.setDesiredExchangeItems(domainRequest.getDesiredExchangeItems());
        }
        
        return builder;
    }
}
