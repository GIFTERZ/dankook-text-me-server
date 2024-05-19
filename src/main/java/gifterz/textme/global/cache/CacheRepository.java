package gifterz.textme.global.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;

@RequiredArgsConstructor
public abstract class CacheRepository {
    private final CacheManager cacheManager;
    private final LocalCacheType localCacheType;

    protected void putCache(String key, Object value) {
        cacheManager.getCache(localCacheType.getName()).put(key, value);
    }

    protected void getCache(String key) {
        cacheManager.getCache(localCacheType.getName()).get(key);
    }

    protected void evictCache(String key) {
        cacheManager.getCache(localCacheType.getName()).evict(key);
    }
}
