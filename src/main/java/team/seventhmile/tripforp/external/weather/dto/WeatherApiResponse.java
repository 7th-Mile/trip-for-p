package team.seventhmile.tripforp.external.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WeatherApiResponse {

    private List<Day> list = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    public static class Day {
        private String dt_txt;
        private Temperature main;
        private List<Weather> weather;
        private Cloud clouds;
        private Wind wind;
        private double pop;
        private Rain rain;
        private Snow snow;
        private Sys sys;

        public Rain getRain() {
            return rain != null ? rain : new Rain();
        }
        public Snow getSnow() {
            return snow != null ? snow : new Snow();
        }

    }
    @Getter
    @NoArgsConstructor
    public static class Temperature {
        private int temp;
        private int humidity;
    }
    @Getter
    @NoArgsConstructor
    public static class Weather {
        private String main;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    public static class Cloud {
        private int all;
    }

    @Getter
    @NoArgsConstructor
    public static class Wind {
        private double speed;
    }

    @Getter
    @NoArgsConstructor
    public static class Sys {
        private String pod;
    }

    @Getter
    @NoArgsConstructor
    public static class Rain {
        @JsonProperty("3h")
        private Double threeHour = 0.0;
    }

    @Getter
    @NoArgsConstructor
    public static class Snow {
        @JsonProperty("3h")
        private Double threeHour = 0.0;
    }

}
