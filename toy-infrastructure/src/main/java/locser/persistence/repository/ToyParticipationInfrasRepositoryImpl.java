package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import locser.persistence.mapper.ToyParticipationJPAMapper;
import locser.toy.domain.model.entity.ToyParticipation;
import locser.toy.domain.repository.ToyParticipationRepository;

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
    System.out.println("ToyParticipationInfrasRepositoryImpl findOneById");

    return toyParticipationJPAMapper.findOneById(id);
  }

  @Override
  public ToyParticipation save(ToyParticipation participation) {
    System.out.println("ToyParticipationInfrasRepositoryImpl save");
    return toyParticipationJPAMapper.save(participation);
  }

  @Override
  public boolean existsByUserIdAndCampaignId(Long userId, Long campaignId) {
    System.out.println("ToyParticipationInfrasRepositoryImpl existsByUserIdAndCampaignId");

    return toyParticipationJPAMapper.existsByUserIdAndCampaignId(userId, campaignId);
  }

  @Override
  public Optional<ToyParticipation> findByUserIdAndCampaignId(Long userId, Long campaignId) {
    System.out.println("ToyParticipationInfrasRepositoryImpl findByUserIdAndCampaignId");

    return toyParticipationJPAMapper.findByUserIdAndCampaignId(userId, campaignId);
  }

  @Override
  public List<ToyParticipation> findByUserId(Long userId, int page, int limit) {
    System.out.println("ToyParticipationInfrasRepositoryImpl findByUserId");

    Pageable pageable = PageRequest.of(page, limit);
    return toyParticipationJPAMapper.findByUserId(userId, pageable).getContent();
  }

  @Override
  public List<ToyParticipation> findByCampaignId(Long campaignId) {
    System.out.println("ToyParticipationInfrasRepositoryImpl findByCampaignId");

    return toyParticipationJPAMapper.findByCampaignId(campaignId);
  }

  @Override
  public long countByCampaignId(Long campaignId) {
    System.out.println("ToyParticipationInfrasRepositoryImpl countByCampaignId");
    return toyParticipationJPAMapper.countByCampaignId(campaignId);
  }

  @Override
  public long countByUserId(Long userId) {
    System.out.println("ToyParticipationInfrasRepositoryImpl countByUserId");
    return toyParticipationJPAMapper.countByUserId(userId);
  }

  @Override
  public List<ToyParticipation> findByUserIdAndStatus(Long userId, Integer status) {
    System.out.println("ToyParticipationInfrasRepositoryImpl findByUserIdAndStatus");
    return toyParticipationJPAMapper.findByUserIdAndStatus(userId, status);
  }

  @Override
  public List<ToyParticipation> findByCampaignIdAndStatus(Long campaignId, Integer status) {
    System.out.println("ToyParticipationInfrasRepositoryImpl findByCampaignIdAndStatus");
    return toyParticipationJPAMapper.findByCampaignIdAndStatus(campaignId, status);
  }

  @Override
  public List<ToyParticipation> saveAll(List<ToyParticipation> participations) {
    // System.out.println("ToyParticipationInfrasRepositoryImpl saveAll");
    return toyParticipationJPAMapper.saveAll(participations);
  }
}