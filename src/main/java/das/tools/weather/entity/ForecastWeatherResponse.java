package das.tools.weather.entity;

import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.current.WeatherLocation;
import das.tools.weather.entity.forecast.WeatherForecast;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastWeatherResponse {
    WeatherCurrent current;
    WeatherForecast forecast;
    WeatherLocation location;

}
