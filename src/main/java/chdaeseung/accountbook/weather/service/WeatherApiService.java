package chdaeseung.accountbook.weather.service;

import chdaeseung.accountbook.weather.dto.OpenWeatherForecastResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class WeatherApiService {

    private final RestTemplate restTemplate;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.city}")
    private String city;

    @Value("${openweather.code}")
    private String code;

    public OpenWeatherForecastResponse getForecast() {
        String url = UriComponentsBuilder
                .fromUriString("https://api.openweathermap.org/data/2.5/forecast")
                .queryParam("q", city + "," + code)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "kr")
                .toUriString();

        return restTemplate.getForObject(url, OpenWeatherForecastResponse.class);
    }
}
