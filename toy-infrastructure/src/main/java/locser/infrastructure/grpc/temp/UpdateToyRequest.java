package locser.infrastructure.grpc.temp;

import java.util.Objects;

/**
 * Temporary class to represent the generated UpdateToyRequest class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class UpdateToyRequest {
    private final long id;
    private final String name;
    private final String description;
    private final String category;
    private final int condition;
    private final int status;
    private final long campaignId;
    private final String desiredExchangeItems;
    
    private UpdateToyRequest(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category;
        this.condition = builder.condition;
        this.status = builder.status;
        this.campaignId = builder.campaignId;
        this.desiredExchangeItems = builder.desiredExchangeItems;
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
     * Gets the name of the toy.
     *
     * @return The name of the toy
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the description of the toy.
     *
     * @return The description of the toy
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the category of the toy.
     *
     * @return The category of the toy
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Gets the condition of the toy.
     *
     * @return The condition of the toy
     */
    public int getCondition() {
        return condition;
    }
    
    /**
     * Gets the status of the toy.
     *
     * @return The status of the toy
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Gets the campaign ID of the toy.
     *
     * @return The campaign ID of the toy
     */
    public long getCampaignId() {
        return campaignId;
    }
    
    /**
     * Gets the desired exchange items of the toy.
     *
     * @return The desired exchange items of the toy
     */
    public String getDesiredExchangeItems() {
        return desiredExchangeItems;
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
     * Builder for UpdateToyRequest.
     */
    public static class Builder {
        private long id;
        private String name = "";
        private String description = "";
        private String category = "";
        private int condition;
        private int status;
        private long campaignId;
        private String desiredExchangeItems = "";
        
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
         * Sets the name of the toy.
         *
         * @param name The name of the toy
         * @return This builder
         */
        public Builder setName(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }
        
        /**
         * Sets the description of the toy.
         *
         * @param description The description of the toy
         * @return This builder
         */
        public Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description);
            return this;
        }
        
        /**
         * Sets the category of the toy.
         *
         * @param category The category of the toy
         * @return This builder
         */
        public Builder setCategory(String category) {
            this.category = Objects.requireNonNull(category);
            return this;
        }
        
        /**
         * Sets the condition of the toy.
         *
         * @param condition The condition of the toy
         * @return This builder
         */
        public Builder setCondition(int condition) {
            this.condition = condition;
            return this;
        }
        
        /**
         * Sets the status of the toy.
         *
         * @param status The status of the toy
         * @return This builder
         */
        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }
        
        /**
         * Sets the campaign ID of the toy.
         *
         * @param campaignId The campaign ID of the toy
         * @return This builder
         */
        public Builder setCampaignId(long campaignId) {
            this.campaignId = campaignId;
            return this;
        }
        
        /**
         * Sets the desired exchange items of the toy.
         *
         * @param desiredExchangeItems The desired exchange items of the toy
         * @return This builder
         */
        public Builder setDesiredExchangeItems(String desiredExchangeItems) {
            this.desiredExchangeItems = Objects.requireNonNull(desiredExchangeItems);
            return this;
        }
        
        /**
         * Builds a new UpdateToyRequest.
         *
         * @return A new UpdateToyRequest
         */
        public UpdateToyRequest build() {
            return new UpdateToyRequest(this);
        }
    }
}
