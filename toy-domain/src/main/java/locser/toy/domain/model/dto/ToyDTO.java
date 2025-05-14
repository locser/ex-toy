package locser.toy.domain.model.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin của một Đồ chơi.
 * Được sử dụng để truyền dữ liệu giữa các lớp và API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToyDTO {
    private Long id;
    private Long userId;
    private Long campaignId;
    private String name;
    private String description;
    private String category;
    private Integer condition;
    private Integer status;
    private String desiredExchangeItems;
    private Instant createdAt;
    private Instant updatedAt;
}
