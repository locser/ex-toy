package locser.controller.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import locser.controller.dto.event.CreateEventRequestDTO;
import locser.controller.dto.event.EventResponseDTO;
import locser.controller.dto.event.UpdateEventRequestDTO;
import locser.toy.domain.model.dto.CreateEventRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.UpdateEventRequest;
import locser.util.PageResponse;
import locser.util.PageResponseDTO;

/**
 * Mapper class to convert between domain DTOs and controller DTOs.
 */
public class EventDTOMapper {

  /**
   * Convert from controller DTO to domain DTO for create request.
   *
   * @param dto Controller DTO
   * @return Domain DTO
   */
  public static CreateEventRequest toCreateEventRequest(CreateEventRequestDTO dto) {
    if (dto == null) {
      return null;
    }

    return CreateEventRequest.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .startDate(LocalDateTime.parse(dto.getStartDate() + " 00:00:00",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        .endDate(LocalDateTime.parse(dto.getEndDate() + " 23:59:59",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        .theme(dto.getTheme())
        .rules(dto.getRules())
        .build();
  }

  /**
   * Convert from controller DTO to domain DTO for update request.
   *
   * @param dto Controller DTO
   * @return Domain DTO
   */
  public static UpdateEventRequest toUpdateEventRequest(UpdateEventRequestDTO dto) {
    if (dto == null) {
      return null;
    }

    return UpdateEventRequest.builder()
        .name(dto.getName())
        .description(dto.getDescription())
        .startDate(LocalDateTime.parse(dto.getStartDate() + " 00:00:00",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        .endDate(LocalDateTime.parse(dto.getEndDate() + " 23:59:59",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        .theme(dto.getTheme())
        .rules(dto.getRules())
        .status(dto.getStatus())
        .build();
  }

  /**
   * Convert from domain DTO to controller DTO for response.
   *
   * @param dto Domain DTO
   * @return Controller DTO
   */
  public static EventResponseDTO toEventResponseDTO(EventDTO dto) {
    if (dto == null) {
      return null;
    }

    return EventResponseDTO.builder()
        .id(dto.getId())
        .name(dto.getName())
        .description(dto.getDescription())
        .startDate(LocalDateTime.parse(dto.getStartDate() + " 00:00:00",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        .endDate(LocalDateTime.parse(dto.getEndDate() + " 23:59:59",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
        // .startDate(dto.getStartDate())
        // .endDate(dto.getEndDate())
        .theme(dto.getTheme())
        .rules(dto.getRules())
        .status(dto.getStatus())
        .totalToys(dto.getTotalToys())
        .availableToys(dto.getAvailableToys())
        .createdAt(dto.getCreatedAt())
        .updatedAt(dto.getUpdatedAt())
        .build();
  }

  /**
   * Convert a list of domain DTOs to a list of controller DTOs.
   *
   * @param dtos List of domain DTOs
   * @return List of controller DTOs
   */
  public static List<EventResponseDTO> toEventResponseDTOs(List<EventDTO> dtos) {
    if (dtos == null) {
      return null;
    }

    return dtos.stream()
        .map(EventDTOMapper::toEventResponseDTO)
        .collect(Collectors.toList());
  }

  /**
   * Convert from domain page response to controller page response.
   *
   * @param pageResponse Domain page response
   * @return Controller page response
   */
  public static PageResponseDTO<EventResponseDTO> toPageResponseDTO(
      PageResponse<EventDTO> pageResponse) {
    if (pageResponse == null) {
      return null;
    }

    List<EventResponseDTO> responseDTOs = toEventResponseDTOs(pageResponse.getList());

    return PageResponseDTO.of(
        responseDTOs,
        pageResponse.getLimit(),
        pageResponse.getTotalRecords());
  }
}