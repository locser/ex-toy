package locser.infrastructure.grpc.temp;

/**
 * Temporary class to represent the generated ToyRequest class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class ToyRequest {
    private long id;
    
    private ToyRequest(Builder builder) {
        this.id = builder.id;
    }
    
    /**
     * Gets the ID of the toy.
     *
     * @return The ID of the toy
     */
    public long getId() {
        return id;
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
     * Builder for ToyRequest.
     */
    public static class Builder {
        private long id;
        
        /**
         * Sets the ID of the toy.
         *
         * @param id The ID of the toy
         * @return This builder
         */
        public Builder setId(long id) {
            this.id = id;
            return this;
        }
        
        /**
         * Builds a new ToyRequest.
         *
         * @return A new ToyRequest
         */
        public ToyRequest build() {
            return new ToyRequest(this);
        }
    }
}
