package team.seventhmile.tripforp.external.weather.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import team.seventhmile.tripforp.external.ai.dto.DateAndAreaRequest;
import team.seventhmile.tripforp.external.weather.dto.WeatherApiResponse;
import team.seventhmile.tripforp.external.weather.dto.WeatherApiResponse.Day;
import team.seventhmile.tripforp.external.weather.dto.WeatherDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherService {
    @Value("${weather.api.key}")
    private String key;
    private final RestTemplate restTemplate;

    public WeatherApiResponse getWeatherApi(String area) {
        String url = String.format(
            "http://api.openweathermap.org/data/2.5/forecast?q=%s&lang=kr&appid=%s&units=metric",
            area, key);
        return restTemplate.getForObject(url, WeatherApiResponse.class);
    }

    public List<WeatherDto> getWeather(DateAndAreaRequest request) {
        WeatherApiResponse response = getWeatherApi(DateAndAreaRequest.convertToEnglish(request.getArea()));
        List<Day> list = new ArrayList<>();
        for (Day day : response.getList()) {
            LocalDate checkDate = LocalDate.parse(day.getDt_txt().split(" ")[0]);
            if (dateValidate(checkDate, request.getStartDate(), request.getEndDate())) {
                list.add(day);
            }
        }
        return list.stream()
            .map(WeatherDto::new)
            .toList();
    }

    private boolean dateValidate(LocalDate checkDate, LocalDate startDate, LocalDate endDate) {
        return !checkDate.isBefore(startDate) && !checkDate.isAfter(endDate);
    }

}
