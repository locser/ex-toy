package locser.controller.service.impl;

import java.util.List;
import locser.application.services.toy.ToyApplicationService;
import locser.controller.dto.toy.CreateToyRequestDTO;
import locser.controller.dto.toy.ToyResponseDTO;
import locser.controller.dto.toy.UpdateToyRequestDTO;
import locser.controller.mapper.ToyDTOMapper;
import locser.controller.service.ToyService;
import locser.toy.domain.model.dto.ToyDTO;
import locser.toy.domain.service.ToyDomainService;
import locser.util.PageResponse;
import locser.util.PageResponseDTO;
import org.springframework.stereotype.Service;

/**
 * Implementation of ToyService.
 */
@Service
public class ToyServiceImpl implements ToyService {

  private final ToyApplicationService toyApplicationService;
  private final ToyDomainService toyDomainService;

  public ToyServiceImpl(ToyApplicationService toyApplicationService,
      ToyDomainService toyDomainService) {
    this.toyApplicationService = toyApplicationService;
    this.toyDomainService = toyDomainService;
  }

  @Override
  public ToyResponseDTO createToy(Long userId, CreateToyRequestDTO request) {
    ToyDTO toyDTO = toyApplicationService.createToy(userId,
        ToyDTOMapper.toCreateToyRequest(request));
    return ToyDTOMapper.toToyResponseDTO(toyDTO);
  }

  @Override
  public List<ToyResponseDTO> getAllToys(Integer status) {
    List<ToyDTO> toyDTOs = toyApplicationService.getAllToys(status);
    return ToyDTOMapper.toToyResponseDTOs(toyDTOs);
  }

  @Override
  public List<ToyResponseDTO> getToysByUserId(Long userId, Integer status) {
    List<ToyDTO> toyDTOs = toyApplicationService.getToysByUserId(userId, status);
    return ToyDTOMapper.toToyResponseDTOs(toyDTOs);
  }

  @Override
  public ToyResponseDTO getToyById(Long id) {
    ToyDTO toyDTO = toyApplicationService.getToyById(id);
    return ToyDTOMapper.toToyResponseDTO(toyDTO);
  }

  @Override
  public ToyResponseDTO updateToy(Long id, UpdateToyRequestDTO request) {
    ToyDTO toyDTO = toyApplicationService.updateToy(id, ToyDTOMapper.toUpdateToyRequest(request));
    return ToyDTOMapper.toToyResponseDTO(toyDTO);
  }

  @Override
  public void deleteToy(Long id) {
    toyApplicationService.deleteToy(id);
  }

  @Override
  public ToyResponseDTO updateToyStatus(Long id, Integer status) {
    toyApplicationService.updateToyStatus(id, status);
    return getToyById(id);
  }

  @Override
  public PageResponseDTO<ToyResponseDTO> getToysWithPagination(int page, int limit, Long userId,
      Integer status,
      Long campaignId, String sortBy, String sortDir) {
    PageResponse<ToyDTO> pageResponse = toyApplicationService.getToysWithPagination(page, limit,
        userId, status,
        campaignId, sortBy, sortDir);
    return ToyDTOMapper.toPageResponseDTO(pageResponse);
  }

  @Override
  public ToyResponseDTO addToCampaign(Long id, Long campaignId) {
    ToyDTO toyDTO = toyApplicationService.addToCampaign(id, campaignId);
    return ToyDTOMapper.toToyResponseDTO(toyDTO);
  }

  @Override
  public ToyResponseDTO removeFromCampaign(Long id) {
    ToyDTO toyDTO = toyApplicationService.removeFromCampaign(id);
    return ToyDTOMapper.toToyResponseDTO(toyDTO);
  }

  @Override
  public ToyResponseDTO restoreToy(Long id) {
    ToyDTO toyDTO = toyApplicationService.restoreToy(id);
    return ToyDTOMapper.toToyResponseDTO(toyDTO);
  }

  @Override
  public boolean isOwner(Long userId, Long toyId) {
    return toyDomainService.isOwner(userId, toyId);
  }
}