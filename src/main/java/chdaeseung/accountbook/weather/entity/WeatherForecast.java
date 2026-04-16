package chdaeseung.accountbook.weather.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weather_forecasts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"city", "forecast_date_time"})
})
public class WeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private LocalDate forecastDate;

    @Column(name = "forecast_date_time", nullable = false)
    private LocalDateTime forecastDateTime;

    @Column(nullable = false)
    private Integer hour;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer precipitation;

    @Column(nullable = false)
    private String weatherMain;

    @Column(nullable = false)
    private String weatherDescription;

    @Column(nullable = false)
    private String icon;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Builder
    public WeatherForecast(String city, LocalDate forecastDate, LocalDateTime forecastDateTime, Integer hour, Double temperature, Integer precipitation, String weatherMain, String weatherDescription, String icon, LocalDateTime lastUpdated) {
        this.city = city;
        this.forecastDate = forecastDate;
        this.forecastDateTime = forecastDateTime;
        this.hour = hour;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.weatherMain = weatherMain;
        this.weatherDescription = weatherDescription;
        this.icon = icon;
        this.lastUpdated = lastUpdated;
    }

    public void update(Double temperature, Integer precipitation, String weatherMain, String weatherDescription, String icon, LocalDateTime lastUpdated) {
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.weatherMain = weatherMain;
        this.weatherDescription = weatherDescription;
        this.icon = icon;
        this.lastUpdated = lastUpdated;
    }
}
