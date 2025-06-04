package locser.toy.domain.model.dto;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đại diện cho thông tin của một Toy Participation.
 * Được sử dụng để truyền dữ liệu giữa các lớp và API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToyParticipationDTO {
    private Long id;
    private Long userId;
    private Long toyId;
    private Long campaignId;
    private String campaignName;
    private ToyDTO toy;
    private LocalDateTime participationDate;
    private Integer status;
    private String statusText;
    private Instant createdAt;
    private Instant updatedAt;
}
