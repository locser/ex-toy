package locser.toy.controller.response;

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
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    /**
     * Tạo một phản hồi thành công với dữ liệu.
     * 
     * @param data Dữ liệu phản hồi
     * @param message Thông báo phản hồi
     * @param <T> Kiểu dữ liệu của dữ liệu phản hồi
     * @return Đối tượng ApiResponse
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Tạo một phản hồi thành công với dữ liệu.
     * 
     * @param data Dữ liệu phản hồi
     * @param <T> Kiểu dữ liệu của dữ liệu phản hồi
     * @return Đối tượng ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Thành công");
    }
    
    /**
     * Tạo một phản hồi thất bại với thông báo lỗi.
     * 
     * @param message Thông báo lỗi
     * @param <T> Kiểu dữ liệu của dữ liệu phản hồi
     * @return Đối tượng ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
