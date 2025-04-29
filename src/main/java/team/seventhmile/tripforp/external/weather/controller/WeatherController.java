package team.seventhmile.tripforp.external.weather.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.ai.dto.DateAndAreaRequest;
import team.seventhmile.tripforp.external.weather.dto.WeatherApiResponse;
import team.seventhmile.tripforp.external.weather.dto.WeatherDto;
import team.seventhmile.tripforp.external.weather.service.WeatherService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<WeatherApiResponse> getWeatherApi(
        @RequestParam(name = "area") String area
    ) {
        return ResponseEntity.ok(weatherService.getWeatherApi(area));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/filter")
    public ResponseEntity<List<WeatherDto>> getWeather(
        @RequestParam(name = "area") String area,
        @RequestParam(name = "startDate") LocalDate startDate,
        @RequestParam(name = "endDate") LocalDate endDate
    ) {
        DateAndAreaRequest request = new DateAndAreaRequest(startDate, endDate, area);
        return ResponseEntity.ok(weatherService.getWeather(request));
    }
}
