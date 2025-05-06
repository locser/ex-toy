package locser.controller.response;

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

  private Integer status;
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
        .status(200)
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
        .status(400)
        .message(message)
        .build();
  }

  /**
   * Tạo một phản hồi thất bại với mã lỗi và thông báo lỗi.
   *
   * @param status  Mã lỗi
   * @param message Thông báo lỗi
   * @param <T>     Kiểu dữ liệu của dữ liệu phản hồi
   * @return Đối tượng BaseResponse
   */
  public static <T> BaseResponse<T> error(Integer status, String message) {
    return BaseResponse.<T>builder()
        .status(status)
        .message(message)
        .build();
  }

  /**
   * Tạo một phản hồi thất bại với mã lỗi, thông báo lỗi và dữ liệu.
   *
   * @param status  Mã lỗi
   * @param message Thông báo lỗi
   * @param data    Dữ liệu phản hồi
   * @param <T>     Kiểu dữ liệu của dữ liệu phản hồi
   * @return Đối tượng BaseResponse
   */
  public static <T> BaseResponse<T> error(Integer status, String message, T data) {
    return BaseResponse.<T>builder()
        .status(status)
        .message(message)
        .data(data)
        .build();
  }
}