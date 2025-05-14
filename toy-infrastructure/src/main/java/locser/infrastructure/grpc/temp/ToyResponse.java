package locser.infrastructure.grpc.temp;

import java.util.Objects;

/**
 * Temporary class to represent the generated ToyResponse class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class ToyResponse {
    private final long id;
    private final long userId;
    private final long campaignId;
    private final String name;
    private final String description;
    private final String category;
    private final int condition;
    private final int status;
    private final String desiredExchangeItems;
    private final String createdAt;
    private final String updatedAt;
    
    private ToyResponse(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.campaignId = builder.campaignId;
        this.name = builder.name;
        this.description = builder.description;
        this.category = builder.category;
        this.condition = builder.condition;
        this.status = builder.status;
        this.desiredExchangeItems = builder.desiredExchangeItems;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
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
     * Gets the user ID of the toy.
     *
     * @return The user ID of the toy
     */
    public long getUserId() {
        return userId;
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
     * Gets the desired exchange items of the toy.
     *
     * @return The desired exchange items of the toy
     */
    public String getDesiredExchangeItems() {
        return desiredExchangeItems;
    }
    
    /**
     * Gets the created at timestamp of the toy.
     *
     * @return The created at timestamp of the toy
     */
    public String getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Gets the updated at timestamp of the toy.
     *
     * @return The updated at timestamp of the toy
     */
    public String getUpdatedAt() {
        return updatedAt;
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
     * Builder for ToyResponse.
     */
    public static class Builder {
        private long id;
        private long userId;
        private long campaignId;
        private String name = "";
        private String description = "";
        private String category = "";
        private int condition;
        private int status;
        private String desiredExchangeItems = "";
        private String createdAt = "";
        private String updatedAt = "";
        
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
         * Sets the created at timestamp of the toy.
         *
         * @param createdAt The created at timestamp of the toy
         * @return This builder
         */
        public Builder setCreatedAt(String createdAt) {
            this.createdAt = Objects.requireNonNull(createdAt);
            return this;
        }
        
        /**
         * Sets the updated at timestamp of the toy.
         *
         * @param updatedAt The updated at timestamp of the toy
         * @return This builder
         */
        public Builder setUpdatedAt(String updatedAt) {
            this.updatedAt = Objects.requireNonNull(updatedAt);
            return this;
        }
        
        /**
         * Builds a new ToyResponse.
         *
         * @return A new ToyResponse
         */
        public ToyResponse build() {
            return new ToyResponse(this);
        }
    }
}
