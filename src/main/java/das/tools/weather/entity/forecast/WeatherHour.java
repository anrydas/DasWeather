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
@JsonRootName("hour")
public class WeatherHour {
    WeatherCondition condition;
    @JsonProperty("chance_of_rain")
    int chanceOfRain;
    @JsonProperty("chance_of_snow")
    int chanceOfSnow;
    @JsonProperty("cloud")
    int cloud;
    @JsonProperty("dewpoint_c")
    float dewPoint;
    @JsonProperty("feelslike_c")
    float fillsLike;
    @JsonProperty("gust_kph")
    float gustKmh;
    @JsonProperty("humidity")
    int humidity;
    @JsonProperty("is_day")
    boolean isDay;
    @JsonProperty("precip_mm")
    float precipitation;
    @JsonProperty("pressure_mb")
    float pressure;
    @JsonProperty("temp_c")
    float temperature;
    @JsonProperty("time")
    String time;
    @JsonProperty("time_epoch")
    String timeEpoch;
    @JsonProperty("uv")
    float uvIndex;
    @JsonProperty("vis_km")
    float visibilityKm;
    @JsonProperty("will_it_rain")
    boolean isWillBeRain;
    @JsonProperty("will_it_snow")
    boolean isWillBeSnow;
    @JsonProperty("wind_degree")
    int windDegree;
    @JsonProperty("wind_dir")
    String windDirection;
    @JsonProperty("wind_kph")
    float windKmh;
}
