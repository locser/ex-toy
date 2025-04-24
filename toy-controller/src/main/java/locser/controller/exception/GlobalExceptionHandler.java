package locser.controller.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import locser.controller.response.BaseResponse;
import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.exception.ResourceNotFoundException;

/**
 * Xử lý ngoại lệ toàn cục cho tất cả các controller.
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
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(BaseResponse.error(ex.getMessage()));
  }

  /**
   * Xử lý ngoại lệ BadRequestException.
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<Object>> handleBadRequestException(BadRequestException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(ex.getMessage()));
  }

  /**
   * Xử lý ngoại lệ MethodArgumentNotValidException (lỗi validation).
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error("Lỗi validation"));
  }

  /**
   * Xử lý tất cả các ngoại lệ khác.
   *
   * @param ex Ngoại lệ
   * @return Phản hồi lỗi
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<BaseResponse<Object>> handleAllExceptions(Exception ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BaseResponse.error("Lỗi hệ thống: " + ex.getMessage()));
  }
}