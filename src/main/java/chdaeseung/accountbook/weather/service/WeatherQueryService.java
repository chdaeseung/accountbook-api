package chdaeseung.accountbook.weather.service;

import chdaeseung.accountbook.weather.dto.WeatherTimelineDto;
import chdaeseung.accountbook.weather.entity.WeatherForecast;
import chdaeseung.accountbook.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherQueryService {

    private final WeatherRepository weatherRepository;

    @Value("${openweather.city}")
    private String city;

    public List<WeatherTimelineDto> getTomorrowWeatherTimeline() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<WeatherForecast> forecasts = weatherRepository.findAllByCityAndForecastDateOrderByForecastDateTimeAsc(city, tomorrow);

        return forecasts.stream()
                .map(this::toDto)
                .toList();
    }

    private WeatherTimelineDto toDto(WeatherForecast forecast) {
        return WeatherTimelineDto.builder()
                .timeLabel(String.format("%02d시", forecast.getHour()))
                .temperature(forecast.getTemperature())
                .precipitation(forecast.getPrecipitation())
                .weatherDescription(simpleWeatherDescription(forecast.getWeatherMain(), forecast.getWeatherDescription()))
                .iconUrl(buildIconUrl(forecast.getIcon()))
                .build();
    }

    private String buildIconUrl(String icon) {
        return "https://openweathermap.org/img/wn/" + icon + "@2x.png";
    }

    private String simpleWeatherDescription(String weatherMain, String weatherDescription) {
        if(weatherMain == null) {
            return "기타";
        }

        switch(weatherMain) {
            case "Clear":
                return "맑음";
            case "Clouds":
                return "흐림";
            case "Rain":
            case "Drizzle":
            case "Thunderstorm":
                return "비";
            case "Snow":
                return "눈";
            case "Mist":
            case "Fog":
            case "Haze":
                return "안개";
            default:
                return weatherDescription != null ? weatherDescription : "기타";
        }
    }
}
