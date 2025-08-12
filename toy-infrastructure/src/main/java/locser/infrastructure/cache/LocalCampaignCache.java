package locser.infrastructure.cache;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import locser.toy.domain.model.entity.Event;
import lombok.extern.slf4j.Slf4j;

/**
 * Local in-memory cache for campaigns with TTL (Time To Live) support.
 * Cache expires after 15 seconds.
 */
@Component
@Slf4j
public class LocalCampaignCache {

    private static final int CACHE_TTL_SECONDS = 15;
    
    private final Map<Long, CacheEntry> cache = new ConcurrentHashMap<>();

    /**
     * Cache entry with TTL support
     */
    private static class CacheEntry {
        private final Event campaign;
        private final LocalDateTime expiryTime;

        public CacheEntry(Event campaign) {
            this.campaign = campaign;
            this.expiryTime = LocalDateTime.now().plusSeconds(CACHE_TTL_SECONDS);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

        public Event getCampaign() {
            return campaign;
        }
    }

    /**
     * Get campaign from local cache
     * 
     * @param campaignId Campaign ID
     * @return Campaign if exists and not expired, null otherwise
     */
    public Event getCampaign(Long campaignId) {
        CacheEntry entry = cache.get(campaignId);
        
        if (entry == null) {
            log.debug("Campaign {} not found in local cache", campaignId);
            return null;
        }
        
        if (entry.isExpired()) {
            log.debug("Campaign {} expired in local cache, removing", campaignId);
            cache.remove(campaignId);
            return null;
        }
        
        log.debug("Campaign {} found in local cache", campaignId);
        return entry.getCampaign();
    }

    /**
     * Put campaign into local cache
     * 
     * @param campaignId Campaign ID
     * @param campaign Campaign object
     */
    public void putCampaign(Long campaignId, Event campaign) {
        if (campaign != null) {
            cache.put(campaignId, new CacheEntry(campaign));
            log.debug("Campaign {} cached locally for {} seconds", campaignId, CACHE_TTL_SECONDS);
        }
    }

    /**
     * Remove campaign from local cache
     * 
     * @param campaignId Campaign ID
     */
    public void removeCampaign(Long campaignId) {
        cache.remove(campaignId);
        log.debug("Campaign {} removed from local cache", campaignId);
    }

    /**
     * Clear all expired entries from cache
     */
    public void cleanupExpiredEntries() {
        cache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                log.debug("Removing expired campaign {} from local cache", entry.getKey());
            }
            return expired;
        });
    }

    /**
     * Get cache statistics
     */
    public Map<String, Object> getCacheStats() {
        cleanupExpiredEntries(); // Clean up before getting stats
        
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("size", cache.size());
        stats.put("ttl_seconds", CACHE_TTL_SECONDS);
        stats.put("entries", cache.keySet());
        
        return stats;
    }

    /**
     * Clear all cache entries
     */
    public void clearAll() {
        cache.clear();
        log.info("Local campaign cache cleared");
    }
}