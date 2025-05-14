package locser.controller.adapter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import locser.controller.dto.toy.CreateToyRequestDTO;
import locser.controller.dto.toy.ToyResponseDTO;
import locser.controller.dto.toy.UpdateToyRequestDTO;
import locser.toy.domain.model.dto.CreateToyRequest;
import locser.toy.domain.model.dto.ToyDTO;
import locser.toy.domain.model.dto.UpdateToyRequest;

/**
 * Adapter for converting between REST DTOs and domain DTOs.
 * This follows the Adapter pattern to isolate the domain model from the REST
 * implementation.
 */
@Component
public class RestToyAdapter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    /**
     * Converts a domain ToyDTO to a REST ToyResponseDTO.
     *
     * @param domainDto The domain ToyDTO
     * @return The REST ToyResponseDTO
     */
    public ToyResponseDTO toResponseDto(ToyDTO domainDto) {
        if (domainDto == null) {
            return null;
        }

        ToyResponseDTO dto = new ToyResponseDTO();
        dto.setId(domainDto.getId());
        dto.setUserId(domainDto.getUserId());
        dto.setCampaignId(domainDto.getCampaignId());
        dto.setName(domainDto.getName());
        dto.setDescription(domainDto.getDescription());
        dto.setCategory(domainDto.getCategory());
        dto.setCondition(domainDto.getCondition());
        dto.setStatus(domainDto.getStatus());
        dto.setDesiredExchangeItems(domainDto.getDesiredExchangeItems());

        // Format dates according to API standards
        if (domainDto.getCreatedAt() != null) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(domainDto.getCreatedAt(), ZONE_ID);
            dto.setCreatedAt(DATE_FORMATTER.format(createdAt));
        }

        if (domainDto.getUpdatedAt() != null) {
            LocalDateTime updatedAt = LocalDateTime.ofInstant(domainDto.getUpdatedAt(), ZONE_ID);
            dto.setUpdatedAt(DATE_FORMATTER.format(updatedAt));
        }

        return dto;
    }

    /**
     * Converts a REST CreateToyRequestDTO to a domain CreateToyRequest.
     *
     * @param requestDto The REST CreateToyRequestDTO
     * @return The domain CreateToyRequest
     */
    public CreateToyRequest toDomainCreateRequest(CreateToyRequestDTO requestDto) {
        if (requestDto == null) {
            return null;
        }

        return CreateToyRequest.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .category(requestDto.getCategory())
                .condition(requestDto.getCondition())
                .campaignId(requestDto.getCampaignId())
                .desiredExchangeItems(requestDto.getDesiredExchangeItems())
                .build();
    }

    /**
     * Converts a REST UpdateToyRequestDTO to a domain UpdateToyRequest.
     *
     * @param requestDto The REST UpdateToyRequestDTO
     * @return The domain UpdateToyRequest
     */
    public UpdateToyRequest toDomainUpdateRequest(UpdateToyRequestDTO requestDto) {
        if (requestDto == null) {
            return null;
        }

        return UpdateToyRequest.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .category(requestDto.getCategory())
                .condition(requestDto.getCondition())
                .status(requestDto.getStatus())
                .campaignId(requestDto.getCampaignId())
                .desiredExchangeItems(requestDto.getDesiredExchangeItems())
                .build();
    }

    /**
     * Converts a domain ToyDTO to a domain CreateToyRequest.
     * This is useful when you need to create a new toy based on an existing one.
     *
     * @param domainDto The domain ToyDTO
     * @return The domain CreateToyRequest
     */
    public CreateToyRequest toDomainCreateRequest(ToyDTO domainDto) {
        if (domainDto == null) {
            return null;
        }

        return CreateToyRequest.builder()
                .name(domainDto.getName())
                .description(domainDto.getDescription())
                .category(domainDto.getCategory())
                .condition(domainDto.getCondition())
                .campaignId(domainDto.getCampaignId())
                .desiredExchangeItems(domainDto.getDesiredExchangeItems())
                .build();
    }

    /**
     * Converts a domain ToyDTO to a domain UpdateToyRequest.
     * This is useful when you need to update a toy based on an existing one.
     *
     * @param domainDto The domain ToyDTO
     * @return The domain UpdateToyRequest
     */
    public UpdateToyRequest toDomainUpdateRequest(ToyDTO domainDto) {
        if (domainDto == null) {
            return null;
        }

        return UpdateToyRequest.builder()
                .name(domainDto.getName())
                .description(domainDto.getDescription())
                .category(domainDto.getCategory())
                .condition(domainDto.getCondition())
                .status(domainDto.getStatus())
                .campaignId(domainDto.getCampaignId())
                .desiredExchangeItems(domainDto.getDesiredExchangeItems())
                .build();
    }
}
