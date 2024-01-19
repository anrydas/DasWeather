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
@JsonRootName("location")
public class WeatherLocation {
    String country;
    String localtime;
    @JsonProperty("lat")
    float latitude;
    @JsonProperty("lon")
    float longitude;
    String name;
    String region;
    @JsonProperty("tz_id")
    String timeZone;
}
