package locser.toy.domain.model.dto;

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
public class UpdateToyRequest {
    private String name;
    private String description;
    private String category;
    private Integer condition;
    private Integer status;
    private Long campaignId;
    private String desiredExchangeItems;
}
