package locser.controller.dto.toy;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin của một Đồ chơi trong phản hồi API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToyResponseDTO {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("campaign_id")
    private Long campaignId;

    private String name;
    private String description;
    private String category;
    private Integer condition;
    private Integer status;

    @JsonProperty("desired_exchange_items")
    private String desiredExchangeItems;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
