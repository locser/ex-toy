package locser.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PaginationDTO {

  @NotNull
  @Min(1)
  int page;

  @NotNull
  @Max(500)
  int limit;

  public PaginationDTO() {
    this.page = 1;
    this.limit = 20;
  }

  public PaginationDTO(int page, int limit) {
    this.page = page > 0 ? page : 1;
    this.limit = limit > 0 ? limit : 20;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return (page - 1) * limit;
  }
}