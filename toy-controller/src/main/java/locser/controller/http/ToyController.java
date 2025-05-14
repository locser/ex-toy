package locser.controller.http;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import locser.controller.dto.PageResponseDTO;
import locser.controller.dto.toy.CreateToyRequestDTO;
import locser.controller.dto.toy.ToyResponseDTO;
import locser.controller.dto.toy.UpdateToyRequestDTO;
import locser.controller.response.BaseResponse;
import locser.controller.service.ToyService;
import locser.toy.domain.validation.annotation.ValidId;
import locser.utils.AppConstants;

/**
 * Controller for Toy API endpoints.
 */
@RestController
@RequestMapping("/api/v1/toys")
public class ToyController {

    private final ToyService toyService;

    public ToyController(ToyService toyService) {
        this.toyService = toyService;
    }

    /**
     * Create a new toy.
     *
     * @param userId  User ID
     * @param request Toy creation request
     * @return Created toy response
     */
    @PostMapping
    public BaseResponse<ToyResponseDTO> createToy(
            @RequestParam Long userId,
            @Valid @RequestBody CreateToyRequestDTO request) {
        ToyResponseDTO response = toyService.createToy(userId, request);
        return BaseResponse.success(response);
    }

    /**
     * Get all toys with pagination.
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
    @GetMapping
    public BaseResponse<PageResponseDTO<ToyResponseDTO>> getAllToys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long campaignId,
            @RequestParam(name = "sort_by", required = false, defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(name = "sort_direction", required = false, defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDir) {
        PageResponseDTO<ToyResponseDTO> response = toyService.getToysWithPagination(page, limit, userId, status,
                campaignId, sortBy, sortDir);
        return BaseResponse.success(response);
    }

    /**
     * Get toy by ID.
     *
     * @param id Toy ID
     * @return Toy response
     */
    @GetMapping("/{id}")
    public BaseResponse<ToyResponseDTO> getToyById(@ValidId(entity = "Toy") @PathVariable Long id) {
        ToyResponseDTO response = toyService.getToyById(id);
        return BaseResponse.success(response);
    }

    /**
     * Update toy.
     *
     * @param id      Toy ID
     * @param request Update request
     * @return Updated toy response
     */
    @PutMapping("/{id}")
    public BaseResponse<ToyResponseDTO> updateToy(
            @ValidId(entity = "Toy") @PathVariable Long id,
            @Valid @RequestBody UpdateToyRequestDTO request) {
        ToyResponseDTO response = toyService.updateToy(id, request);
        return BaseResponse.success(response);
    }

    /**
     * Delete toy.
     *
     * @param id Toy ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    public BaseResponse deleteToy(@ValidId(entity = "Toy") @PathVariable Long id) {
        toyService.deleteToy(id);
        return BaseResponse.success();
    }

    /**
     * Update toy status.
     *
     * @param id     Toy ID
     * @param status New status
     * @return Updated toy response
     */
    @PostMapping("/{id}/status")
    public BaseResponse<ToyResponseDTO> updateToyStatus(
            @ValidId(entity = "Toy") @PathVariable Long id,
            @RequestParam Integer status) {
        ToyResponseDTO response = toyService.updateToyStatus(id, status);
        return BaseResponse.success(response);
    }

    /**
     * Add toy to campaign.
     *
     * @param id         Toy ID
     * @param campaignId Campaign ID
     * @return Updated toy response
     */
    @PostMapping("/{id}/campaign/{campaignId}")
    public BaseResponse<ToyResponseDTO> addToCampaign(
            @ValidId(entity = "Toy") @PathVariable Long id,
            @ValidId(entity = "Campaign") @PathVariable Long campaignId) {
        ToyResponseDTO response = toyService.addToCampaign(id, campaignId);
        return BaseResponse.success(response);
    }

    /**
     * Remove toy from campaign.
     *
     * @param id Toy ID
     * @return Updated toy response
     */
    @DeleteMapping("/{id}/campaign")
    public BaseResponse<ToyResponseDTO> removeFromCampaign(@ValidId(entity = "Toy") @PathVariable Long id) {
        ToyResponseDTO response = toyService.removeFromCampaign(id);
        return BaseResponse.success(response);
    }

    /**
     * Restore deleted toy.
     *
     * @param id Toy ID
     * @return Restored toy response
     */
    @PostMapping("/{id}/restore")
    public BaseResponse<ToyResponseDTO> restoreToy(@ValidId(entity = "Toy") @PathVariable Long id) {
        ToyResponseDTO response = toyService.restoreToy(id);
        return BaseResponse.success(response);
    }

    /**
     * Get toys by user ID.
     *
     * @param userId User ID
     * @param status Toy status (optional)
     * @return List of toys
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<ToyResponseDTO>> getToysByUserId(
            @ValidId(entity = "User") @PathVariable Long userId,
            @RequestParam(required = false) Integer status) {
        List<ToyResponseDTO> response = toyService.getToysByUserId(userId, status);
        return BaseResponse.success(response);
    }
}