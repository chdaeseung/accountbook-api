package chdaeseung.accountbook.weather.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenWeatherForecastResponse {
    private List<ForecastItem> list;
    private CityInfo city;

    @Getter
    @Setter
    public static class ForecastItem {
        private MainInfo main;
        private List<WeatherInfo> weather;
        private Double pop;
        private Long dt;
        private String dt_txt;
    }

    @Getter
    @Setter
    public static class MainInfo {
        private double temp;
    }

    @Getter
    @Setter
    public static class WeatherInfo {
        private String main;
        private String description;
        private String icon;
    }

    @Getter
    @Setter
    public static class CityInfo {
        private Integer timezone;
    }
}
