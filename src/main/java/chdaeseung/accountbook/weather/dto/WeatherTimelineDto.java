package chdaeseung.accountbook.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WeatherTimelineDto {
    private String timeLabel;
    private Double temperature;
    private Integer precipitation;
    private String weatherDescription;
    private String iconUrl;
}
