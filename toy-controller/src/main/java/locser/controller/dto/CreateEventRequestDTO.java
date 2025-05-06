package locser.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Ngày bắt đầu phải có định dạng DD/MM/YYYY")
  @JsonProperty("start_date")
  private String startDate; // start_date prop

  @NotNull(message = "Ngày kết thúc không được để trống")
  @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Ngày kết thúc phải có định dạng DD/MM/YYYY")
  @JsonProperty("end_date")
  private String endDate;

  @NotNull(message = "Thể loại không được để trống")
  private String theme;

  @NotNull(message = "Quy tắc không được để trống")
  private String rules;
}
