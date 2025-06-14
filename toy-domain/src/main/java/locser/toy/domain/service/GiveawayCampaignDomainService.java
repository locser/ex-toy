package locser.toy.domain.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import locser.toy.domain.exception.ResourceNotFoundException;
import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.ToyParticipation;

/**
 * Interface định nghĩa các dịch vụ miền cho Giveaway Campaign.
 */
public interface GiveawayCampaignDomainService {

    /**
     * Khởi tạo một giveaway campaign mới với các giá trị mặc định.
     *
     * @param campaign Campaign cần khởi tạo
     * @return Campaign đã được khởi tạo
     */
    Event initializeNewGiveawayCampaign(Event campaign);

    /**
     * Lấy giveaway campaign theo ID.
     *
     * @param id ID của campaign
     * @return Campaign
     * @throws ResourceNotFoundException nếu không tìm thấy hoặc không phải giveaway
     *                                   campaign
     */
    Event getGiveawayCampaignById(Long id);

    /**
     * Xác thực và cập nhật giveaway campaign.
     *
     * @param campaign Campaign cần cập nhật
     * @return Campaign đã được cập nhật
     */
    Event validateAndUpdateGiveawayCampaign(Event campaign);

    /**
     * Xóa giveaway campaign.
     *
     * @param id ID của campaign cần xóa
     */
    void deleteGiveawayCampaign(Long id);

    /**
     * Thêm toys vào giveaway campaign.
     *
     * @param campaignId ID của campaign
     * @param toyIds     Danh sách ID của toys cần thêm
     * @return Số lượng toys đã được thêm thành công
     */
    int addToysToGiveawayCampaign(Long campaignId, List<Long> toyIds);

    /**
     * Lấy danh sách toys có sẵn trong giveaway campaign.
     *
     * @param campaignId ID của campaign
     * @return Danh sách toys có sẵn
     */
    List<Toy> getAvailableToysInCampaign(Long campaignId);

    /**
     * Đếm số lượng toys có sẵn trong campaign.
     *
     * @param campaignId ID của campaign
     * @return Số lượng toys có sẵn
     */
    long countAvailableToysInCampaign(Long campaignId);

    /**
     * Đếm tổng số toys trong campaign.
     *
     * @param campaignId ID của campaign
     * @return Tổng số toys trong campaign
     */
    long countTotalToysInCampaign(Long campaignId);

    /**
     * Kiểm tra user có thể tham gia campaign không.
     *
     * @param userId     ID của user
     * @param campaignId ID của campaign
     * @return true nếu có thể tham gia, false nếu không
     */
    int canParticipate(Long userId, Long campaignId);

    /**
     * Kiểm tra user đã tham gia campaign chưa.
     *
     * @param userId     ID của user
     * @param campaignId ID của campaign
     * @return true nếu đã tham gia, false nếu chưa
     */
    int hasUserParticipated(Long userId, Long campaignId);

    /**
     * Lấy danh sách participations của user.
     *
     * @param userId ID của user
     * @param page   Số trang (bắt đầu từ 0)
     * @param limit  Số lượng items per page
     * @return Danh sách participations
     */
    List<ToyParticipation> getUserParticipations(Long userId, int page, int limit);

    /**
     * Đếm số lượng participations của user.
     *
     * @param userId ID của user
     * @return Số lượng participations
     */
    long countUserParticipations(Long userId);

    /**
     * Lấy danh sách participations của campaign.
     *
     * @param campaignId ID của campaign
     * @return Danh sách participations
     */
    List<ToyParticipation> getCampaignParticipations(Long campaignId);

    /**
     * Đếm số lượng participations của campaign.
     *
     * @param campaignId ID của campaign
     * @return Số lượng participations
     */
    long countCampaignParticipations(Long campaignId);

    /**
     * Cập nhật trạng thái campaign.
     *
     * @param campaignId ID của campaign
     * @param status     Trạng thái mới
     */
    void updateCampaignStatus(Long campaignId, Integer status);

    /**
     * Kiểm tra campaign có đang active không.
     *
     * @param campaign Campaign cần kiểm tra
     * @return true nếu active, false nếu không
     */
    int isCampaignActive(Event campaign);

    /**
     * Kiểm tra campaign có hết hạn không.
     *
     * @param campaign Campaign cần kiểm tra
     * @return true nếu hết hạn, false nếu chưa
     */
    int isCampaignExpired(Event campaign);

    /**
     * User tham gia giveaway campaign - Level 1 (Basic).
     * Thực hiện atomic transaction để claim toy.
     *
     * @param userId     ID của user
     * @param campaignId ID của campaign
     * @return ToyParticipation đã được tạo
     */
    ToyParticipation participateInGiveaway(Long userId, Long campaignId);

    /**
     * User tham gia giveaway campaign - Level 2 (Optimized).
     * Sử dụng cache và optimistic locking.
     *
     * @param userId     ID của user
     * @param campaignId ID của campaign
     * @return ToyParticipation đã được tạo
     */
    ToyParticipation participateInGiveawayOptimized(Long userId, Long campaignId);

    /**
     * User tham gia giveaway campaign - Level 3 (Advanced).
     * Sử dụng distributed locking và smart toy selection.
     *
     * @param userId      ID của user
     * @param campaignId  ID của campaign
     * @param preferences Preferences của user (optional)
     * @return ToyParticipation đã được tạo
     */
    ToyParticipation participateInGiveawayAdvanced(Long userId, Long campaignId,
            String preferences);

    /**
     * Chọn toy ngẫu nhiên từ danh sách available toys.
     *
     * @param campaignId ID của campaign
     * @return Toy được chọn
     */
    Toy selectRandomToy(Long campaignId);

    /**
     * Chọn toy thông minh dựa trên preferences và fairness algorithm.
     *
     * @param campaignId  ID của campaign
     * @param userId      ID của user
     * @param preferences Preferences của user
     * @return Toy được chọn
     */
    Toy selectToyWithPreferences(Long campaignId, Long userId, String preferences);

    /**
     * Validate điều kiện tham gia campaign.
     *
     * @param userId     ID của user
     * @param campaignId ID của campaign
     * @param campaign   Campaign entity
     */
    void validateParticipationEligibility(Long userId, Long campaignId, Event campaign);

    List<Toy> getToysInCampaign(Specification<Toy> specification);

    List<Toy> getToysInCampaign(Specification<Toy> specification, Pageable pageable);
}
