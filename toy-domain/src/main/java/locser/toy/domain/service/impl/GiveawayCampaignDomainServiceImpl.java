package locser.toy.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.model.enums.EventType;
import locser.toy.domain.model.enums.ToyStatus;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.repository.GiveawayCampaignRepository;
import locser.toy.domain.repository.ToyParticipationRepository;
import locser.toy.domain.repository.ToyRepository;
import locser.toy.domain.service.GiveawayCampaignDomainService;
import locser.toy.domain.validation.IdValidator;

/**
 * Triển khai các dịch vụ miền cho Giveaway Campaign.
 */
@Service
public class GiveawayCampaignDomainServiceImpl implements GiveawayCampaignDomainService {

  private final EventRepository eventRepository;
  private final ToyRepository toyRepository;
  private final ToyParticipationRepository toyParticipationRepository;
  private final GiveawayCampaignRepository giveawayCampaignRepository;

  public GiveawayCampaignDomainServiceImpl(EventRepository eventRepository,
      ToyRepository toyRepository,
      ToyParticipationRepository toyParticipationRepository,
      GiveawayCampaignRepository giveawayCampaignRepository) {
    this.eventRepository = eventRepository;
    this.toyRepository = toyRepository;
    this.toyParticipationRepository = toyParticipationRepository;
    this.giveawayCampaignRepository = giveawayCampaignRepository;
  }

  @Override
  public Event initializeNewGiveawayCampaign(Event campaign) {
    // Thiết lập các giá trị mặc định
    campaign.setType(EventType.GIVEAWAY.getValue());
    campaign.setStatus(EventStatus.UPCOMING.getValue());

    // Xác thực dữ liệu
    validateCampaignDates(campaign);
    validateCampaignData(campaign);

    return campaign;
  }

  @Override
  public Event getGiveawayCampaignById(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Campaign");

    Optional<Event> campaign = giveawayCampaignRepository.findById(id);
    if (campaign.isEmpty()) {
      throw new BadRequestException("Không tìm thấy chiến dịch");
    }
    Event event = campaign.get();
    if (event.getType() != EventType.GIVEAWAY.getValue()) {
      throw new BadRequestException("Chiến dịch không phải là giveaway campaign");
    }
    return event;
  }

  @Override
  public Event validateAndUpdateGiveawayCampaign(Event campaign) {
    // Xác thực dữ liệu
    validateCampaignDates(campaign);
    validateCampaignData(campaign);

    return campaign;
  }

  @Override
  public void deleteGiveawayCampaign(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Campaign");

    Event campaign = getGiveawayCampaignById(id);

    // Soft delete: Chỉ đánh dấu là đã xóa
    campaign.setStatus(EventStatus.DELETED.getValue());
    giveawayCampaignRepository.save(campaign);
  }

  @Override
  @Transactional
  public int addToysToGiveawayCampaign(Long campaignId, List<Long> toyIds) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(campaignId, "Campaign");

    // Lấy thông tin campaign
    Event campaign = getGiveawayCampaignById(campaignId);

    // Kiểm tra trạng thái campaign
    if (campaign.getStatus() != EventStatus.UPCOMING.getValue()) {
      throw new BadRequestException("Không thể thêm toys vào campaign đã bắt đầu hoặc kết thúc");
    }

    // Thêm toys vào campaign
    int addedCount = toyRepository.addToysToCampaign(toyIds, campaignId);
    if (addedCount > 0) {
      // Cập nhật số lượng toys trong campaign
      giveawayCampaignRepository.updateAvailableToys(campaignId, addedCount);
    }

