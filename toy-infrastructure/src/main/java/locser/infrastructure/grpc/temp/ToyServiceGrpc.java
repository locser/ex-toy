package locser.infrastructure.grpc.temp;

import io.grpc.ManagedChannel;

/**
 * Temporary class to represent the generated ToyServiceGrpc class.
 * This will be replaced by the actual generated class when proto files are compiled.
 */
public class ToyServiceGrpc {
    
    /**
     * Blocking stub for making synchronous calls to the ToyService.
     */
    public static class ToyServiceBlockingStub {
        
        /**
         * Creates a new blocking stub.
         *
         * @param channel The channel to use for calls
         * @return A new blocking stub
         */
        public static ToyServiceBlockingStub newStub(ManagedChannel channel) {
            return new ToyServiceBlockingStub();
        }
        
        /**
         * Gets a toy by ID.
         *
         * @param request The request containing the toy ID
         * @return The toy response
         */
        public ToyResponse getToy(ToyRequest request) {
            return new ToyResponse.Builder()
                    .setId(request.getId())
                    .setName("Temporary Toy")
                    .setDescription("This is a temporary toy response")
                    .setUserId(1L)
                    .setCampaignId(0L)
                    .setCategory("Unknown")
                    .setCondition(1)
                    .setStatus(1)
                    .setDesiredExchangeItems("")
                    .setCreatedAt("2023-01-01T00:00:00Z")
                    .setUpdatedAt("2023-01-01T00:00:00Z")
                    .build();
        }
        
        /**
         * Creates a new toy.
         *
         * @param request The request containing the toy details
         * @return The created toy response
         */
        public ToyResponse createToy(CreateToyRequest request) {
            return new ToyResponse.Builder()
                    .setId(1L)
                    .setName(request.getName())
                    .setDescription(request.getDescription())
                    .setUserId(request.getUserId())
                    .setCampaignId(request.getCampaignId())
                    .setCategory(request.getCategory())
                    .setCondition(request.getCondition())
                    .setStatus(1)
                    .setDesiredExchangeItems(request.getDesiredExchangeItems())
                    .setCreatedAt("2023-01-01T00:00:00Z")
                    .setUpdatedAt("2023-01-01T00:00:00Z")
                    .build();
        }
        
        /**
         * Updates an existing toy.
         *
         * @param request The request containing the updated toy details
         * @return The updated toy response
         */
        public ToyResponse updateToy(UpdateToyRequest request) {
            return new ToyResponse.Builder()
                    .setId(request.getId())
                    .setName(request.getName())
                    .setDescription(request.getDescription())
                    .setUserId(1L)
                    .setCampaignId(request.getCampaignId())
                    .setCategory(request.getCategory())
                    .setCondition(request.getCondition())
                    .setStatus(request.getStatus())
                    .setDesiredExchangeItems(request.getDesiredExchangeItems())
                    .setCreatedAt("2023-01-01T00:00:00Z")
                    .setUpdatedAt("2023-01-01T00:00:00Z")
                    .build();
        }
        
        /**
         * Deletes a toy.
         *
         * @param request The request containing the toy ID
         * @return The delete response
         */
        public DeleteToyResponse deleteToy(ToyRequest request) {
            return new DeleteToyResponse.Builder()
                    .setSuccess(true)
                    .setMessage("Toy deleted successfully")
                    .build();
        }
        
        /**
         * Lists toys with pagination.
         *
         * @param request The request containing pagination parameters
         * @return The list toys response
         */
        public ListToysResponse listToys(ListToysRequest request) {
            ListToysResponse.Builder builder = new ListToysResponse.Builder()
                    .setLimit(request.getLimit())
                    .setTotalRecord(10L);
            
            // Add some dummy toys
            for (int i = 0; i < Math.min(request.getLimit(), 5); i++) {
                builder.addToy(new ToyResponse.Builder()
                        .setId((long) (i + 1))
                        .setName("Toy " + (i + 1))
                        .setDescription("Description " + (i + 1))
                        .setUserId(1L)
                        .setCampaignId(0L)
                        .setCategory("Category " + (i % 3 + 1))
                        .setCondition(i % 3 + 1)
                        .setStatus(1)
                        .setDesiredExchangeItems("")
                        .setCreatedAt("2023-01-01T00:00:00Z")
                        .setUpdatedAt("2023-01-01T00:00:00Z")
                        .build());
            }
            
            return builder.build();
        }
    }
    
    /**
     * Creates a new blocking stub.
     *
     * @param channel The channel to use for calls
     * @return A new blocking stub
     */
    public static ToyServiceBlockingStub newBlockingStub(ManagedChannel channel) {
        return ToyServiceBlockingStub.newStub(channel);
    }
}
