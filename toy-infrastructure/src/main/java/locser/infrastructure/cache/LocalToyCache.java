package locser.infrastructure.cache;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import locser.toy.domain.model.entity.Toy;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LocalToyCache {
    // TODO: nhận quà xong cần xoá local cache của toy đó

    private static final int CACHE_TTL_SECONDS = 60 * 60; // Increased to 60 seconds for better performance

    private final Map<Long, CacheEntry> cache = new ConcurrentHashMap<>();

    /**
     * Cache entry with TTL support
     */
    private static class CacheEntry {
        private final Toy toy;
        private final LocalDateTime expiryTime;

        public CacheEntry(Toy toy, int ttl) {
            this.toy = toy;
            this.expiryTime = LocalDateTime.now().plusSeconds(ttl);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

        public Toy getToy() {
            return toy;
        }
    }

    /**
     * Get toy from local cache
     *
     * @param toyId toy ID
     * @return toy if exists and not expired, null otherwise
     */
    public Toy getToy(Long toyId) {
        CacheEntry entry = cache.get(toyId);

        if (entry == null) {
            log.debug("toy {} not found in local cache", toyId);
            return null;
        }

        if (entry.isExpired()) {
            log.debug("toy {} expired in local cache, removing", toyId);
            cache.remove(toyId);
            return null;
        }

        log.debug("toy {} found in local cache", toyId);
        return entry.getToy();
    }

    /**
     * Put toy into local cache
     *
     * @param toyId toy ID
     * @param toy   toy object
     */
    public void putToy(Long toyId, Toy toy) {
        if (toy != null) {
            int ttl = CACHE_TTL_SECONDS + (int) (Math.random() * 1000);

            cache.put(toyId, new CacheEntry(toy, ttl));
            log.debug("toy {} cached locally for {} seconds", toyId, ttl);
        }
    }

    /**
     * Remove toy from local cache
     *
     * @param toyId toy ID
     */
    public void removeToy(Long toyId) {
        cache.remove(toyId);
        log.debug("toy {} removed from local cache", toyId);
    }

    /**
     * Clear all expired entries from cache
     */
    public void cleanupExpiredEntries() {
        cache.entrySet().removeIf(entry -> {
            boolean expired = entry.getValue().isExpired();
            if (expired) {
                log.debug("Removing expired toy {} from local cache", entry.getKey());
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
        log.info("Local toy cache cleared");
    }
}
