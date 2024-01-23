package das.tools.weather.entity.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonRootName("forecast")
public class WeatherForecast {
    @JsonProperty("forecastday")
    WeatherDayForecast[] dayForecast;
}
