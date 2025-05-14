package locser.infrastructure.grpc.temp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Temporary class to represent the generated ListToysResponse class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class ListToysResponse {
    private final List<ToyResponse> toys;
    private final int limit;
    private final long totalRecord;
    
    private ListToysResponse(Builder builder) {
        this.toys = Collections.unmodifiableList(new ArrayList<>(builder.toys));
        this.limit = builder.limit;
        this.totalRecord = builder.totalRecord;
    }
    
    /**
     * Gets the list of toys.
     *
     * @return The list of toys
     */
    public List<ToyResponse> getToysList() {
        return toys;
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
     * Gets the total number of records.
     *
     * @return The total number of records
     */
    public long getTotalRecord() {
        return totalRecord;
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
     * Builder for ListToysResponse.
     */
    public static class Builder {
        private final List<ToyResponse> toys = new ArrayList<>();
        private int limit;
        private long totalRecord;
        
        /**
         * Adds a toy to the list.
         *
         * @param toy The toy to add
         * @return This builder
         */
        public Builder addToy(ToyResponse toy) {
            this.toys.add(Objects.requireNonNull(toy));
            return this;
        }
        
        /**
         * Adds all toys to the list.
         *
         * @param toys The toys to add
         * @return This builder
         */
        public Builder addAllToys(List<ToyResponse> toys) {
            this.toys.addAll(Objects.requireNonNull(toys));
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
         * Sets the total number of records.
         *
         * @param totalRecord The total number of records
         * @return This builder
         */
        public Builder setTotalRecord(long totalRecord) {
            this.totalRecord = totalRecord;
            return this;
        }
        
        /**
         * Builds a new ListToysResponse.
         *
         * @return A new ListToysResponse
         */
        public ListToysResponse build() {
            return new ListToysResponse(this);
        }
    }
}
