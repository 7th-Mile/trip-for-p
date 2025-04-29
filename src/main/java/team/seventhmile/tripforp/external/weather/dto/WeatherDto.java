package team.seventhmile.tripforp.external.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.external.weather.dto.WeatherApiResponse.Day;

@Getter
@NoArgsConstructor
public class WeatherDto {

    @JsonProperty("날짜 및 시간")
    private String datetime;
    @JsonProperty("기온")
    private String temperature;
    @JsonProperty("습도")
    private String humidity;
    @JsonProperty("상태")
    private String status;
    @JsonProperty("구름")
    private String cloud;
    @JsonProperty("풍속")
    private String wind;
    @JsonProperty("강수확률")
    private String pop;
    @JsonProperty("예상 강수량-3시간")
    private String rain;
    @JsonProperty("예상 강설량-3시간")
    private String snow;

    public WeatherDto(Day day) {
        this.datetime = day.getDt_txt();
        this.temperature = day.getMain().getTemp() + "°";
        this.humidity = day.getMain().getHumidity() + "%";
        this.status = convertStatusToKorean(day, status);
        this.cloud = day.getClouds().getAll() + "%";
        this.wind = day.getWind().getSpeed() + "m/s";
        this.pop = (int)(day.getPop() * 100) + "%";
        this.rain = day.getRain().getThreeHour() + "mm";
        this.snow = day.getSnow().getThreeHour() + "mm";
    }

    private String convertStatusToKorean(Day day, String status) {
        switch (day.getWeather().get(0).getMain()) {
            case "Clear":
                return "맑음";
            case "Clouds":
                return "구름";
            case "Rain":
                return "비";
            case "Snow":
                return "눈";
            case "Thunderstorm":
                return "뇌우";
            case "Drizzle":
                return "이슬비";
            case "Mist":
                return "옅은 안개";
            case "Fog":
                return "안개";
            case "Haze":
                return "연무";
            case "Smoke":
                return "연기";
            case "Dust":
            case "Sand":
                return "먼지";
            case "Squall":
                return "돌풍";
            case "Tornado":
                return "토네이도";
            default:
                return status;
        }
    }
}
