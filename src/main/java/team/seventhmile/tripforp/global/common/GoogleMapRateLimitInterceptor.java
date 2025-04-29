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
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleMapRateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket(String role) {
        Bandwidth limit;
        if (role.equals("ADMIN")) {
            limit = Bandwidth.classic(10000, Refill.intervally(10000, Duration.ofDays(1)));
        } else if (role.equals("USER")) {
            limit = Bandwidth.classic(600, Refill.intervally(600, Duration.ofDays(1)));
        } else {
            limit = Bandwidth.classic(200, Refill.intervally(200, Duration.ofDays(1)));
        }
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

        String path = request.getRequestURI();

        String ip = getClientIP(request);
        String role = getRole();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Bucket bucket;
        if (!role.equals("ANONYMOUS")) {
            bucket = buckets.computeIfAbsent(name, k -> createNewBucket(role));
            log.info("계정: {}, 권한: {}", name, role);
        } else {
            bucket = buckets.computeIfAbsent(ip, k -> createNewBucket(role));
            log.info("클라이언트 IP: {}, 권한: {}", ip, role);
        }

        if (path.startsWith("/api/courses/popular-places") && bucket.getAvailableTokens() >= 100) {
            return true;
        }

        int requiredTokens = getRequiredTokens(path);
        if (bucket.tryConsume(requiredTokens)) {
            log.info("잔여 API 호출 횟수: {}", bucket.getAvailableTokens());
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {

        String path = request.getRequestURI();
        if (path.startsWith("/api/courses/popular-places")) {
            Integer size = RequestSizeHolder.getSize();
            if (size != null && size > 0) {
                String role = getRole();
                String name = SecurityContextHolder.getContext().getAuthentication().getName();

                Bucket bucket = buckets.computeIfAbsent(name, k -> createNewBucket(role));
                log.info("계정: {}, 권한: {}", name, role);

                int requiredTokens = size * 20;
                if (bucket.tryConsume(requiredTokens)) {
                    log.info("잔여 API 호출 횟수: {}", bucket.getAvailableTokens());
                } else {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                }
            }
        }
    }

    private String getRole() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString()
            .replace("[", "")
            .replace("]", "")
            .replace("ROLE_", "");
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private int getRequiredTokens(String path) {
        if (path.startsWith("/api/google-maps/detail")) return 20;
        if (path.startsWith("/api/google-maps/search")) return 32;
        if (path.startsWith("/api/google-maps/load")) return 7;
        if (path.startsWith("/api/courses/popular-places")) return 100;
        return 1;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) {
        RequestSizeHolder.clear();
    }
}
