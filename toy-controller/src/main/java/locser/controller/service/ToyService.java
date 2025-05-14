package locser.controller.service;

import java.util.List;

import locser.controller.dto.PageResponseDTO;
import locser.controller.dto.toy.CreateToyRequestDTO;
import locser.controller.dto.toy.ToyResponseDTO;
import locser.controller.dto.toy.UpdateToyRequestDTO;

/**
 * Service interface for Toy controller.
 */
public interface ToyService {

    /**
     * Create a new toy.
     *
     * @param userId  User ID
     * @param request Toy creation request
     * @return Created toy response
     */
    ToyResponseDTO createToy(Long userId, CreateToyRequestDTO request);

    /**
     * Get all toys.
     *
     * @param status Toy status (optional)
     * @return List of toys
     */
    List<ToyResponseDTO> getAllToys(Integer status);

    /**
     * Get toys by user ID.
     *
     * @param userId User ID
     * @param status Toy status (optional)
     * @return List of toys
     */
    List<ToyResponseDTO> getToysByUserId(Long userId, Integer status);

    /**
     * Get toy by ID.
     *
     * @param id Toy ID
     * @return Toy response
     */
    ToyResponseDTO getToyById(Long id);

    /**
     * Update toy.
     *
     * @param id      Toy ID
     * @param request Update request
     * @return Updated toy response
     */
    ToyResponseDTO updateToy(Long id, UpdateToyRequestDTO request);

    /**
     * Delete toy.
     *
     * @param id Toy ID
     */
    void deleteToy(Long id);

    /**
     * Update toy status.
     *
     * @param id     Toy ID
     * @param status New status
     * @return Updated toy response
     */
    ToyResponseDTO updateToyStatus(Long id, Integer status);

    /**
     * Get toys with pagination.
     *
     * @param page       Page number
     * @param limit      Page size
     * @param userId     User ID (optional)
     * @param status     Toy status (optional)
     * @param campaignId Campaign ID (optional)
     * @param sortBy     Field to sort by
     * @param sortDir    Sort direction
     * @return Paginated toys response
     */
    PageResponseDTO<ToyResponseDTO> getToysWithPagination(int page, int limit, Long userId, Integer status,
            Long campaignId, String sortBy, String sortDir);

    /**
     * Add toy to campaign.
     *
     * @param id         Toy ID
     * @param campaignId Campaign ID
     * @return Updated toy response
     */
    ToyResponseDTO addToCampaign(Long id, Long campaignId);

    /**
     * Remove toy from campaign.
     *
     * @param id Toy ID
     * @return Updated toy response
     */
    ToyResponseDTO removeFromCampaign(Long id);

    /**
     * Restore deleted toy.
     *
     * @param id Toy ID
     * @return Restored toy response
     */
    ToyResponseDTO restoreToy(Long id);

    /**
     * Check if user is the owner of the toy.
     *
     * @param userId User ID
     * @param toyId  Toy ID
     * @return True if user is the owner, false otherwise
     */
    boolean isOwner(Long userId, Long toyId);
}
