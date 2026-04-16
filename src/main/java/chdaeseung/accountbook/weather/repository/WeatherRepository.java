package chdaeseung.accountbook.weather.repository;

import chdaeseung.accountbook.weather.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherForecast, Long> {
    Optional<WeatherForecast> findByCityAndForecastDateTime(String city, LocalDateTime forecastDateTime);

    List<WeatherForecast> findAllByCityAndForecastDateOrderByForecastDateTimeAsc(String city, LocalDate forecastDate);
}
