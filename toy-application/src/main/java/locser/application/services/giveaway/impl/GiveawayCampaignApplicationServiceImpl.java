package locser.application.services.giveaway.impl;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import locser.application.services.giveaway.GiveawayCampaignApplicationService;
import locser.toy.domain.model.dto.AddToysToGiveawayCampaignRequest;
import locser.toy.domain.model.dto.CreateGiveawayCampaignRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.GiveawayCampaignStatsDTO;
import locser.toy.domain.model.dto.ToyParticipationDTO;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.model.enums.EventType;
import locser.toy.domain.repository.EventRepository;
import locser.toy.domain.service.GiveawayCampaignDomainService;
import locser.toy.domain.specifications.ToySpecification;
import locser.util.PageResponse;
import locser.toy.domain.service.DomainEventPublisher;
import locser.toy.domain.model.event.ParticipationCreatedEvent;
import locser.toy.domain.model.enums.ToyParticipationStatus;

/**
 * Triển khai các dịch vụ ứng dụng cho Giveaway Campaign.
 */
@Service
public class GiveawayCampaignApplicationServiceImpl implements GiveawayCampaignApplicationService {

  private final GiveawayCampaignDomainService giveawayCampaignDomainService;
  private final EventRepository eventRepository;
  private final ToySpecification toySpecification;
  private final DomainEventPublisher domainEventPublisher;

  public GiveawayCampaignApplicationServiceImpl(
      GiveawayCampaignDomainService giveawayCampaignDomainService,
      EventRepository eventRepository,
      ToySpecification toySpecification,
      DomainEventPublisher domainEventPublisher) {
    this.giveawayCampaignDomainService = giveawayCampaignDomainService;
    this.eventRepository = eventRepository;
    this.toySpecification = toySpecification;
    this.domainEventPublisher = domainEventPublisher;
  }

  @Override
  public EventDTO createGiveawayCampaign(CreateGiveawayCampaignRequest request) {
    // Tạo entity từ request
    Event campaign = new Event();
    campaign.setName(request.getName());
    campaign.setDescription(request.getDescription());
    campaign.setStartDate(request.getStartDate());
    campaign.setEndDate(request.getEndDate());
    campaign.setTheme(request.getTheme());
    campaign.setRules(new String(request.getRules()));
    campaign.setTotalToys(request.getTotalToys());
    campaign.setAvailableToys(0);

    // Gọi domain service để xử lý logic nghiệp vụ
    campaign = giveawayCampaignDomainService.initializeNewGiveawayCampaign(campaign);
    System.out.println(campaign);

    // Lưu vào repository
    Event savedCampaign = eventRepository.save(campaign);

    // Chuyển đổi và trả về DTO
    return mapToEventDTO(savedCampaign);
  }

  @Override
  public EventDTO getGiveawayCampaignById(Long id) {
    Event campaign = giveawayCampaignDomainService.getGiveawayCampaignById(id);
    return mapToEventDTO(campaign);
  }

  @Override
  public PageResponse<EventDTO> getGiveawayCampaignsWithPagination(
      int page, int limit, Integer status, String sortBy, String sortDirection) {

    // Convert page from 1-based to 0-based
    int pageIndex = Math.max(0, page - 1);

    List<Event> campaigns = eventRepository.findByTypeWithPagination(
        EventType.GIVEAWAY.getValue(), pageIndex, limit, status, sortBy, sortDirection);

    List<EventDTO> campaignDTOs = campaigns.stream()
        .map(this::mapToEventDTO)
        .collect(Collectors.toList());

    long total = eventRepository.countByType(EventType.GIVEAWAY.getValue(), status);

    return new PageResponse<>(campaignDTOs, limit, total);
  }

  @Override
  public EventDTO updateGiveawayCampaign(Long id, CreateGiveawayCampaignRequest request) {
    // Lấy campaign hiện tại
    Event existingCampaign = giveawayCampaignDomainService.getGiveawayCampaignById(id);

    // Cập nhật thông tin
    existingCampaign.setName(request.getName());
    existingCampaign.setDescription(request.getDescription());
    existingCampaign.setStartDate(request.getStartDate());
    existingCampaign.setEndDate(request.getEndDate());
    existingCampaign.setTheme(request.getTheme());
    existingCampaign.setRules(request.getRules());

    // Validate và update
    existingCampaign = giveawayCampaignDomainService.validateAndUpdateGiveawayCampaign(
        existingCampaign);

    // Lưu vào repository
    Event updatedCampaign = eventRepository.save(existingCampaign);

    return mapToEventDTO(updatedCampaign);
  }

  @Override
  public void deleteGiveawayCampaign(Long id) {
    giveawayCampaignDomainService.deleteGiveawayCampaign(id);
  }

  @Override
  public int addToysToGiveawayCampaign(Long campaignId, AddToysToGiveawayCampaignRequest request) {
    return giveawayCampaignDomainService.addToysToGiveawayCampaign(campaignId, request.getToyIds());
  }

