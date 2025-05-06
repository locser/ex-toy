package locser.controller.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import locser.controller.response.BaseResponse;
import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.exception.ResourceNotFoundException;

/**
 * Xử lý ngoại lệ toàn cục cho tất cả các controller.
 * Tất cả các phản hồi đều có cùng một cấu trúc và trả về mã HTTP 200 OK.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Xử lý ngoại lệ ResourceNotFoundException.
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.error(404, ex.getMessage()));
  }

  /**
   * Xử lý ngoại lệ BadRequestException.
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<BaseResponse<Object>> handleBadRequestException(BadRequestException ex) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.error(400, ex.getMessage()));
  }

  /**
   * Xử lý ngoại lệ MethodArgumentNotValidException (lỗi validation).
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.error(400, "Lỗi validation", errors));
  }

  /**
   * Xử lý ngoại lệ HttpMessageNotReadableException (lỗi chuyển đổi JSON).
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<BaseResponse<Object>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.error(400, "Lỗi định dạng dữ liệu: " + ex.getMessage()));
  }

  /**
   * Xử lý tất cả các ngoại lệ khác.
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Object>> handleAllExceptions(Exception ex) {
    System.out.println("Lỗi hệ thống: " + ex);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(BaseResponse.error(500, "Lỗi hệ thống: " + ex.getMessage()));
  }
}