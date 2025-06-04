package locser.persistence.repository;

import java.util.List;
import java.util.Optional;
import locser.persistence.mapper.ToyParticipationJPAMapper;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.repository.ToyParticipationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of ToyParticipationRepository using JPA.
 */
@Service
public class ToyParticipationInfrasRepositoryImpl implements ToyParticipationRepository {

  private final ToyParticipationJPAMapper toyParticipationJPAMapper;

  public ToyParticipationInfrasRepositoryImpl(ToyParticipationJPAMapper toyParticipationJPAMapper) {
    this.toyParticipationJPAMapper = toyParticipationJPAMapper;
  }

  @Override
  public Optional<ToyParticipation> findOneById(Long id) {
    return toyParticipationJPAMapper.findOneById(id);
  }

  @Override
  public ToyParticipation save(ToyParticipation participation) {
    return toyParticipationJPAMapper.save(participation);
  }

  @Override
  public boolean existsByUserIdAndCampaignId(Long userId, Long campaignId) {
    return toyParticipationJPAMapper.existsByUserIdAndCampaignId(userId, campaignId);
  }

  @Override
  public Optional<ToyParticipation> findByUserIdAndCampaignId(Long userId, Long campaignId) {
    return toyParticipationJPAMapper.findByUserIdAndCampaignId(userId, campaignId);
  }

  @Override
  public List<ToyParticipation> findByUserId(Long userId, int page, int limit) {
    Pageable pageable = PageRequest.of(page, limit);
    return toyParticipationJPAMapper.findByUserId(userId, pageable).getContent();
  }

  @Override
  public List<ToyParticipation> findByCampaignId(Long campaignId) {
    return toyParticipationJPAMapper.findByCampaignId(campaignId);
  }

  @Override
  public long countByCampaignId(Long campaignId) {
    return toyParticipationJPAMapper.countByCampaignId(campaignId);
  }

  @Override
  public long countByUserId(Long userId) {
    return toyParticipationJPAMapper.countByUserId(userId);
  }

  @Override
  public List<ToyParticipation> findByUserIdAndStatus(Long userId, Integer status) {
    return toyParticipationJPAMapper.findByUserIdAndStatus(userId, status);
  }

  @Override
  public List<ToyParticipation> findByCampaignIdAndStatus(Long campaignId, Integer status) {
    return toyParticipationJPAMapper.findByCampaignIdAndStatus(campaignId, status);
  }
}