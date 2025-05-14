package locser.controller.dto.toy;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu cập nhật một Đồ chơi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateToyRequestDTO {
    private String name;
    private String description;
    private String category;
    private Integer condition;
    private Integer status;

    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("desired_exchange_items")
    private String desiredExchangeItems;
}
