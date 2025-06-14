package locser.infrastructure.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisGiveawayCampaignCache {
    private static final String CAMPAIGN_KEY_PREFIX = "campaign:";
    private static final String AVAILABLE_TOYS_KEY_PREFIX = "campaign:toys:";
    private static final String USER_PARTICIPATION_KEY_PREFIX = "campaign:user:";
    private static final long CAMPAIGN_CACHE_TTL = 24; // 24 hours
    private static final long USER_PARTICIPATION_CACHE_TTL = 7; // 7 days

    private final RedisTemplate<String, Object> redisTemplate;

    // Lua script for atomic random selection and removal
    private static final String RANDOM_AND_REMOVE_SCRIPT = "local toys = redis.call('LRANGE', KEYS[1], 0, -1) " +
            "if #toys == 0 then return nil end " +
            "local randomIndex = math.random(1, #toys) " +
            "local selectedToy = toys[randomIndex] " +
            "redis.call('LREM', KEYS[1], 1, selectedToy) " +
            "return selectedToy";

    private final DefaultRedisScript<String> randomAndRemoveScript;

    public RedisGiveawayCampaignCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.randomAndRemoveScript = new DefaultRedisScript<>();
        this.randomAndRemoveScript.setScriptText(RANDOM_AND_REMOVE_SCRIPT);
        this.randomAndRemoveScript.setResultType(String.class);
    }

    public void cacheCampaign(Long campaignId, Object campaign) {
        String key = CAMPAIGN_KEY_PREFIX + campaignId;
        redisTemplate.opsForValue().set(key, campaign, CAMPAIGN_CACHE_TTL, TimeUnit.HOURS);
    }

    public Object getCachedCampaign(Long campaignId) {
        String key = CAMPAIGN_KEY_PREFIX + campaignId;
        return redisTemplate.opsForValue().get(key);
    }

    public void cacheAvailableToys(Long campaignId, Set<Long> toyIds) {
        String key = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
        // Clear existing toys
        redisTemplate.delete(key);
        // Add all toys to list
        List<Long> toyList = new ArrayList<>(toyIds);
        redisTemplate.opsForList().rightPushAll(key, toyList);
        redisTemplate.expire(key, CAMPAIGN_CACHE_TTL, TimeUnit.HOURS);
    }

    public Optional<Long> getRandomAvailableToy(Long campaignId) {
        String key = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
        String toyId = redisTemplate.execute(
                randomAndRemoveScript,
                List.of(key));
        return Optional.ofNullable(toyId).map(Long::parseLong);
    }

    public long getAvailableToysCount(Long campaignId) {
        String key = AVAILABLE_TOYS_KEY_PREFIX + campaignId;
        return redisTemplate.opsForList().size(key);
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
}