  @Override
  public GiveawayCampaignStatsDTO getGiveawayCampaignStats(Long campaignId) {
    Event campaign = giveawayCampaignDomainService.getGiveawayCampaignById(campaignId,
        EventType.GIVEAWAY.getValue(), EventStatus.ONGOING.getValue());

    long totalToys = giveawayCampaignDomainService.countTotalToysInCampaign(campaignId);
    long availableToys = campaign.getAvailableToys();
    long claimedToys = totalToys - availableToys;
    long totalParticipants = giveawayCampaignDomainService.countCampaignParticipations(campaignId);

    double participationRate = totalToys > 0 ? (double) claimedToys / totalToys * 100 : 0.0;

    return GiveawayCampaignStatsDTO.builder()
        .id(campaignId)
        .name(campaign.getName())
        .totalToys(totalToys)
        .availableToys(availableToys)
        .claimedToys(claimedToys)
        .totalParticipants(totalParticipants)
        .participationRate(participationRate)
        .isActive(giveawayCampaignDomainService.isCampaignActive(campaign))
        .isExpired(giveawayCampaignDomainService.isCampaignExpired(campaign))
        .build();
  }

  @Override
  public PageResponse<ToyParticipationDTO> getUserParticipations(Long userId, int page, int limit) {
    // Convert page from 1-based to 0-based
    int pageIndex = Math.max(0, page - 1);

    List<ToyParticipation> participations = giveawayCampaignDomainService
        .getUserParticipations(userId, pageIndex, limit);

    List<ToyParticipationDTO> participationDTOs = participations.stream()
        .map(this::mapToToyParticipationDTO)
        .collect(Collectors.toList());

    long total = giveawayCampaignDomainService.countUserParticipations(userId);

    return new PageResponse<>(participationDTOs, limit, total);
  }

  @Override
  public int canUserParticipate(Long userId, Long campaignId) {
    return giveawayCampaignDomainService.canParticipate(userId, campaignId);
  }

  @Override
  public int hasUserParticipated(Long userId, Long campaignId) {
    return giveawayCampaignDomainService.hasUserParticipated(userId, campaignId);
  }

  @Override
  @Transactional
  public ToyParticipationDTO participateInGiveaway(Long userId, Long campaignId,
      Integer level, String preferences) {
    // Validate level parameter
    if (level == null || level < 1 || level > 3) {
      throw new IllegalArgumentException("Level phải từ 1 đến 3");
    }

    ToyParticipation participation;

    // Delegate to appropriate domain service method based on level
    switch (level) {
      case 1:
        participation = giveawayCampaignDomainService.participateInGiveaway(userId, campaignId);
        break;
      case 2:
        participation = giveawayCampaignDomainService.participateInGiveawayOptimized(userId, campaignId);
        break;
      case 3:
        participation = giveawayCampaignDomainService.participateInGiveawayAdvanced(
            userId, campaignId, preferences);
        break;
      default:
        throw new IllegalArgumentException("Level không hợp lệ: " + level);
    }

    // Convert to DTO and return
    return mapToToyParticipationDTO(participation);
  }

  /**
   * Chuyển đổi Event entity thành EventDTO.
   */
  private EventDTO mapToEventDTO(Event event) {
    return EventDTO.builder()
        .id(event.getId())
        .name(event.getName())
        .description(event.getDescription())
        .startDate(event.getStartDate())
        .endDate(event.getEndDate())
        .theme(event.getTheme())
        .rules(event.getRules())
        .status(event.getStatus())
        .createdAt(event.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())
        .updatedAt(event.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant())
        .build();
  }

  /**
   * Chuyển đổi ToyParticipation entity thành ToyParticipationDTO.
   */
  private ToyParticipationDTO mapToToyParticipationDTO(ToyParticipation participation) {
    return ToyParticipationDTO.builder()
        .id(participation.getId())
        .userId(participation.getUserId())
        .toyId(participation.getToyId())
        .campaignId(participation.getCampaignId())
        .participationDate(participation.getParticipationDate())
        .status(participation.getStatus())
        .createdAt(participation.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())
        .updatedAt(participation.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant())
        .build();
  }

  @Override
  public List<Toy> getToysInCampaign(Long campaignId, Integer status, Long userId, String name,
      Long toyCondition, int page, int limit) {

    Specification<Toy> specification = toySpecification.byFieldId("campaignId", campaignId)
        .and(toySpecification.byFieldId("userId", userId))
        .and(toySpecification.byStatus(status))
        .and(toySpecification.byNameLike(name))
        .and(toySpecification.byToyCondition(toyCondition));

    Pageable pageable = PageRequest.of(page - 1, limit);

    return giveawayCampaignDomainService.getToysInCampaign(specification, pageable);
  }

  @Override
  @Transactional
  public Long claim10kGiveaway(Long userId, Long campaignId) {
    // Call domain service to handle the core business logic
    Long toyId = giveawayCampaignDomainService.claim10kGiveaway(userId, campaignId);
    
    // Publish domain event at application layer (following DDD principles)
    ParticipationCreatedEvent participationEvent = new ParticipationCreatedEvent(
        toyId,
        userId,
        campaignId,
        ToyParticipationStatus.CLAIMED.getValue()
    );
    
    domainEventPublisher.publish(participationEvent);
    
    return toyId;
  }

}