package locser.controller.http;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import locser.application.services.giveaway.GiveawayCampaignApplicationService;
import locser.controller.dto.giveaway.AddToysToGiveawayCampaignRequestDTO;
import locser.controller.dto.giveaway.CreateGiveawayCampaignRequestDTO;
import locser.controller.dto.giveaway.GiveawayCampaignResponseDTO;
import locser.controller.dto.giveaway.GiveawayCampaignStatsResponseDTO;
import locser.controller.dto.giveaway.ToyParticipationResponseDTO;
import locser.controller.mapper.GiveawayCampaignDTOMapper;
import locser.controller.response.BaseResponse;
import locser.toy.domain.model.dto.CreateGiveawayCampaignRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.GiveawayCampaignStatsDTO;
import locser.toy.domain.model.dto.ToyParticipationDTO;
import locser.toy.domain.validation.annotation.ValidId;
import locser.util.PageResponse;
import locser.util.PageResponseDTO;
import locser.utils.AppConstants;

/**
 * Controller xử lý các API liên quan đến Giveaway Campaign.
 */
@RestController
@RequestMapping("/api/v1")
public class GiveawayCampaignController {

  private final GiveawayCampaignApplicationService giveawayCampaignService;

  public GiveawayCampaignController(GiveawayCampaignApplicationService giveawayCampaignService) {
    this.giveawayCampaignService = giveawayCampaignService;
  }

  /**
   * Tạo mới một giveaway campaign.
   *
   * @param requestDTO Thông tin campaign cần tạo
   * @return Campaign đã được tạo
   */
  @PostMapping("/admin/giveaway-campaigns")
  public BaseResponse<GiveawayCampaignResponseDTO> createGiveawayCampaign(
      @Valid @RequestBody CreateGiveawayCampaignRequestDTO requestDTO) {

    System.out.println(requestDTO);

    CreateGiveawayCampaignRequest dto = GiveawayCampaignDTOMapper.toCreateGiveawayCampaignRequest(requestDTO);

    EventDTO campaign = giveawayCampaignService.createGiveawayCampaign(dto);

    return BaseResponse.success(
        GiveawayCampaignDTOMapper.toGiveawayCampaignResponseDTO(campaign),
        "Tạo chiến dịch phát quà thành công");
  }

  /**
   * Lấy danh sách giveaway campaigns với phân trang.
   *
   * @param page          Số trang (bắt đầu từ 1)
   * @param limit         Số lượng items per page
   * @param status        Trạng thái campaign (tùy chọn)
   * @param sortBy        Trường để sắp xếp
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Danh sách campaigns với phân trang
   */
  @GetMapping("/giveaway-campaigns")
  public BaseResponse<PageResponseDTO<GiveawayCampaignResponseDTO>> getGiveawayCampaigns(
      @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
      @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int limit,
      @RequestParam(required = false) Integer status,
      @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY) String sortBy,
      @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {

    PageResponse<EventDTO> campaigns = giveawayCampaignService.getGiveawayCampaignsWithPagination(
        page, limit, status, sortBy, sortDirection);

    PageResponseDTO<GiveawayCampaignResponseDTO> response = PageResponseDTO
        .<GiveawayCampaignResponseDTO>builder()
        .list(campaigns.getList().stream()
            .map(GiveawayCampaignDTOMapper::toGiveawayCampaignResponseDTO)
            .toList())
        .limit(limit)
        .totalRecords(campaigns.getTotalRecords())
        .build();

    return BaseResponse.success(response, "Lấy danh sách chiến dịch thành công");
  }

  /**
   * Lấy chi tiết giveaway campaign.
   *
   * @param id     ID của campaign
   * @param userId ID của user (từ header, tùy chọn)
   * @return Chi tiết campaign
   */
  @GetMapping("/giveaway-campaigns/{id}")
  public BaseResponse<GiveawayCampaignResponseDTO> getGiveawayCampaignById(
      @PathVariable @ValidId Long id,
      @RequestHeader(value = "X-User-Id", required = false) Long userId) {

    EventDTO campaign = giveawayCampaignService.getGiveawayCampaignById(id);
    GiveawayCampaignStatsDTO stats = giveawayCampaignService.getGiveawayCampaignStats(id);

    Boolean canParticipate = false;
    Boolean userParticipated = false;

    if (userId != null) {
      canParticipate = giveawayCampaignService.canUserParticipate(userId, id);
      userParticipated = giveawayCampaignService.hasUserParticipated(userId, id);
    }

    GiveawayCampaignResponseDTO response = GiveawayCampaignDTOMapper.toGiveawayCampaignResponseDTO(
        campaign, stats.getTotalToys(), stats.getAvailableToys(), stats.getTotalParticipants(),
        canParticipate, userParticipated, stats.getIsActive(), stats.getIsExpired());

    return BaseResponse.success(response, "Lấy chi tiết chiến dịch thành công");
  }

