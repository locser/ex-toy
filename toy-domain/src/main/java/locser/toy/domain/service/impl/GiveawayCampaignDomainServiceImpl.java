package locser.toy.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.exception.ResourceNotFoundException;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.model.enums.EventStatus;
import locser.toy.domain.model.enums.EventType;
import locser.toy.domain.model.enums.ToyStatus;
import locser.toy.domain.repository.EventRepository;
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

    public GiveawayCampaignDomainServiceImpl(
            EventRepository eventRepository,
            ToyRepository toyRepository,
            ToyParticipationRepository toyParticipationRepository) {
        this.eventRepository = eventRepository;
        this.toyRepository = toyRepository;
        this.toyParticipationRepository = toyParticipationRepository;
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

        Event campaign = eventRepository.findByIdAndType(id, EventType.GIVEAWAY.getValue())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chiến dịch phát quà với ID: " + id));

        return campaign;
    }

    @Override
    public Event validateAndUpdateGiveawayCampaign(Event campaign) {
        // Xác thực dữ liệu
        validateCampaignDates(campaign);
        validateCampaignData(campaign);

        // Đảm bảo type vẫn là GIVEAWAY
        campaign.setType(EventType.GIVEAWAY.getValue());

        return campaign;
    }

    @Override
    public void deleteGiveawayCampaign(Long id) {
        // Kiểm tra ID phải lớn hơn 0
        IdValidator.validateId(id, "Campaign");

        Event campaign = getGiveawayCampaignById(id);

        // Kiểm tra campaign có thể xóa không
        if (campaign.getStatus().equals(EventStatus.ONGOING.getValue())) {
            throw new BadRequestException("Không thể xóa chiến dịch đang diễn ra");
        }

        // Soft delete: Chỉ đánh dấu là đã xóa
        campaign.setStatus(EventStatus.DELETED.getValue());
        eventRepository.save(campaign);

        // Cập nhật trạng thái toys về AVAILABLE
        toyRepository.updateStatusByCampaignId(id, 
            ToyStatus.GIVEAWAY_AVAILABLE.getValue(), 
            ToyStatus.AVAILABLE.getValue());
    }

    @Override
    public int addToysToGiveawayCampaign(Long campaignId, List<Long> toyIds) {
        // Validate inputs
        IdValidator.validateId(campaignId, "Campaign");
        if (toyIds == null || toyIds.isEmpty()) {
            throw new BadRequestException("Danh sách toys không được để trống");
        }

        // Validate campaign exists and is giveaway type
        Event campaign = getGiveawayCampaignById(campaignId);

        // Validate campaign status
        if (campaign.getStatus().equals(EventStatus.FINISHED.getValue()) ||
            campaign.getStatus().equals(EventStatus.DELETED.getValue())) {
            throw new BadRequestException("Không thể thêm toys vào chiến dịch đã kết thúc hoặc đã xóa");
        }

        // Validate all toys exist and are available
        for (Long toyId : toyIds) {
            IdValidator.validateId(toyId, "Toy");
            Toy toy = toyRepository.findOneById(toyId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đồ chơi với ID: " + toyId));
            
            if (!toy.getStatus().equals(ToyStatus.AVAILABLE.getValue())) {
                throw new BadRequestException("Đồ chơi ID " + toyId + " không khả dụng để thêm vào chiến dịch");
            }
        }

        // Batch update toys
        return toyRepository.addToysToCampaign(toyIds, campaignId, ToyStatus.GIVEAWAY_AVAILABLE.getValue());
    }

    @Override
    public List<Toy> getAvailableToysInCampaign(Long campaignId) {
        IdValidator.validateId(campaignId, "Campaign");
        return toyRepository.findByCampaignIdAndStatus(campaignId, ToyStatus.GIVEAWAY_AVAILABLE.getValue());
    }

    @Override
    public long countAvailableToysInCampaign(Long campaignId) {
        IdValidator.validateId(campaignId, "Campaign");
        return toyRepository.countByCampaignIdAndStatus(campaignId, ToyStatus.GIVEAWAY_AVAILABLE.getValue());
    }

    @Override
    public long countTotalToysInCampaign(Long campaignId) {
        IdValidator.validateId(campaignId, "Campaign");
        long available = toyRepository.countByCampaignIdAndStatus(campaignId, ToyStatus.GIVEAWAY_AVAILABLE.getValue());
        long claimed = toyRepository.countByCampaignIdAndStatus(campaignId, ToyStatus.GIVEAWAY_CLAIMED.getValue());
        return available + claimed;
    }

    @Override
    public boolean canParticipate(Long userId, Long campaignId) {
        IdValidator.validateId(userId, "User");
        IdValidator.validateId(campaignId, "Campaign");

        // Check if user already participated
        if (hasUserParticipated(userId, campaignId)) {
            return false;
        }

        // Check if campaign is active
        Event campaign = getGiveawayCampaignById(campaignId);
        if (!isCampaignActive(campaign)) {
            return false;
        }

        // Check if there are available toys
        return countAvailableToysInCampaign(campaignId) > 0;
    }

    @Override
    public boolean hasUserParticipated(Long userId, Long campaignId) {
        IdValidator.validateId(userId, "User");
        IdValidator.validateId(campaignId, "Campaign");
        return toyParticipationRepository.existsByUserIdAndCampaignId(userId, campaignId);
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
    public boolean isCampaignActive(Event campaign) {
        if (campaign == null) {
            return false;
        }

        // Check status
        if (!campaign.getStatus().equals(EventStatus.ONGOING.getValue())) {
            return false;
        }

        // Check dates
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(campaign.getStartDate()) && !now.isAfter(campaign.getEndDate());
    }

    @Override
    public boolean isCampaignExpired(Event campaign) {
        if (campaign == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(campaign.getEndDate());
    }

    /**
     * Xác thực ngày bắt đầu và kết thúc của campaign.
     */
    private void validateCampaignDates(Event campaign) {
        if (campaign.getStartDate() != null && campaign.getEndDate() != null) {
            if (campaign.getEndDate().isBefore(campaign.getStartDate())) {
                throw new BadRequestException("Ngày kết thúc không thể trước ngày bắt đầu");
            }
        }
    }

    /**
     * Xác thực dữ liệu campaign.
     */
    private void validateCampaignData(Event campaign) {
        if (campaign.getName() == null || campaign.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên chiến dịch không được để trống");
        }
    }
}
