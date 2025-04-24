package locser.controller.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lớp đại diện cho phản hồi API chung.
 *
 * @param <T> Kiểu dữ liệu của dữ liệu phản hồi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {

  private HttpStatus success;
  private String message;
  private T data;

  /**
   * Tạo một phản hồi thành công với dữ liệu.
   *
   * @param data    Dữ liệu phản hồi
   * @param message Thông báo phản hồi
   * @param <T>     Kiểu dữ liệu của dữ liệu phản hồi
   * @return Đối tượng BaseResponse
   */
  public static <T> BaseResponse<T> success(T data, String message) {
    return BaseResponse.<T>builder()
        .success(HttpStatus.OK)
        .message(message)
        .data(data)
        .build();
  }

  /**
   * Tạo một phản hồi thành công với dữ liệu.
   *
   * @param data Dữ liệu phản hồi
   * @param <T>  Kiểu dữ liệu của dữ liệu phản hồi
   * @return Đối tượng BaseResponse
   */
  public static <T> BaseResponse<T> success(T data) {
    return success(data, "Thành công");
  }

  public static BaseResponse success() {
    return success(null, "Thành công");
  }

  /**
   * Tạo một phản hồi thất bại với thông báo lỗi.
   *
   * @param message Thông báo lỗi
   * @param <T>     Kiểu dữ liệu của dữ liệu phản hồi
   * @return Đối tượng BaseResponse
   */
  public static <T> BaseResponse<T> error(String message) {
    return BaseResponse.<T>builder()
        .success(HttpStatus.BAD_REQUEST)
        .message(message)
        .build();
  }
}