  /**
   * Cập nhật giveaway campaign.
   *
   * @param id         ID của campaign
   * @param requestDTO Thông tin cập nhật
   * @return Campaign đã được cập nhật
   */
  @PostMapping("/admin/giveaway-campaigns/{id}/update")
  public BaseResponse<GiveawayCampaignResponseDTO> updateGiveawayCampaign(
      @PathVariable @ValidId Long id,
      @Valid @RequestBody CreateGiveawayCampaignRequestDTO requestDTO) {

    EventDTO campaign = giveawayCampaignService.updateGiveawayCampaign(id,
        GiveawayCampaignDTOMapper.toCreateGiveawayCampaignRequest(requestDTO));

    return BaseResponse.success(
        GiveawayCampaignDTOMapper.toGiveawayCampaignResponseDTO(campaign),
        "Cập nhật chiến dịch thành công");
  }

  /**
   * Xóa giveaway campaign.
   *
   * @param id ID của campaign cần xóa
   * @return Thông báo thành công
   */
  @DeleteMapping("/admin/giveaway-campaigns/{id}")
  public BaseResponse<Void> deleteGiveawayCampaign(@PathVariable @ValidId Long id) {
    giveawayCampaignService.deleteGiveawayCampaign(id);
    return BaseResponse.success(null, "Xóa chiến dịch thành công");
  }

  /**
   * Thêm toys vào giveaway campaign.
   *
   * @param campaignId ID của campaign
   * @param requestDTO Danh sách toy IDs cần thêm
   * @return Số lượng toys đã được thêm thành công
   */
  @PostMapping("/admin/giveaway-campaigns/{campaignId}/toys")
  public BaseResponse<String> addToysToGiveawayCampaign(
      @PathVariable @ValidId Long campaignId,
      @Valid @RequestBody AddToysToGiveawayCampaignRequestDTO requestDTO) {

    int addedCount = giveawayCampaignService.addToysToGiveawayCampaign(campaignId,
        GiveawayCampaignDTOMapper.toAddToysToGiveawayCampaignRequest(requestDTO));

    return BaseResponse.success(
        "Đã thêm " + addedCount + " đồ chơi vào chiến dịch thành công",
        "Thêm đồ chơi vào chiến dịch thành công");
  }

  /**
   * Lấy thống kê của giveaway campaign.
   *
   * @param campaignId ID của campaign
   * @return Thống kê campaign
   */
  @GetMapping("/admin/giveaway-campaigns/{campaignId}/stats")
  public BaseResponse<GiveawayCampaignStatsResponseDTO> getGiveawayCampaignStats(
      @PathVariable @ValidId Long campaignId) {

    GiveawayCampaignStatsDTO stats = giveawayCampaignService.getGiveawayCampaignStats(campaignId);

    return BaseResponse.success(
        GiveawayCampaignDTOMapper.toGiveawayCampaignStatsResponseDTO(stats),
        "Lấy thống kê chiến dịch thành công");
  }

  /**
   * Lấy danh sách participations của user với phân trang.
   *
   * @param userId ID của user
   * @param page   Số trang (bắt đầu từ 1)
   * @param limit  Số lượng items per page
   * @return Danh sách participations với phân trang
   */
  @GetMapping("/users/{userId}/giveaway-participations")
  public BaseResponse<PageResponseDTO<ToyParticipationResponseDTO>> getUserParticipations(
      @PathVariable @ValidId Long userId,
      @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
      @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int limit) {

    PageResponse<ToyParticipationDTO> participations = giveawayCampaignService
        .getUserParticipations(userId, page, limit);

    PageResponseDTO<ToyParticipationResponseDTO> response = PageResponseDTO
        .<ToyParticipationResponseDTO>builder()
        .list(participations.getList().stream()
            .map(GiveawayCampaignDTOMapper::toToyParticipationResponseDTO)
            .toList())
        .limit(limit)
        .totalRecords(participations.getTotalRecords())
        .build();

    return BaseResponse.success(response, "Lấy lịch sử tham gia thành công");
  }
}