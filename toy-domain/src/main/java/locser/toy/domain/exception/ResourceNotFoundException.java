package locser.toy.domain.exception;

/**
 * Exception được ném ra khi không tìm thấy tài nguyên được yêu cầu.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
