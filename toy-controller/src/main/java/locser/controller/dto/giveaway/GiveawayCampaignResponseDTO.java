package locser.controller.dto.giveaway;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin của một Giveaway Campaign trong phản hồi API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiveawayCampaignResponseDTO {
    private Long id;
    private String name;
    private String description;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    private String theme;
    private String rules;
    private Integer status;

    @JsonProperty("claimed_toys")
    private Long claimedToys;

    @JsonProperty("total_participants")
    private Long totalParticipants;

    @JsonProperty("can_participate")
    private Boolean canParticipate;

    @JsonProperty("user_participated")
    private Boolean userParticipated;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_expired")
    private Boolean isExpired;

    @JsonProperty("total_toys")
    private Integer totalToys;

    @JsonProperty("available_toys")
    private Integer availableToys;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
