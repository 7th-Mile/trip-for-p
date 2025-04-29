package team.seventhmile.tripforp.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import team.seventhmile.tripforp.global.common.GeminiRateLimitInterceptor;
import team.seventhmile.tripforp.global.common.GoogleMapRateLimitInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final GoogleMapRateLimitInterceptor googleMapRateLimitInterceptor;
	private final GeminiRateLimitInterceptor geminiRateLimitInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(googleMapRateLimitInterceptor)
			.addPathPatterns("/api/google-maps/**")
			.addPathPatterns("/api/courses/popular-places");
		registry.addInterceptor(geminiRateLimitInterceptor)
			.addPathPatterns("/api/ai");
	}


}
