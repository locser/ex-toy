package locser.infrastructure.grpc.temp;

import java.util.Objects;

/**
 * Temporary class to represent the generated ListToysRequest class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class ListToysRequest {
    private final int page;
    private final int limit;
    private final Long userId;
    private final Integer status;
    private final Long campaignId;
    private final String sortBy;
    private final String sortDirection;
    
    private ListToysRequest(Builder builder) {
        this.page = builder.page;
        this.limit = builder.limit;
        this.userId = builder.userId;
        this.status = builder.status;
        this.campaignId = builder.campaignId;
        this.sortBy = builder.sortBy;
        this.sortDirection = builder.sortDirection;
    }
    
    /**
     * Gets the page number.
     *
     * @return The page number
     */
    public int getPage() {
        return page;
    }
    
    /**
     * Gets the page size.
     *
     * @return The page size
     */
    public int getLimit() {
        return limit;
    }
    
    /**
     * Gets the user ID filter.
     *
     * @return The user ID filter
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * Checks if the user ID filter is set.
     *
     * @return Whether the user ID filter is set
     */
    public boolean hasUserId() {
        return userId != null;
    }
    
    /**
     * Gets the status filter.
     *
     * @return The status filter
     */
    public Integer getStatus() {
        return status;
    }
    
    /**
     * Checks if the status filter is set.
     *
     * @return Whether the status filter is set
     */
    public boolean hasStatus() {
        return status != null;
    }
    
    /**
     * Gets the campaign ID filter.
     *
     * @return The campaign ID filter
     */
    public Long getCampaignId() {
        return campaignId;
    }
    
    /**
     * Checks if the campaign ID filter is set.
     *
     * @return Whether the campaign ID filter is set
     */
    public boolean hasCampaignId() {
        return campaignId != null;
    }
    
    /**
     * Gets the sort by field.
     *
     * @return The sort by field
     */
    public String getSortBy() {
        return sortBy;
    }
    
    /**
     * Gets the sort direction.
     *
     * @return The sort direction
     */
    public String getSortDirection() {
        return sortDirection;
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
     * Builder for ListToysRequest.
     */
    public static class Builder {
        private int page;
        private int limit;
        private Long userId;
        private Integer status;
        private Long campaignId;
        private String sortBy = "id";
        private String sortDirection = "desc";
        
        /**
         * Sets the page number.
         *
         * @param page The page number
         * @return This builder
         */
        public Builder setPage(int page) {
            this.page = page;
            return this;
        }
        
        /**
         * Sets the page size.
         *
         * @param limit The page size
         * @return This builder
         */
        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }
        
        /**
         * Sets the user ID filter.
         *
         * @param userId The user ID filter
         * @return This builder
         */
        public Builder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        /**
         * Sets the status filter.
         *
         * @param status The status filter
         * @return This builder
         */
        public Builder setStatus(Integer status) {
            this.status = status;
            return this;
        }
        
        /**
         * Sets the campaign ID filter.
         *
         * @param campaignId The campaign ID filter
         * @return This builder
         */
        public Builder setCampaignId(Long campaignId) {
            this.campaignId = campaignId;
            return this;
        }
        
        /**
         * Sets the sort by field.
         *
         * @param sortBy The sort by field
         * @return This builder
         */
        public Builder setSortBy(String sortBy) {
            this.sortBy = Objects.requireNonNull(sortBy);
            return this;
        }
        
        /**
         * Sets the sort direction.
         *
         * @param sortDirection The sort direction
         * @return This builder
         */
        public Builder setSortDirection(String sortDirection) {
            this.sortDirection = Objects.requireNonNull(sortDirection);
            return this;
        }
        
        /**
         * Builds a new ListToysRequest.
         *
         * @return A new ListToysRequest
         */
        public ListToysRequest build() {
            return new ListToysRequest(this);
        }
    }
}
