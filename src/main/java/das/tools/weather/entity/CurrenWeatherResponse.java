package das.tools.weather.entity;

import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.current.WeatherLocation;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrenWeatherResponse {
    WeatherCurrent current;
    WeatherLocation location;
}
