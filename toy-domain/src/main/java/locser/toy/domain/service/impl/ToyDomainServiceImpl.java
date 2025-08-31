package locser.toy.domain.service.impl;

import org.springframework.stereotype.Service;

import locser.toy.domain.exception.BadRequestException;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.enums.ToyStatus;
import locser.toy.domain.repository.ToyRepository;
import locser.toy.domain.service.ToyDomainService;
import locser.toy.domain.validation.IdValidator;

/**
 * Triển khai các dịch vụ miền cho Toy.
 */
@Service
public class ToyDomainServiceImpl implements ToyDomainService {

  private final ToyRepository toyRepository;

  public ToyDomainServiceImpl(ToyRepository toyRepository) {
    this.toyRepository = toyRepository;
  }

  @Override
  public Toy initializeNewToy(Toy toy) {
    // Thiết lập các giá trị mặc định
    toy.setStatus(ToyStatus.AVAILABLE.getValue());

    // Xác thực dữ liệu
    validateToy(toy);

    return toy;
  }

  @Override
  public Toy getToyById(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Toy");

    return toyRepository.findOneById(id);
  }

  @Override
  public Toy validateAndUpdateToy(Toy toy) {
    // Xác thực dữ liệu
    validateToy(toy);

    return toy;
  }

  @Override
  public void deleteToy(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Toy");

    Toy toy = getToyById(id);

    // Soft delete: Chỉ đánh dấu là đã xóa
    toy.setStatus(ToyStatus.REMOVED.getValue());
    toyRepository.save(toy);
  }

  @Override
  public void updateToyStatus(Long id, Integer status) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Toy");

    Toy toy = getToyById(id);

    toy.setStatus(status);

    toyRepository.save(toy);
  }

  @Override
  public Toy addToCampaign(Long id, Long campaignId) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Toy");
    IdValidator.validateId(campaignId, "Campaign");

    Toy toy = getToyById(id);

    // Kiểm tra trạng thái đồ chơi
    if (toy.getStatus() != ToyStatus.AVAILABLE.getValue()) {
      throw new BadRequestException("Đồ chơi không khả dụng để thêm vào chiến dịch");
    }

    toy.setCampaignId(campaignId);

    return toyRepository.save(toy);
  }

  @Override
  public Toy removeFromCampaign(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Toy");

    Toy toy = getToyById(id);

    // Kiểm tra đồ chơi có thuộc chiến dịch nào không
    if (toy.getCampaignId() == 0L) {
      throw new BadRequestException("Đồ chơi không thuộc chiến dịch nào");
    }

    toy.setCampaignId(0L);

    return toyRepository.save(toy);
  }

  @Override
  public Toy restoreToy(Long id) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(id, "Toy");

    Toy toy = getToyById(id);

    // Kiểm tra đồ chơi có bị xóa không
    if (toy.getStatus() != ToyStatus.REMOVED.getValue()) {
      throw new BadRequestException("Đồ chơi không ở trạng thái đã xóa");
    }

    toy.setStatus(ToyStatus.AVAILABLE.getValue());

    return toyRepository.save(toy);
  }

  @Override
  public boolean isOwner(Long userId, Long toyId) {
    // Kiểm tra ID phải lớn hơn 0
    IdValidator.validateId(userId, "User");
    IdValidator.validateId(toyId, "Toy");

    Toy toy = getToyById(toyId);

    return toy.getUserId().equals(userId);
  }

  /**
   * Xác thực dữ liệu đồ chơi.
   */
  private void validateToy(Toy toy) {
    if (toy.getName() == null || toy.getName().trim().isEmpty()) {
      throw new BadRequestException("Tên đồ chơi không được để trống");
    }

    if (toy.getUserId() == null || toy.getUserId() <= 0) {
      throw new BadRequestException("ID người dùng không hợp lệ");
    }
  }
}