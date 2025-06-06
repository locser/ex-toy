package locser.toy.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thống kê của một Giveaway Campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiveawayCampaignStatsDTO {
    private Long id;
    private String name;
    private Long totalToys;
    private Long availableToys;
    private Long claimedToys;
    private Long totalParticipants;
    private Double participationRate;
    private int isActive;
    private int isExpired;
}
