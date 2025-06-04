package locser.controller.dto.giveaway;

import com.fasterxml.jackson.annotation.JsonProperty;

import locser.controller.dto.toy.ToyResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin của một Toy Participation trong phản hồi API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToyParticipationResponseDTO {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("toy_id")
    private Long toyId;

    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("campaign_name")
    private String campaignName;

    private ToyResponseDTO toy;

    @JsonProperty("participation_date")
    private String participationDate;

    private Integer status;

    @JsonProperty("status_text")
    private String statusText;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
