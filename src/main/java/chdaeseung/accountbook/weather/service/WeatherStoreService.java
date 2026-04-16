package chdaeseung.accountbook.weather.service;

import chdaeseung.accountbook.weather.dto.OpenWeatherForecastResponse;
import chdaeseung.accountbook.weather.entity.WeatherForecast;
import chdaeseung.accountbook.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WeatherStoreService {

    private final WeatherApiService weatherApiService;
    private final WeatherRepository weatherRepository;

    @Value("${openweather.city}")
    private String city;

    private static final DateTimeFormatter FORECAST_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void fetchAndStoreTomorrowForecast() {
        OpenWeatherForecastResponse response = weatherApiService.getForecast();

        if(response == null || response.getList() == null || response.getList().isEmpty()) {
            log.warn("날씨 예보 응답 null");
            return;
        }

        if(response.getCity() == null || response.getCity().getTimezone() == null) {
            log.warn("도시 timezone 정보 null");
            return;
        }

        int timezone = response.getCity().getTimezone();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<OpenWeatherForecastResponse.ForecastItem> tomorrowForecastItems =
                response.getList().stream()
                        .filter(item -> isTomorrow(item, tomorrow, timezone))
                        .filter(item -> isTargetHour(item, timezone))
                        .toList();

        if(tomorrowForecastItems.isEmpty()) {
            log.warn("내일 날씨 데이터 없음 : {}", tomorrow);
            return;
        }

        for(OpenWeatherForecastResponse.ForecastItem item : tomorrowForecastItems) {
            LocalDateTime forecastDateTime = toLocalDateTime(item, timezone);
            int hour = forecastDateTime.getHour();
            Double temperature = item.getMain().getTemp();
            Integer precipitation = popToPercentage(item.getPop());
            OpenWeatherForecastResponse.WeatherInfo weatherInfo = item.getWeather().get(0);
            String weatherMain = weatherInfo.getMain();
            String weatherDescription = weatherInfo.getDescription();
            String icon = weatherInfo.getIcon();

            weatherRepository.findByCityAndForecastDateTime(city, forecastDateTime)
                    .ifPresentOrElse(
                            existingForecast -> existingForecast.update(
                                    temperature,
                                    precipitation,
                                    weatherMain,
                                    weatherDescription,
                                    icon,
                                    LocalDateTime.now()
                            ),
                            () -> weatherRepository.save(
                                    WeatherForecast.builder()
                                            .city(city)
                                            .forecastDate(tomorrow)
                                            .forecastDateTime(forecastDateTime)
                                            .hour(hour)
                                            .temperature(temperature)
                                            .precipitation(precipitation)
                                            .weatherMain(weatherMain)
                                            .weatherDescription(weatherDescription)
                                            .icon(icon)
                                            .lastUpdated(LocalDateTime.now())
                                            .build()
                            )
                    );
        }

        log.info("내일 날씨 저장 완료 city : {}, date : {}, size : {}", city, tomorrow, tomorrowForecastItems.size());
    }

    private boolean isTomorrow(OpenWeatherForecastResponse.ForecastItem item, LocalDate tomorrow, int timezone) {
        return toLocalDateTime(item, timezone).toLocalDate().equals(tomorrow);
    }

    private boolean isTargetHour(OpenWeatherForecastResponse.ForecastItem item, int timezone) {
        int hour = toLocalDateTime(item, timezone).getHour();
        return hour == 6 || hour == 9 || hour == 12 || hour == 15 || hour == 18 || hour == 21;
    }

    private LocalDateTime toLocalDateTime(OpenWeatherForecastResponse.ForecastItem item, int timezone) {
        return Instant.ofEpochSecond(item.getDt()).atOffset(ZoneOffset.ofTotalSeconds(timezone)).toLocalDateTime();
    }

    private Integer popToPercentage(Double pop) {
        if (pop == null) {
            return 0;
        }
        return (int) Math.round(pop * 100);
    }
}
