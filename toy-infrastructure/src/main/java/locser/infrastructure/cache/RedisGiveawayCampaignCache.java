package locser.infrastructure.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import locser.toy.domain.model.entity.Event;
import locser.toy.domain.model.entity.Toy;

@Service
public class RedisGiveawayCampaignCache {

  private static final String CAMPAIGN_KEY_PREFIX = "campaign:";
  private static final String AVAILABLE_TOYS_KEY_PREFIX = "campaign:toys:";
  private static final String USER_PARTICIPATION_KEY_PREFIX = "campaign:user:";
  private static final long CAMPAIGN_CACHE_TTL = 24; // 24 hours
  private static final long USER_PARTICIPATION_CACHE_TTL = 7; // 7 days
  private static final String AVAILABLE_TOYS_COUNT_KEY_PREFIX = "campaign:toys:count:";
  private static final String TOY_KEY_PREFIX = "toy:";
  private static final long TOY_CACHE_TTL = 3 * 60 * 60; // 3 hours

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisGiveawayCampaignCache(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  private EventCacheDTO toDto(Event event) {
    EventCacheDTO dto = new EventCacheDTO();
    dto.id = event.getId();
    dto.name = event.getName();
    dto.description = event.getDescription();
    dto.startDate = event.getStartDate().toString();
    dto.endDate = event.getEndDate().toString();
    dto.theme = event.getTheme();
    dto.rules = event.getRules();
    dto.status = event.getStatus();
    dto.type = event.getType();
    dto.totalToys = event.getTotalToys();
    dto.availableToys = event.getAvailableToys();
    return dto;
  }

  private Event fromDto(EventCacheDTO dto) {
    Event event = new Event();
    event.setId(dto.id);
    event.setName(dto.name);
    event.setDescription(dto.description);
    event.setStartDate(java.time.LocalDateTime.parse(dto.startDate));
    event.setEndDate(java.time.LocalDateTime.parse(dto.endDate));
    event.setTheme(dto.theme);
    event.setRules(dto.rules);
    event.setStatus(dto.status);
    event.setType(dto.type);
    event.setTotalToys(dto.totalToys);
    event.setAvailableToys(dto.availableToys);
    return event;
  }

  public void cacheCampaign(Long campaignId, Event campaign) {
    System.out.println("caching campaign: " + campaign);
    String key = CAMPAIGN_KEY_PREFIX + campaignId;
    EventCacheDTO dto = toDto(campaign);
    redisTemplate.opsForValue().set(key, dto, CAMPAIGN_CACHE_TTL, TimeUnit.HOURS);
  }

  public Event getCachedCampaign(Long campaignId) {
    System.out.println("getCachedCampaign cached: " + campaignId);

    String key = CAMPAIGN_KEY_PREFIX + campaignId;
    Object cached = redisTemplate.opsForValue().get(key);
    if (cached == null) {
      return null;
    }

    try {
      if (cached instanceof EventCacheDTO) {
        return fromDto((EventCacheDTO) cached);
      }
      // Fallback: convert via Jackson
      EventCacheDTO dto = new ObjectMapper().convertValue(cached, EventCacheDTO.class);
      return fromDto(dto);
    } catch (Exception e) {
      System.err.println("Failed to convert cached object: " + e.getMessage());
      redisTemplate.delete(key);
      return null;
    }
  }

  public void cacheAvailableToys(Long campaignId, Set<Long> toyIds) {
    String key = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
    // Clear existing toys
    redisTemplate.delete(key);
    // Add all toys to set (not list)
    redisTemplate.opsForSet().add(key, toyIds.toArray());
    redisTemplate.expire(key, CAMPAIGN_CACHE_TTL, TimeUnit.HOURS);
  }

  public Long getRandomAvailableToy(Long campaignId) {
    String key = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
    // SPOP atomically returns and removes random element
    Object toyId = redisTemplate.opsForSet().pop(key);
    return toyId != null ? Long.parseLong(toyId.toString()) : null;
  }

  public int getAvailableToysCount(Long campaignId) {
    String key = AVAILABLE_TOYS_COUNT_KEY_PREFIX + campaignId;
    Object count = redisTemplate.opsForValue().get(key);
    if (count == null) {
      return -1;
    }
    return Integer.parseInt(count.toString());
  }

  public void markUserParticipated(Long userId, Long campaignId) {
    String key = USER_PARTICIPATION_KEY_PREFIX + campaignId;
    redisTemplate.opsForSet().add(key, userId);
    redisTemplate.expire(key, USER_PARTICIPATION_CACHE_TTL, TimeUnit.DAYS);
  }

  public boolean hasUserParticipated(Long userId, Long campaignId) {
    String key = USER_PARTICIPATION_KEY_PREFIX + campaignId;
    return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId));
  }

  public void removeCampaign(Long campaignId) {
    String campaignKey = CAMPAIGN_KEY_PREFIX + campaignId;
    String toysKey = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
    String participationKey = USER_PARTICIPATION_KEY_PREFIX + campaignId;

    redisTemplate.delete(campaignKey);
    redisTemplate.delete(toysKey);
    redisTemplate.delete(participationKey);
  }

  public void removeToyIdFromAvailableToys(Long campaignId, Long toyId) {
    String key = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
    redisTemplate.opsForSet().remove(key, toyId);

  }

  // Simple DTO for Redis cache
  public static class EventCacheDTO {

    public Long id;
    public String name;
    public String description;
    public String startDate; // Store as ISO string
    public String endDate; // Store as ISO string
    public String theme;
    public String rules;
    public Integer status;
    public Integer type;
    public Integer totalToys;
    public Integer availableToys;
    public String createdAt; // Store as ISO string
    public String updatedAt; // Store as ISO string
  }

  public void cacheAvailableToysCount(Long campaignId, int count) {
    String key = AVAILABLE_TOYS_COUNT_KEY_PREFIX + campaignId;
    redisTemplate.opsForValue().set(key, count, CAMPAIGN_CACHE_TTL, TimeUnit.HOURS);
  }

  public static class ToyCacheDTO {
    public Long id;
    public Long userId;
    public Long campaignId;
    public String name;
    public String description;
    public String category;
    public Integer condition;
    public Integer status;
    public String desiredExchangeItems;
    public String createdAt;
    public String updatedAt;
  }

  private Toy fromToyDto(ToyCacheDTO dto) {
    Toy toy = new Toy();
    toy.setId(dto.id);
    toy.setUserId(dto.userId);
    toy.setCampaignId(dto.campaignId);
    toy.setName(dto.name);
    toy.setDescription(dto.description);
    toy.setCategory(dto.category);
    toy.setCondition(dto.condition);
    toy.setStatus(dto.status);
    toy.setDesiredExchangeItems(dto.desiredExchangeItems);
    toy.setCreatedAt(java.time.LocalDateTime.parse(dto.createdAt));
    toy.setUpdatedAt(java.time.LocalDateTime.parse(dto.updatedAt));
    return toy;
  }

  private ToyCacheDTO toToyDto(Toy toy) {
    ToyCacheDTO dto = new ToyCacheDTO();
    dto.id = toy.getId();
    dto.userId = toy.getUserId();
    dto.campaignId = toy.getCampaignId();
    dto.name = toy.getName();
    dto.description = toy.getDescription();
    dto.category = toy.getCategory();
    dto.condition = toy.getCondition();
    dto.status = toy.getStatus();
    dto.desiredExchangeItems = toy.getDesiredExchangeItems();
    dto.createdAt = toy.getCreatedAt().toString();
    dto.updatedAt = toy.getUpdatedAt().toString();
    return dto;
  }

  public void cacheToy(Long toyId, Toy toy) {
    String key = TOY_KEY_PREFIX + toyId;
    ToyCacheDTO dto = toToyDto(toy);
    redisTemplate.opsForValue().set(key, dto, TOY_CACHE_TTL, TimeUnit.HOURS);
  }

  public Toy getCachedToy(Long toyId) {
    String key = TOY_KEY_PREFIX + toyId;
    
    try {
      Object cached = redisTemplate.opsForValue().get(key);

      if (cached == null) {
        return null;
      }

      if (cached instanceof ToyCacheDTO) {
        return fromToyDto((ToyCacheDTO) cached);
      }
      // Fallback: convert via Jackson
      ToyCacheDTO dto = new ObjectMapper().convertValue(cached, ToyCacheDTO.class);
      return fromToyDto(dto);
    } catch (Exception e) {
      System.err.println("Failed to get cached toy " + toyId + ": " + e.getMessage());
      // Don't delete key on timeout/connection errors, only on conversion errors
      if (e.getMessage() != null && !e.getMessage().contains("timeout") && !e.getMessage().contains("connection")) {
        redisTemplate.delete(key);
      }
      return null;
    }
  }

  public void removeToy(Long toyId) {
    String key = TOY_KEY_PREFIX + toyId;
    redisTemplate.delete(key);
  }
}