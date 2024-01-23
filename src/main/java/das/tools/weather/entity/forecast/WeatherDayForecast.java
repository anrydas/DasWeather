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
@JsonRootName("forecastday")
public class WeatherDayForecast {
    WeatherAstro astro;
    String date;
    @JsonProperty("date_epoch")
    long dateEpoch;
    WeatherDay day;
    WeatherHour[] hour;
}
