package locser.controller.dto.event;

import locser.controller.dto.PaginationDTO;
import locser.toy.domain.model.enums.EventStatus;
import lombok.Getter;

@Getter
public class PaginationEventDTO extends PaginationDTO {

  EventStatus status;

  public PaginationEventDTO() {
    super();
  }

  public PaginationEventDTO(int page, int limit) {
    super(page, limit);
  }

  public PaginationEventDTO(int page, int limit, EventStatus status) {
    super(page, limit);
    this.status = status;
  }

}