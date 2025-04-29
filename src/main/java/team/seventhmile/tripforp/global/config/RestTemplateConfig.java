package team.seventhmile.tripforp.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate googleMapsRestTemplate() {
        return new RestTemplateBuilder()
            .rootUri("https://places.googleapis.com/v1")
            .defaultHeader("X-Goog-Api-Key", googleMapsApiKey)
            .build();
    }

}
