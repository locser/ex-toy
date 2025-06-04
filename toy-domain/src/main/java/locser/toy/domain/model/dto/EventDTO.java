package locser.toy.domain.model.dto;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin của một Sự kiện/Chiến dịch. Được sử dụng để truyền
 * dữ liệu giữa các
 * lớp và API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

  private Long id;
  private String name;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String theme;
  private String rules;
  private Integer type;
  private long total;
  private Integer status;
  private Instant createdAt;
  private Instant updatedAt;
}