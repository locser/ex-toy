package locser.application.services.toy.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import locser.application.services.toy.ToyApplicationService;
import locser.toy.domain.model.dto.CreateToyRequest;
import locser.toy.domain.model.dto.ToyDTO;
import locser.toy.domain.model.dto.UpdateToyRequest;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.repository.ToyRepository;
import locser.toy.domain.service.ToyDomainService;
import locser.util.PageResponse;

/**
 * Lớp dịch vụ ứng dụng cho Toy, điều phối các use case.
 */
@Service
public class ToyApplicationServiceImpl implements ToyApplicationService {

  private final ToyRepository toyRepository;
  private final ToyDomainService toyDomainService;

  public ToyApplicationServiceImpl(ToyRepository toyRepository, ToyDomainService toyDomainService) {
    this.toyRepository = toyRepository;
    this.toyDomainService = toyDomainService;
  }

  @Override
  public ToyDTO mapToDTO(Toy toy) {
    return ToyDTO.builder()
        .id(toy.getId())
        .userId(toy.getUserId())
        .campaignId(toy.getCampaignId())
        .name(toy.getName())
        .description(toy.getDescription())
        .category(toy.getCategory())
        .condition(toy.getCondition())
        .status(toy.getStatus())
        .desiredExchangeItems(toy.getDesiredExchangeItems())
        .createdAt(toy.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
        .updatedAt(toy.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
        .build();
  }

  @Override
  public ToyDTO createToy(Long userId, CreateToyRequest request) {
    // Tạo entity từ request
    Toy toy = new Toy();
    toy.setUserId(userId);
    toy.setName(request.getName());
    toy.setDescription(request.getDescription());
    toy.setCategory(request.getCategory());
    toy.setCondition(request.getCondition());
    toy.setDesiredExchangeItems(request.getDesiredExchangeItems());

    if (request.getCampaignId() != null && request.getCampaignId() > 0) {
      toy.setCampaignId(request.getCampaignId());
    }

    // Gọi domain service để xử lý logic nghiệp vụ
    toy = toyDomainService.initializeNewToy(toy);

    // Lưu vào repository
    Toy savedToy = toyRepository.save(toy);

    // Chuyển đổi và trả về DTO
    return mapToDTO(savedToy);
  }

  @Override
  public List<ToyDTO> getAllToys(Integer status) {
    List<Toy> toys;

    if (status != null) {
      toys = toyRepository.findByStatus(status);
    } else {
      toys = toyRepository.findAll();
    }

    return toys.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<ToyDTO> getToysByUserId(Long userId, Integer status) {
    List<Toy> toys;

    if (status != null) {
      toys = toyRepository.findByUserIdAndStatus(userId, status);
    } else {
      toys = toyRepository.findByUserId(userId);
    }

    return toys.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public ToyDTO getToyById(Long id) {
    Toy toy = toyDomainService.getToyById(id);
    return mapToDTO(toy);
  }

  @Override
  public ToyDTO updateToy(Long id, UpdateToyRequest request) {
    // Lấy đồ chơi hiện tại
    Toy toy = toyDomainService.getToyById(id);

    // Cập nhật thông tin
    if (request.getName() != null) {
      toy.setName(request.getName());
    }

    if (request.getDescription() != null) {
      toy.setDescription(request.getDescription());
    }

    if (request.getCategory() != null) {
      toy.setCategory(request.getCategory());
    }

    if (request.getCondition() != null) {
      toy.setCondition(request.getCondition());
    }

    if (request.getStatus() != null) {
      toy.setStatus(request.getStatus());
    }

    if (request.getCampaignId() != null) {
      toy.setCampaignId(request.getCampaignId());
    }

    if (request.getDesiredExchangeItems() != null) {
      toy.setDesiredExchangeItems(request.getDesiredExchangeItems());
    }

    // Gọi domain service để xác thực và xử lý logic nghiệp vụ
    toy = toyDomainService.validateAndUpdateToy(toy);

    // Lưu vào repository
    Toy updatedToy = toyRepository.save(toy);

    // Chuyển đổi và trả về DTO
    return mapToDTO(updatedToy);
  }

  @Override
  public void deleteToy(Long id) {
    toyDomainService.deleteToy(id);
  }

  @Override
  public void updateToyStatus(Long id, Integer status) {
    toyDomainService.updateToyStatus(id, status);
  }

  @Override
  public PageResponse<ToyDTO> getToysWithPagination(int page, int limit, Long userId,
      Integer status, Long campaignId) {
    // Mặc định sắp xếp theo id giảm dần
    return getToysWithPagination(page, limit, userId, status, campaignId, "id", "desc");
  }

  @Override
  public PageResponse<ToyDTO> getToysWithPagination(int page, int limit, Long userId,
      Integer status, Long campaignId,
      String sortBy, String sortDirection) {
    List<Toy> toys = toyRepository.findWithPagination(page, limit, userId, status, campaignId,
        sortBy, sortDirection);

    List<ToyDTO> toyDTOs = toys.stream()
        .map(this::mapToDTO)
        .collect(Collectors.toList());

    long total = toyRepository.count(userId, status, campaignId);

    return new PageResponse<ToyDTO>(toyDTOs, limit, total);
  }

  @Override
  public ToyDTO addToCampaign(Long id, Long campaignId) {
    Toy toy = toyDomainService.addToCampaign(id, campaignId);
    return mapToDTO(toy);
  }

  @Override
  public ToyDTO removeFromCampaign(Long id) {
    Toy toy = toyDomainService.removeFromCampaign(id);
    return mapToDTO(toy);
  }

  @Override
  public ToyDTO restoreToy(Long id) {
    Toy toy = toyDomainService.restoreToy(id);
    return mapToDTO(toy);
  }
}