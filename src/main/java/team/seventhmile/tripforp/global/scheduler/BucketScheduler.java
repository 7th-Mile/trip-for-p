package team.seventhmile.tripforp.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.seventhmile.tripforp.global.common.GeminiRateLimitInterceptor;
import team.seventhmile.tripforp.global.common.GoogleMapRateLimitInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class BucketScheduler {

    private final GoogleMapRateLimitInterceptor googleMapRateLimitInterceptor;
    private final GeminiRateLimitInterceptor geminiRateLimitInterceptor;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    private void cleanGoogleMapBuckets() {
        try {
            int initialSize = googleMapRateLimitInterceptor.count();
            googleMapRateLimitInterceptor.resetBucket();
            int removedCount = initialSize - googleMapRateLimitInterceptor.count();
            log.info("버킷 초기화 완료. 초기화된 버킷 수: {}", removedCount);
        } catch (Exception e) {
            log.error("버켓 초기화 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    private void cleanGeminiBuckets() {
        try {
            int initialSize = geminiRateLimitInterceptor.count();
            geminiRateLimitInterceptor.resetBucket();
            int removedCount = initialSize - geminiRateLimitInterceptor.count();
            log.info("버킷 초기화 완료. 초기화된 버킷 수: {}", removedCount);
        } catch (Exception e) {
            log.error("버켓 초기화 중 오류 발생", e);
        }
    }



}
