package das.tools.weather.entity.forecast;

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
@JsonRootName("astro")
public class WeatherAstro {
    @JsonProperty("sunrise")
    String sunRise;
    @JsonProperty("sunset")
    String sunSet;
    @JsonProperty("moonrise")
    String moonRise;
    @JsonProperty("moonset")
    String moonSet;
    @JsonProperty("moon_phase")
    String moonPhase;
}
