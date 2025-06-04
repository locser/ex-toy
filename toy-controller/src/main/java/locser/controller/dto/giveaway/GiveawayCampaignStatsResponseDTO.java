package locser.controller.dto.giveaway;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thống kê của một Giveaway Campaign trong phản hồi API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiveawayCampaignStatsResponseDTO {
    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("campaign_name")
    private String campaignName;

    @JsonProperty("total_toys")
    private Long totalToys;

    @JsonProperty("available_toys")
    private Long availableToys;

    @JsonProperty("claimed_toys")
    private Long claimedToys;

    @JsonProperty("total_participants")
    private Long totalParticipants;

    @JsonProperty("participation_rate")
    private Double participationRate;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_expired")
    private Boolean isExpired;
}
