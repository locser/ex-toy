package locser.toy.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO đại diện cho phản hồi phân trang.
 * @param <T> Kiểu dữ liệu của danh sách phần tử
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int limit;
    private long totalRecord;
    private List<T> list;

    /**
     * Tạo một đối tượng PageResponse từ danh sách và thông tin phân trang.
     *
     * @param list Danh sách phần tử
     * @param limit Số lượng phần tử tối đa trên một trang
     * @param totalRecord Tổng số bản ghi
     * @param <T> Kiểu dữ liệu của danh sách phần tử
     * @return Đối tượng PageResponse
     */
    public static <T> PageResponse<T> of(List<T> list, int limit, long totalRecord) {
        return PageResponse.<T>builder()
                .list(list)
                .limit(limit)
                .totalRecord(totalRecord)
                .build();
    }
}
