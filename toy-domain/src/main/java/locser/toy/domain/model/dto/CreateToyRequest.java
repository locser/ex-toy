package locser.toy.domain.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho yêu cầu tạo mới một Đồ chơi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateToyRequest {

  @NotBlank(message = "Tên đồ chơi không được để trống")
  private String name;

  private String description;

  @NotBlank(message = "Danh mục đồ chơi không được để trống")
  private String category;

  @NotNull(message = "Tình trạng đồ chơi không được để trống")
  private Integer condition;

  private Long campaignId;

  private String desiredExchangeItems;
}
