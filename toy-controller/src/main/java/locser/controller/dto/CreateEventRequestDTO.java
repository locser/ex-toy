package locser.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu tạo mới một Sự kiện/Chiến dịch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequestDTO {

  @NotBlank(message = "Tên sự kiện không được để trống")
  private String name;

  private String description;

  @NotNull(message = "Ngày bắt đầu không được để trống")
  private LocalDateTime startDate;

  @NotNull(message = "Ngày kết thúc không được để trống")
  private LocalDateTime endDate;

  private String theme;

  private String rules;
}
