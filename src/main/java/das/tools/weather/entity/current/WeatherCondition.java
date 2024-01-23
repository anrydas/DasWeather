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
@JsonRootName("condition")
public class WeatherCondition {
    int code;
    String icon;
    String text;
}
