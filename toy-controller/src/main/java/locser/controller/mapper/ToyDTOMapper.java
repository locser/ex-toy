package locser.controller.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import locser.controller.dto.toy.CreateToyRequestDTO;
import locser.controller.dto.toy.ToyResponseDTO;
import locser.controller.dto.toy.UpdateToyRequestDTO;
import locser.toy.domain.model.dto.CreateToyRequest;
import locser.toy.domain.model.dto.ToyDTO;
import locser.toy.domain.model.dto.UpdateToyRequest;
import locser.util.PageResponse;
import locser.util.PageResponseDTO;

/**
 * Mapper class to convert between domain DTOs and controller DTOs.
 */
public class ToyDTOMapper {

  /**
   * Convert from controller DTO to domain DTO for create request.
   *
   * @param dto Controller DTO
   * @return Domain DTO
   */
  public static CreateToyRequest toCreateToyRequest(CreateToyRequestDTO dto) {
    if (dto == null) {
      return null;
    }

    return CreateToyRequest.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .category(dto.getCategory())
        .condition(dto.getCondition())
        .campaignId(dto.getCampaignId())
        .desiredExchangeItems(dto.getDesiredExchangeItems())
        .build();
  }

  /**
   * Convert from controller DTO to domain DTO for update request.
   *
   * @param dto Controller DTO
   * @return Domain DTO
   */
  public static UpdateToyRequest toUpdateToyRequest(UpdateToyRequestDTO dto) {
    if (dto == null) {
      return null;
    }

    return UpdateToyRequest.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .category(dto.getCategory())
        .condition(dto.getCondition())
        .status(dto.getStatus())
        .campaignId(dto.getCampaignId())
        .desiredExchangeItems(dto.getDesiredExchangeItems())
        .build();
  }

  /**
   * Convert from domain DTO to controller DTO for response.
   *
   * @param dto Domain DTO
   * @return Controller DTO
   */
  public static ToyResponseDTO toToyResponseDTO(ToyDTO dto) {
    if (dto == null) {
      return null;
    }

    // Format for dates: DD/MM/YYYY HH:mm
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    ZoneId zoneId = ZoneId.systemDefault();

    ToyResponseDTO responseDTO = new ToyResponseDTO();
    responseDTO.setId(dto.getId());
    responseDTO.setUserId(dto.getUserId());
    responseDTO.setCampaignId(dto.getCampaignId());
    responseDTO.setName(dto.getName());
    responseDTO.setDescription(dto.getDescription());
    responseDTO.setCategory(dto.getCategory());
    responseDTO.setCondition(dto.getCondition());
    responseDTO.setStatus(dto.getStatus());
    responseDTO.setDesiredExchangeItems(dto.getDesiredExchangeItems());

    // Format dates according to API standards
    if (dto.getCreatedAt() != null) {
      LocalDateTime createdAt = LocalDateTime.ofInstant(dto.getCreatedAt(), zoneId);
      responseDTO.setCreatedAt(formatter.format(createdAt));
    }

    if (dto.getUpdatedAt() != null) {
      LocalDateTime updatedAt = LocalDateTime.ofInstant(dto.getUpdatedAt(), zoneId);
      responseDTO.setUpdatedAt(formatter.format(updatedAt));
    }

    return responseDTO;
  }

  /**
   * Convert list of domain DTOs to list of controller DTOs.
   *
   * @param dtos List of domain DTOs
   * @return List of controller DTOs
   */
  public static List<ToyResponseDTO> toToyResponseDTOs(List<ToyDTO> dtos) {
    if (dtos == null) {
      return null;
    }

    return dtos.stream()
        .map(ToyDTOMapper::toToyResponseDTO)
        .collect(Collectors.toList());
  }

  /**
   * Convert from domain PageResponse to controller PageResponseDTO.
   *
   * @param pageResponse Domain PageResponse
   * @return Controller PageResponseDTO
   */
  public static PageResponseDTO<ToyResponseDTO> toPageResponseDTO(
      PageResponse<ToyDTO> pageResponse) {
    if (pageResponse == null) {
      return null;
    }

    List<ToyResponseDTO> responseDTOs = toToyResponseDTOs(pageResponse.getList());

    return PageResponseDTO.<ToyResponseDTO>builder()
        .list(responseDTOs)
        .limit(pageResponse.getLimit())
        .totalRecords(pageResponse.getTotalRecords())
        .build();
  }
}