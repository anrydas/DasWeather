package das.tools.weather.entity.current;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("current")
public class WeatherCurrent {
    @JsonProperty("air_quality")
    WeatherAirQuality airQuality;
    @JsonProperty("condition")
    WeatherCondition condition;
    int cloud;
    @JsonProperty("feelslike_c")
    float feelsLike;
    @JsonProperty("feelslike_f")
    float feelsLikeF;
    @JsonProperty("gust_kph")
    float gust; // Пориви вітру
    @JsonProperty("gust_mph")
    float gustMph;
    int humidity;
    @JsonProperty("is_day")
    boolean isDay;
    @JsonProperty("last_updated")
    String lastUpdate;
    @JsonProperty("last_updated_epoch")
    long lastUpdateEpoch;
    @JsonProperty("precip_mm")
    float precipitation; // опади
    @JsonProperty("pressure_mb")
    float pressureMb;
    @JsonProperty("temp_c")
    float temperatureC;
    @JsonProperty("temp_f")
    float temperatureF;
    @JsonProperty("uv")
    float uvIndex;
    @JsonProperty("vis_km")
    float visibilityKm;
    @JsonProperty("wind_degree")
    int windDegree;
    @JsonProperty("wind_dir")
    String windDirection;
    @JsonProperty("wind_kph")
    float windKmh;
    @JsonProperty("wind_mph")
    float windMph;
}
