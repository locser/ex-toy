package locser.application.services.toy;

import java.util.List;
import locser.toy.domain.model.dto.CreateToyRequest;
import locser.toy.domain.model.dto.ToyDTO;
import locser.toy.domain.model.dto.UpdateToyRequest;
import locser.toy.domain.model.entity.Toy;
import locser.util.PageResponse;

/**
 * Lớp dịch vụ ứng dụng cho Toy, điều phối các use case.
 */
public interface ToyApplicationService {

  /**
   * Chuyển đổi từ Entity sang DTO.
   */
  ToyDTO mapToDTO(Toy toy);

  /**
   * Tạo mới đồ chơi.
   *
   * @param userId  ID của người dùng
   * @param request Thông tin đồ chơi cần tạo
   * @return DTO của đồ chơi đã tạo
   */
  ToyDTO createToy(Long userId, CreateToyRequest request);

  /**
   * Lấy danh sách tất cả đồ chơi.
   *
   * @param status Trạng thái đồ chơi (tùy chọn)
   * @return Danh sách DTO của đồ chơi
   */
  List<ToyDTO> getAllToys(Integer status);

  /**
   * Lấy danh sách đồ chơi của người dùng.
   *
   * @param userId ID của người dùng
   * @param status Trạng thái đồ chơi (tùy chọn)
   * @return Danh sách DTO của đồ chơi
   */
  List<ToyDTO> getToysByUserId(Long userId, Integer status);

  /**
   * Lấy thông tin chi tiết đồ chơi.
   *
   * @param id ID của đồ chơi
   * @return DTO của đồ chơi
   */
  ToyDTO getToyById(Long id);

  /**
   * Cập nhật thông tin đồ chơi.
   *
   * @param id      ID của đồ chơi
   * @param request Thông tin cập nhật
   * @return DTO của đồ chơi đã cập nhật
   */
  ToyDTO updateToy(Long id, UpdateToyRequest request);

  /**
   * Xóa đồ chơi.
   *
   * @param id ID của đồ chơi cần xóa
   */
  void deleteToy(Long id);

  /**
   * Cập nhật trạng thái đồ chơi.
   *
   * @param id     ID của đồ chơi
   * @param status Trạng thái mới
   */
  void updateToyStatus(Long id, Integer status);

  /**
   * Lấy danh sách đồ chơi có phân trang.
   *
   * @param page       Số trang (bắt đầu từ 0)
   * @param limit      Kích thước trang
   * @param userId     ID của người dùng (tùy chọn)
   * @param status     Trạng thái đồ chơi (tùy chọn)
   * @param campaignId ID của chiến dịch (tùy chọn)
   * @return Đối tượng PageResponse chứa danh sách DTO của đồ chơi và thông tin phân trang
   */
  PageResponse<ToyDTO> getToysWithPagination(int page, int limit, Long userId, Integer status,
      Long campaignId);

  /**
   * Lấy danh sách đồ chơi có phân trang và sắp xếp.
   *
   * @param page          Số trang (bắt đầu từ 0)
   * @param limit         Kích thước trang
   * @param userId        ID của người dùng (tùy chọn)
   * @param status        Trạng thái đồ chơi (tùy chọn)
   * @param campaignId    ID của chiến dịch (tùy chọn)
   * @param sortBy        Trường để sắp xếp
   * @param sortDirection Hướng sắp xếp (asc, desc)
   * @return Đối tượng PageResponse chứa danh sách DTO của đồ chơi và thông tin phân trang
   */
  PageResponse<ToyDTO> getToysWithPagination(int page, int limit, Long userId, Integer status,
      Long campaignId, String sortBy, String sortDirection);

  /**
   * Thêm đồ chơi vào chiến dịch.
   *
   * @param id         ID của đồ chơi
   * @param campaignId ID của chiến dịch
   * @return DTO của đồ chơi đã cập nhật
   */
  ToyDTO addToCampaign(Long id, Long campaignId);

  /**
   * Xóa đồ chơi khỏi chiến dịch.
   *
   * @param id ID của đồ chơi
   * @return DTO của đồ chơi đã cập nhật
   */
  ToyDTO removeFromCampaign(Long id);

  /**
   * Khôi phục đồ chơi đã xóa.
   *
   * @param id ID của đồ chơi
   * @return DTO của đồ chơi đã khôi phục
   */
  ToyDTO restoreToy(Long id);
}