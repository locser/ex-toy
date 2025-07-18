package locser.controller.exception;

/**
 * Xử lý ngoại lệ toàn cục cho tất cả các controller. Tất cả các phản hồi đều có
 * cùng một cấu trúc
 * và trả về mã HTTP 200 OK.
 */
// @RestControllerAdvice
public class GlobalExceptionHandler {

  // /**
  // * Xử lý ngoại lệ ResourceNotFoundException.
  // *
  // * @param ex Ngoại lệ
  // * @return Phản hồi lỗi
  // */
  // @ExceptionHandler(ResourceNotFoundException.class)
  // public ResponseEntity<BaseResponse<Object>> handleResourceNotFoundException(
  // ResourceNotFoundException ex) {
  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(404, ex.getMessage()));
  // }

  // /**
  // * Xử lý ngoại lệ BadRequestException.
  // *
  // * @param ex Ngoại lệ
  // * @return Phản hồi lỗi
  // */
  // @ExceptionHandler(BadRequestException.class)
  // public ResponseEntity<BaseResponse<Object>>
  // handleBadRequestException(BadRequestException ex) {
  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(400, ex.getMessage()));
  // }

  // /**
  // * Xử lý ngoại lệ MethodArgumentNotValidException (lỗi validation).
  // *
  // * @param ex Ngoại lệ
  // * @return Phản hồi lỗi
  // */
  // @ExceptionHandler(MethodArgumentNotValidException.class)
  // public ResponseEntity<BaseResponse<Map<String, String>>>
  // handleValidationExceptions(
  // MethodArgumentNotValidException ex) {
  // Map<String, String> errors = new HashMap<>();
  // ex.getBindingResult().getAllErrors().forEach(error -> {
  // String fieldName = ((FieldError) error).getField();
  // String errorMessage = error.getDefaultMessage();
  // errors.put(fieldName, errorMessage);
  // });

  // // "message": "Lỗi validation: {totalToys=Tổng số toy không được để trống}"

  // // tôi muốn nó trả về là
  // // {
  // // "status": 400,
  // // "message": "Tổng số toy không được để trống",
  // // "data": null
  // // }

  // // Lấy message đầu tiên từ errors (nếu có), nếu không thì trả về message mặc
  // // định
  // String firstErrorMessage = errors.values().stream().findFirst().orElse("Lỗi
  // validation");

  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(400, firstErrorMessage, null));
  // }

  // /**
  // * Xử lý ngoại lệ HttpMessageNotReadableException (lỗi chuyển đổi JSON).
  // *
  // * @param ex Ngoại lệ
  // * @return Phản hồi lỗi
  // */
  // @ExceptionHandler(HttpMessageNotReadableException.class)
  // public ResponseEntity<BaseResponse<Object>>
  // handleHttpMessageNotReadableException(
  // HttpMessageNotReadableException ex) {
  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(400, "Lỗi định dạng dữ liệu: " + ex.getMessage()));
  // }

  // /**
  // * Xử lý tất cả các ngoại lệ khác.
  // *
  // * @param ex Ngoại lệ
  // * @return Phản hồi lỗi
  // */
  // @ExceptionHandler(Exception.class)
  // public ResponseEntity<BaseResponse<Object>> handleAllExceptions(Exception ex)
  // {
  // System.out.println("Lỗi hệ thống: " + ex);
  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(400, "Lỗi hệ thống: " + ex.getMessage()));
  // }

  // @ExceptionHandler(NoResourceFoundException.class)
  // public ResponseEntity<BaseResponse<Object>>
  // handleNoResourceFoundExceptionExceptions(
  // Exception ex) {
  // System.out.println("Lỗi hệ thống: " + ex);
  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(404, "Không tìm thấy API: " + ex.getMessage()));
  // }

  // // MissingServletRequestParameterException
  // @ExceptionHandler(MissingServletRequestParameterException.class)
  // public ResponseEntity<BaseResponse<Object>>
  // handleMissingServletRequestParameterException(
  // MissingServletRequestParameterException ex) {
  // return ResponseEntity
  // .status(HttpStatus.OK)
  // .body(BaseResponse.error(400, "Thiếu tham số: " + ex.getMessage()));
  // }
}