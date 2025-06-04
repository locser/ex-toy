package locser.controller.dto.giveaway;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu thêm toys vào Giveaway Campaign từ Controller.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddToysToGiveawayCampaignRequestDTO {

    @JsonProperty("toy_ids")
    @NotNull(message = "Danh sách toy IDs không được null")
    @NotEmpty(message = "Danh sách toy IDs không được để trống")
    private List<Long> toyIds;
}
