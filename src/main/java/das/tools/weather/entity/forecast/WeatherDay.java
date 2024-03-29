package das.tools.weather.entity.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import das.tools.weather.entity.current.WeatherCondition;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonRootName("day")
public class WeatherDay {
    WeatherCondition condition;
    @JsonProperty("avghumidity")
    float avgHumidity;
    @JsonProperty("avgtemp_c")
    float avgTemperature;
    @JsonProperty("avgtemp_f")
    float avgTemperatureF;
    @JsonProperty("avgvis_km")
    float avgVisibilityKm;
    @JsonProperty("daily_chance_of_rain")
    byte chanceOfRain;
    @JsonProperty("daily_chance_of_snow")
    byte chanceOfSnow;
    @JsonProperty("daily_will_it_rain")
    boolean isWillBeRain;
    @JsonProperty("daily_will_it_snow")
    boolean isWillBeSnow;
    @JsonProperty("maxtemp_c")
    float maxTempC;
    @JsonProperty("mintemp_c")
    float minTempC;
    @JsonProperty("maxwind_kph")
    float maxWindKmh;
    @JsonProperty("totalprecip_mm")
    float totalPrecipitation;
    @JsonProperty("totalsnow_cm")
    float totalSnow;
    @JsonProperty("uv")
    float uvIndex;
}
