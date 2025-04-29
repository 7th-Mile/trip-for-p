package team.seventhmile.tripforp.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(buildRegionCache());
        caches.add(buildPopularRegionCache());
        caches.add(buildPopularPlaceCache());
        caches.add(buildMagazineCache());
        caches.add(buildEmailCodeCache());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private CaffeineCache buildRegionCache() {
        return new CaffeineCache("regions",
            Caffeine.newBuilder()
                .maximumSize(300)
                .build());
    }

    private CaffeineCache buildPopularRegionCache() {
        return new CaffeineCache("popularRegions",
            Caffeine.newBuilder()
                .maximumSize(20)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    private CaffeineCache buildPopularPlaceCache() {
        return new CaffeineCache("popularPlaces",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1500)
                .build());
    }

    private CaffeineCache buildMagazineCache() {
        return new CaffeineCache("magazines",
            Caffeine.newBuilder()
                .maximumSize(20)
                .build());
    }

    private CaffeineCache buildEmailCodeCache() {
        return new CaffeineCache("emailCodes",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build());
    }
}