    return addedCount;
  }

  @Override
  public List<Toy> getAvailableToysInCampaign(Long campaignId) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(campaignId, "Campaign");

    // // Lấy thông tin campaign
    // Event campaign = getGiveawayCampaignById(campaignId);

    // // Kiểm tra trạng thái campaign
    // if (campaign.getStatus() != EventStatus.ONGOING.getValue()) {
    // throw new BadRequestException("Campaign chưa bắt đầu hoặc đã kết thúc");
    // }

    return giveawayCampaignRepository.findAvailableToys(campaignId);
  }

  @Override
  @Transactional
  public ToyParticipation participateInGiveaway(Long userId, Long campaignId) {
    return participateInGiveawayCampaign(userId, campaignId);
  }

  @Override
  @Transactional
  public ToyParticipation participateInGiveawayOptimized(Long userId, Long campaignId) {
    return participateInGiveawayCampaign(userId, campaignId);
  }

  @Override
  @Transactional
  public ToyParticipation participateInGiveawayAdvanced(Long userId, Long campaignId,
      String preferences) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(userId, "User");
    IdValidator.validateId(campaignId, "Campaign");

    // Lấy thông tin campaign
    Event campaign = getGiveawayCampaignById(campaignId);

    // Validate điều kiện tham gia
    validateParticipationEligibility(userId, campaignId, campaign);

    // Kiểm tra user đã tham gia chưa
    if (giveawayCampaignRepository.hasUserParticipated(userId, campaignId)) {
      throw new BadRequestException("Bạn đã tham gia campaign này");
    }

    // Lấy danh sách toys có sẵn
    List<Toy> availableToys = getAvailableToysInCampaign(campaignId);
    if (availableToys.isEmpty()) {
      throw new BadRequestException("Không còn đồ chơi trong chiến dịch");
    }

    // Chọn toy dựa trên preferences
    Toy selectedToy = selectToyWithPreferences(campaignId, userId, preferences);

    // Tạo bản ghi tham gia
    ToyParticipation participation = new ToyParticipation();
    participation.setUserId(userId);
    participation.setCampaignId(campaignId);
    participation.setToyId(selectedToy.getId());
    participation.setStatus(ToyStatus.GIVEAWAY_CLAIMED.getValue());
    return giveawayCampaignRepository.saveParticipation(participation);
  }

  private ToyParticipation participateInGiveawayCampaign(Long userId, Long campaignId) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(userId, "User");
    IdValidator.validateId(campaignId, "Campaign");

    // Lấy thông tin campaign
    Event campaign = getGiveawayCampaignById(campaignId);

    // Validate điều kiện tham gia
    validateParticipationEligibility(userId, campaignId, campaign);

    // Kiểm tra user đã tham gia chưa
    if (giveawayCampaignRepository.hasUserParticipated(userId, campaignId)) {
      throw new BadRequestException("Bạn đã tham gia campaign này");
    }

    // Lấy danh sách toys có sẵn
    List<Toy> availableToys = getAvailableToysInCampaign(campaignId);
    if (availableToys.isEmpty()) {
      throw new BadRequestException("Không còn toys trong campaign");
    }

    // Chọn ngẫu nhiên một toy
    Toy selectedToy = selectRandomToy(campaignId);

    // Tạo bản ghi tham gia
    ToyParticipation participation = new ToyParticipation();
    participation.setUserId(userId);
    participation.setCampaignId(campaignId);
    participation.setToyId(selectedToy.getId());
    participation.setStatus(ToyStatus.GIVEAWAY_CLAIMED.getValue());
    return giveawayCampaignRepository.saveParticipation(participation);
  }

  @Override
  public Toy selectRandomToy(Long campaignId) {
    return toyRepository.findRandomAvailableToyInCampaign(campaignId);
  }

  @Override
  public Toy selectToyWithPreferences(Long campaignId, Long userId, String preferences) {
    // For now, fallback to random selection
    return selectRandomToy(campaignId);
  }

  @Override
  public void validateParticipationEligibility(Long userId, Long campaignId, Event campaign) {
    // Kiểm tra trạng thái campaign
    if (campaign.getStatus() != EventStatus.ONGOING.getValue()) {
      throw new BadRequestException("Campaign chưa bắt đầu hoặc đã kết thúc");
    }

    // Kiểm tra user đã tham gia chưa
    if (giveawayCampaignRepository.hasUserParticipated(userId, campaignId)) {
      throw new BadRequestException("Bạn đã tham gia campaign này");
    }

    // Kiểm tra còn toys không
    if (campaign.getAvailableToys() <= 0) {
      throw new BadRequestException("Không còn toys trong campaign");
    }
  }

  @Override
  public List<Toy> getToysInCampaign(Specification<Toy> specification) {
    return toyRepository.findAll(specification);
  }

  @Override
  public List<Toy> getToysInCampaign(Specification<Toy> specification, Pageable pageable) {
    return toyRepository.findAll(specification, pageable);
  }

  private void validateCampaignDates(Event campaign) {
    LocalDateTime now = LocalDateTime.now();
    if (campaign.getStartDate() == null || campaign.getEndDate() == null) {
      throw new BadRequestException("Ngày bắt đầu và kết thúc không được để trống");
    }
    if (campaign.getStartDate().isBefore(now)) {
      throw new BadRequestException("Ngày bắt đầu phải sau thời điểm hiện tại");
    }
    if (campaign.getEndDate().isBefore(campaign.getStartDate())) {
      throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu");
    }
  }

  private void validateCampaignData(Event campaign) {
    if (campaign.getName() == null || campaign.getName().trim().isEmpty()) {
      throw new BadRequestException("Tên campaign không được để trống");
    }
    if (campaign.getDescription() == null || campaign.getDescription().trim().isEmpty()) {
      throw new BadRequestException("Mô tả campaign không được để trống");
    }
    if (campaign.getTheme() == null || campaign.getTheme().trim().isEmpty()) {
      throw new BadRequestException("Chủ đề campaign không được để trống");
    }
    if (campaign.getRules() == null || campaign.getRules().trim().isEmpty()) {
      throw new BadRequestException("Quy tắc campaign không được để trống");
    }
    if (campaign.getTotalToys() == null || campaign.getTotalToys() <= 0) {
      throw new BadRequestException("Tổng số toys phải lớn hơn 0");
    }
  }

  @Override
  public long countAvailableToysInCampaign(Long campaignId) {
    IdValidator.validateId(campaignId, "Campaign");
    return toyRepository.countByCampaignIdAndStatus(campaignId,
        ToyStatus.GIVEAWAY_AVAILABLE.getValue());
  }

  @Override
  public long countTotalToysInCampaign(Long campaignId) {
    IdValidator.validateId(campaignId, "Campaign");
    long available = toyRepository.countByCampaignIdAndStatus(campaignId,
        ToyStatus.GIVEAWAY_AVAILABLE.getValue());
    long claimed = toyRepository.countByCampaignIdAndStatus(campaignId,
        ToyStatus.GIVEAWAY_CLAIMED.getValue());
    return available + claimed;
  }

  @Override
  public int canParticipate(Long userId, Long campaignId) {
    IdValidator.validateId(userId, "User");
    IdValidator.validateId(campaignId, "Campaign");

    // Check if user already participated using Redis cache
    if (giveawayCampaignRepository.hasUserParticipated(userId, campaignId)) {
      return 0;
    }

    // Check if campaign is active
    Event campaign = getGiveawayCampaignById(campaignId);
    if (isCampaignActive(campaign) == 0) {
      return 0;
    }

    // Check if there are available toys
    return campaign.getAvailableToys() > 0 ? 1 : 0;
  }

  @Override
  public int hasUserParticipated(Long userId, Long campaignId) {
    IdValidator.validateId(userId, "User");
    IdValidator.validateId(campaignId, "Campaign");
    return toyParticipationRepository.existsByUserIdAndCampaignId(userId, campaignId) ? 1 : 0;
  }

  @Override
  public List<ToyParticipation> getUserParticipations(Long userId, int page, int limit) {
    IdValidator.validateId(userId, "User");
    return toyParticipationRepository.findByUserId(userId, page, limit);
  }

  @Override
  public long countUserParticipations(Long userId) {
    IdValidator.validateId(userId, "User");
    return toyParticipationRepository.countByUserId(userId);
  }

  @Override
  public List<ToyParticipation> getCampaignParticipations(Long campaignId) {
    IdValidator.validateId(campaignId, "Campaign");
    return toyParticipationRepository.findByCampaignId(campaignId);
  }

  @Override
  public long countCampaignParticipations(Long campaignId) {
    IdValidator.validateId(campaignId, "Campaign");
    return toyParticipationRepository.countByCampaignId(campaignId);
  }

  @Override
  public void updateCampaignStatus(Long campaignId, Integer status) {
    IdValidator.validateId(campaignId, "Campaign");

    Event campaign = getGiveawayCampaignById(campaignId);
    campaign.setStatus(status);
    eventRepository.save(campaign);
  }

  @Override
  public int isCampaignActive(Event campaign) {
    if (campaign == null) {
      return 0;
    }

    // Check status
    if (!campaign.getStatus().equals(EventStatus.ONGOING.getValue())) {
      return 0;
    }

    // Check dates
    LocalDateTime now = LocalDateTime.now();
    if (!now.isBefore(campaign.getStartDate()) && !now.isAfter(campaign.getEndDate())) {
      return 1;
    }

    return 0;
  }

  @Override
  public int isCampaignExpired(Event campaign) {
    if (campaign == null) {
      return 0;
    }

    // Check status
    if (campaign.getStatus().equals(EventStatus.FINISHED.getValue())) {
      return 1;
    }

    // Check dates
    LocalDateTime now = LocalDateTime.now();
    if (now.isAfter(campaign.getEndDate())) {
      return 1;
    }

    return 0;
  }
}