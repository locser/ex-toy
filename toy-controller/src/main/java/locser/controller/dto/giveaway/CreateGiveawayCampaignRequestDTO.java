package locser.controller.dto.giveaway;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu tạo mới một Giveaway Campaign từ Controller.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGiveawayCampaignRequestDTO {

    @NotBlank(message = "Tên chiến dịch không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "Ngày bắt đầu không được để trống")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}$", message = "Ngày bắt đầu phải có định dạng dd/MM/yyyy HH:mm")
    private String startDate;

    @NotBlank(message = "Ngày kết thúc không được để trống")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}$", message = "Ngày kết thúc phải có định dạng dd/MM/yyyy HH:mm")
    private String endDate;

    @NotBlank(message = "Chủ đề không được để trống")
    private String theme;

    @NotBlank(message = "Quy tắc không được để trống")
    private String rules;

    @NotNull(message = "Tổng số toy không được để trống")
    @Min(value = 1, message = "Tổng số toy phải lớn hơn 0")
    private Integer totalToys;
}
