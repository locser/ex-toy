package locser.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu cập nhật trạng thái của một Sự kiện/Chiến dịch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventStatusRequestDTO {
    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 1, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private Integer status;
}
