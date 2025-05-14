package locser.infrastructure.grpc.temp;

import java.util.Objects;

/**
 * Temporary class to represent the generated CreateToyRequest class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class CreateToyRequest {
    private final long userId;
    private final String name;
    private final String description;
    private final String category;
    private final int condition;
    private final long campaignId;
    private final String desiredExchangeItems;
    
    private CreateToyRequest(Builder builder) {
        this.userId = builder.userId;
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category;
        this.condition = builder.condition;
        this.campaignId = builder.campaignId;
        this.desiredExchangeItems = builder.desiredExchangeItems;
    }
    
    /**
     * Gets the user ID of the toy.
     *
     * @return The user ID of the toy
     */
    public long getUserId() {
        return userId;
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
     * Builder for CreateToyRequest.
     */
    public static class Builder {
        private long userId;
        private String name = "";
        private String description = "";
        private String category = "";
        private int condition;
        private long campaignId;
        private String desiredExchangeItems = "";
        
        /**
         * Sets the user ID of the toy.
         *
         * @param userId The user ID of the toy
         * @return This builder
         */
        public Builder setUserId(long userId) {
            this.userId = userId;
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
         * Builds a new CreateToyRequest.
         *
         * @return A new CreateToyRequest
         */
        public CreateToyRequest build() {
            return new CreateToyRequest(this);
        }
    }
}
