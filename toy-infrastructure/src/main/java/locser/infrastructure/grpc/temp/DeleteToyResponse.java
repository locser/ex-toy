package locser.infrastructure.grpc.temp;

import java.util.Objects;

/**
 * Temporary class to represent the generated DeleteToyResponse class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class DeleteToyResponse {
    private final boolean success;
    private final String message;
    
    private DeleteToyResponse(Builder builder) {
        this.success = builder.success;
        this.message = builder.message;
    }
    
    /**
     * Gets whether the deletion was successful.
     *
     * @return Whether the deletion was successful
     */
    public boolean getSuccess() {
        return success;
    }
    
    /**
     * Gets the message of the response.
     *
     * @return The message of the response
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Creates a new builder for this class.
     *
     * @return A new builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }
    
    /**
     * Builder for DeleteToyResponse.
     */
    public static class Builder {
        private boolean success;
        private String message = "";
        
        /**
         * Sets whether the deletion was successful.
         *
         * @param success Whether the deletion was successful
         * @return This builder
         */
        public Builder setSuccess(boolean success) {
            this.success = success;
            return this;
        }
        
        /**
         * Sets the message of the response.
         *
         * @param message The message of the response
         * @return This builder
         */
        public Builder setMessage(String message) {
            this.message = Objects.requireNonNull(message);
            return this;
        }
        
        /**
         * Builds a new DeleteToyResponse.
         *
         * @return A new DeleteToyResponse
         */
        public DeleteToyResponse build() {
            return new DeleteToyResponse(this);
        }
    }
}
