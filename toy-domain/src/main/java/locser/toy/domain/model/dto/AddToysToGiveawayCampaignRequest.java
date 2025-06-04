package locser.toy.domain.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu thêm toys vào Giveaway Campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToysToGiveawayCampaignRequest {

    @NotNull(message = "Danh sách toy IDs không được null")
    @NotEmpty(message = "Danh sách toy IDs không được để trống")
    private List<Long> toyIds;
}
