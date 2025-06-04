package locser.toy.domain.service;

import locser.toy.domain.exception.ResourceNotFoundException;
import locser.toy.domain.model.entity.Toy;

/**
 * Interface định nghĩa các dịch vụ miền cho Toy.
 */
public interface ToyDomainService {

  /**
   * Khởi tạo một đồ chơi mới với các giá trị mặc định.
   *
   * @param toy Đồ chơi cần khởi tạo
   * @return Đồ chơi đã được khởi tạo
   */
  Toy initializeNewToy(Toy toy);

  /**
   * Lấy đồ chơi theo ID.
   *
   * @param id ID của đồ chơi
   * @return Đồ chơi
   * @throws ResourceNotFoundException nếu không tìm thấy
   */
  Toy getToyById(Long id);

  /**
   * Xác thực và cập nhật đồ chơi.
   *
   * @param toy Đồ chơi cần cập nhật
   * @return Đồ chơi đã được cập nhật
   */
  Toy validateAndUpdateToy(Toy toy);

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
   * Thêm đồ chơi vào chiến dịch.
   *
   * @param id         ID của đồ chơi
   * @param campaignId ID của chiến dịch
   * @return Đồ chơi đã được cập nhật
   */
  Toy addToCampaign(Long id, Long campaignId);

  /**
   * Xóa đồ chơi khỏi chiến dịch.
   *
   * @param id ID của đồ chơi
   * @return Đồ chơi đã được cập nhật
   */
  Toy removeFromCampaign(Long id);

  /**
   * Khôi phục đồ chơi đã xóa.
   *
   * @param id ID của đồ chơi
   * @return Đồ chơi đã được khôi phục
   */
  Toy restoreToy(Long id);

  /**
   * Kiểm tra xem người dùng có sở hữu đồ chơi không.
   *
   * @param userId ID của người dùng
   * @param toyId  ID của đồ chơi
   * @return true nếu người dùng sở hữu đồ chơi, false nếu không
   */
  boolean isOwner(Long userId, Long toyId);
}
