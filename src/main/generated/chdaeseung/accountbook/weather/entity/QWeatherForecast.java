package chdaeseung.accountbook.weather.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWeatherForecast is a Querydsl query type for WeatherForecast
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeatherForecast extends EntityPathBase<WeatherForecast> {

    private static final long serialVersionUID = 1001848395L;

    public static final QWeatherForecast weatherForecast = new QWeatherForecast("weatherForecast");

    public final StringPath city = createString("city");

    public final DatePath<java.time.LocalDate> forecastDate = createDate("forecastDate", java.time.LocalDate.class);

    public final DateTimePath<java.time.LocalDateTime> forecastDateTime = createDateTime("forecastDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> hour = createNumber("hour", Integer.class);

    public final StringPath icon = createString("icon");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final NumberPath<Integer> precipitation = createNumber("precipitation", Integer.class);

    public final NumberPath<Double> temperature = createNumber("temperature", Double.class);

    public final StringPath weatherDescription = createString("weatherDescription");

    public final StringPath weatherMain = createString("weatherMain");

    public QWeatherForecast(String variable) {
        super(WeatherForecast.class, forVariable(variable));
    }

    public QWeatherForecast(Path<? extends WeatherForecast> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeatherForecast(PathMetadata metadata) {
        super(WeatherForecast.class, metadata);
    }

}

