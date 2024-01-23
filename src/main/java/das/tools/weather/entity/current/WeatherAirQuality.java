package das.tools.weather.entity.current;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonRootName("air_quality")
public class WeatherAirQuality {
    float co;
    float no2;
    float o3;
    float so2;
}
