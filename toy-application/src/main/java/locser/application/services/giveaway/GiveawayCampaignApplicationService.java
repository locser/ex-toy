package locser.application.services.giveaway;

import java.util.List;

import locser.toy.domain.model.dto.AddToysToGiveawayCampaignRequest;
import locser.toy.domain.model.dto.CreateGiveawayCampaignRequest;
import locser.toy.domain.model.dto.EventDTO;
import locser.toy.domain.model.dto.GiveawayCampaignStatsDTO;
import locser.toy.domain.model.dto.ToyParticipationDTO;
import locser.toy.domain.model.entity.Toy;
import locser.util.PageResponse;

/**
 * Interface định nghĩa các dịch vụ ứng dụng cho Giveaway Campaign.
 */
public interface GiveawayCampaignApplicationService {

        /**
         * Tạo mới một giveaway campaign.
         *
         * @param request Thông tin campaign cần tạo
         * @return Campaign đã được tạo
         */
        EventDTO createGiveawayCampaign(CreateGiveawayCampaignRequest request);

        /**
         * Lấy giveaway campaign theo ID.
         *
         * @param id ID của campaign
         * @return Campaign details
         */
        EventDTO getGiveawayCampaignById(Long id);

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
        PageResponse<EventDTO> getGiveawayCampaignsWithPagination(
                        int page, int limit, Integer status, String sortBy, String sortDirection);

        /**
         * Cập nhật giveaway campaign.
         *
         * @param id      ID của campaign
         * @param request Thông tin cập nhật
         * @return Campaign đã được cập nhật
         */
        EventDTO updateGiveawayCampaign(Long id, CreateGiveawayCampaignRequest request);

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
         * @param request    Danh sách toy IDs cần thêm
         * @return Số lượng toys đã được thêm thành công
         */
        int addToysToGiveawayCampaign(Long campaignId, AddToysToGiveawayCampaignRequest request);

        /**
         * Lấy thống kê của giveaway campaign.
         *
         * @param campaignId ID của campaign
         * @return Thống kê campaign
         */
        GiveawayCampaignStatsDTO getGiveawayCampaignStats(Long campaignId);

        /**
         * Lấy danh sách participations của user với phân trang.
         *
         * @param userId ID của user
         * @param page   Số trang (bắt đầu từ 1)
         * @param limit  Số lượng items per page
         * @return Danh sách participations với phân trang
         */
        PageResponse<ToyParticipationDTO> getUserParticipations(Long userId, int page, int limit);

        /**
         * Kiểm tra user có thể tham gia campaign không.
         *
         * @param userId     ID của user
         * @param campaignId ID của campaign
         * @return true nếu có thể tham gia, false nếu không
         */
        int canUserParticipate(Long userId, Long campaignId);

        /**
         * Kiểm tra user đã tham gia campaign chưa.
         *
         * @param userId     ID của user
         * @param campaignId ID của campaign
         * @return true nếu đã tham gia, false nếu chưa
         */
        int hasUserParticipated(Long userId, Long campaignId);

        /**
         * User tham gia giveaway campaign để nhận toy miễn phí.
         * Hỗ trợ cả 3 levels: Basic, Optimized, Advanced.
         *
         * @param userId      ID của user
         * @param campaignId  ID của campaign
         * @param level       Performance level (1=Basic, 2=Optimized, 3=Advanced)
         * @param preferences User preferences (optional, for level 3)
         * @return ToyParticipation đã được tạo
         */
        ToyParticipationDTO participateInGiveaway(Long userId, Long campaignId,
                        Integer level, String preferences);

        List<Toy> getToysInCampaign(Long campaignId, Integer status, Long userId, String name,
                        Long toyCondition, int page, int limit);

        Long claim10kGiveaway(Long userId, Long campaignId);
}