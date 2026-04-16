package chdaeseung.accountbook.weather.scheduler;

import chdaeseung.accountbook.weather.service.WeatherStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherStoreService weatherStoreService;

    @Scheduled(cron = "0 0 0 * * *")
    public void saveTomorrowWeather() {
        try {
            log.info("내일 날씨 스케줄 시작");
            weatherStoreService.fetchAndStoreTomorrowForecast();
            log.info("내일 날씨 스케줄 완료");
        } catch (Exception e) {
            log.error("!!!! 내일 날씨 저장 스케쥴 오류 발생", e);
        }
    }
}
