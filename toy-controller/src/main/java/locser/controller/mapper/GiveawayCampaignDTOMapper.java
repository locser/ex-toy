package locser.controller.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import locser.controller.dto.giveaway.AddToysToGiveawayCampaignRequestDTO;
import locser.controller.dto.giveaway.CreateGiveawayCampaignRequestDTO;
import locser.controller.dto.giveaway.GiveawayCampaignResponseDTO;
import locser.controller.dto.giveaway.GiveawayCampaignStatsResponseDTO;
import locser.controller.dto.giveaway.ToyParticipationResponseDTO;
import locser.toy.domain.model.dto.AddToysToGiveawayCampaignRequest;
import locser.toy.domain.model.dto.CreateGiveawayCampaignRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.GiveawayCampaignStatsDTO;
import locser.toy.domain.model.dto.ToyParticipationDTO;
import locser.toy.domain.model.enums.ToyParticipationStatus;

/**
 * Mapper class to convert between domain DTOs and controller DTOs for Giveaway
 * Campaign.
 */
public class GiveawayCampaignDTOMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    /**
     * Convert from controller DTO to domain DTO for create request.
     *
     * @param dto Controller DTO
     * @return Domain DTO
     */
    public static CreateGiveawayCampaignRequest toCreateGiveawayCampaignRequest(CreateGiveawayCampaignRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return CreateGiveawayCampaignRequest.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(LocalDateTime.parse(dto.getStartDate() + ":00",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .endDate(LocalDateTime.parse(dto.getEndDate() + ":59",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .theme(dto.getTheme())
                .rules(dto.getRules())
                .build();
    }

    /**
     * Convert from controller DTO to domain DTO for add toys request.
     *
     * @param dto Controller DTO
     * @return Domain DTO
     */
    public static AddToysToGiveawayCampaignRequest toAddToysToGiveawayCampaignRequest(
            AddToysToGiveawayCampaignRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return AddToysToGiveawayCampaignRequest.builder()
                .toyIds(dto.getToyIds())
                .build();
    }

    /**
     * Convert from domain DTO to controller response DTO.
     *
     * @param dto Domain DTO
     * @return Controller response DTO
     */
    public static GiveawayCampaignResponseDTO toGiveawayCampaignResponseDTO(EventDTO dto) {
        if (dto == null) {
            return null;
        }

        return GiveawayCampaignResponseDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate().format(DATE_TIME_FORMATTER))
                .endDate(dto.getEndDate().format(DATE_TIME_FORMATTER))
                .theme(dto.getTheme())
                .rules(dto.getRules())
                .status(dto.getStatus())
                .totalToys(dto.getTotalToys())
                .availableToys(dto.getAvailableToys())
                .createdAt(formatInstant(dto.getCreatedAt()))
                .updatedAt(formatInstant(dto.getUpdatedAt()))
                .build();
    }

    /**
     * Convert from domain DTO to controller response DTO with additional info.
     *
     * @param dto               Domain DTO
     * @param totalToys         Total toys in campaign
     * @param availableToys     Available toys in campaign
     * @param totalParticipants Total participants
     * @param canParticipate    Can user participate
     * @param userParticipated  Has user participated
     * @param isActive          Is campaign active
     * @param isExpired         Is campaign expired
     * @return Controller response DTO
     */
    public static GiveawayCampaignResponseDTO toGiveawayCampaignResponseDTO(
            EventDTO dto, Long totalToys, Long availableToys, Long totalParticipants,
            int canParticipate, int userParticipated, int isActive, int isExpired) {

        GiveawayCampaignResponseDTO response = toGiveawayCampaignResponseDTO(dto);
        if (response != null) {
            response.setTotalToys(totalToys.intValue());
            response.setAvailableToys(availableToys.intValue());
            response.setClaimedToys(totalToys - availableToys);
            response.setTotalParticipants(totalParticipants);
            response.setCanParticipate(canParticipate);
            response.setUserParticipated(userParticipated);
            response.setIsActive(isActive);
            response.setIsExpired(isExpired);
        }
        return response;
    }

    /**
     * Convert from domain stats DTO to controller response DTO.
     *
     * @param dto Domain stats DTO
     * @return Controller response DTO
     */
    public static GiveawayCampaignStatsResponseDTO toGiveawayCampaignStatsResponseDTO(GiveawayCampaignStatsDTO dto) {
        if (dto == null) {
            return null;
        }

        return GiveawayCampaignStatsResponseDTO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .totalToys(dto.getTotalToys())
                .availableToys(dto.getAvailableToys())
                .claimedToys(dto.getClaimedToys())
                .totalParticipants(dto.getTotalParticipants())
                .participationRate(dto.getParticipationRate())
                .isActive(dto.getIsActive())
                .isExpired(dto.getIsExpired())
                .build();
    }

    /**
     * Convert from domain participation DTO to controller response DTO.
     *
     * @param dto Domain participation DTO
     * @return Controller response DTO
     */
    public static ToyParticipationResponseDTO toToyParticipationResponseDTO(ToyParticipationDTO dto) {
        if (dto == null) {
            return null;
        }

        return ToyParticipationResponseDTO.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .toyId(dto.getToyId())
                .campaignId(dto.getCampaignId())
                .campaignName(dto.getCampaignName())
                // .toy(dto.getToy() != null ? ToyDTOMapper.toToyResponseDTO(dto.getToy()) :
                // null) // TODO: Implement ToyDTOMapper
                .participationDate(dto.getParticipationDate().format(DATE_TIME_FORMATTER))
                .status(dto.getStatus())
                .statusText(getParticipationStatusText(dto.getStatus()))
                .createdAt(formatInstant(dto.getCreatedAt()))
                .updatedAt(formatInstant(dto.getUpdatedAt()))
                .build();
    }

    /**
     * Format Instant to string.
     *
     * @param instant Instant to format
     * @return Formatted string
     */
    private static String formatInstant(java.time.Instant instant) {
        if (instant == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        return DATE_TIME_FORMATTER.format(localDateTime);
    }

    /**
     * Get participation status text.
     *
     * @param status Status code
     * @return Status text in Vietnamese
     */
    private static String getParticipationStatusText(Integer status) {
        if (status == null) {
            return "Không xác định";
        }

        ToyParticipationStatus participationStatus = ToyParticipationStatus.fromValue(status);
        if (participationStatus == null) {
            return "Không xác định";
        }

        switch (participationStatus) {
            case CLAIMED:
                return "Đã nhận";
            case DELIVERED:
                return "Đã giao";
            case CANCELLED:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }
}
