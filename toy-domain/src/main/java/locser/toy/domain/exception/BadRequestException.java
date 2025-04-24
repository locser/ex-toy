package locser.toy.domain.exception;

/**
 * Exception được ném ra khi yêu cầu không hợp lệ.
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
