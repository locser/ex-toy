package locser.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic class for handling paginated responses.
 *
 * @param <T> The type of items in the content list
 */
@Data
@Getter
@Setter
public class PageResponse<T> {

  private int limit;
  private long total_records;
  private List<T> list;

  public PageResponse() {
  }

  public PageResponse(List<T> list, int limit, long totalRecords) {
    this.limit = limit;
    this.total_records = totalRecords;
    this.list = list == null ? new ArrayList<>() : list;
  }

  public int getTotalRecords() {
    return (int) total_records;
  }

  public int getLimit() {
    return limit;
  }

  public void setTotalRecords(long totalRecords) {
    this.total_records = totalRecords;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

}