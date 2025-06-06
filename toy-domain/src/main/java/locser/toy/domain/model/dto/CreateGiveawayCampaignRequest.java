package locser.toy.domain.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu tạo mới một Giveaway Campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGiveawayCampaignRequest {

    @NotBlank(message = "Tên chiến dịch không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    @NotBlank(message = "Chủ đề không được để trống")
    private String theme;

    @NotBlank(message = "Quy tắc không được để trống")
    private String rules;

    @NotNull(message = "Tổng số toy không được để trống")
    @Min(value = 1, message = "Tổng số toy phải lớn hơn 0")
    private Integer totalToys;
}
