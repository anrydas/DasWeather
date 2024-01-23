package das.tools.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import das.tools.weather.entity.current.WeatherLocation;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchLocationResponse {
    WeatherLocation[] locations;
}
