package team.seventhmile.tripforp.global.common;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiRateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofDays(1)));

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    public void resetBucket() {
        buckets.clear();
    }

    public int count() {
        return buckets.size();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {

        String ip = getClientIP(request);
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Bucket bucket = buckets.computeIfAbsent(name, k -> createNewBucket());

        log.info("클라이언트 IP: {}, 계정: {}", ip, name);

        if (bucket.tryConsume(1)) {
            log.info("잔여 API 호출 횟수: {}", bucket.getAvailableTokens());
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public long getChance(UserDetails userDetails) {
        Bucket bucket = buckets.get(userDetails.getUsername());
        return bucket != null ? bucket.getAvailableTokens() : 3;
    }
}
