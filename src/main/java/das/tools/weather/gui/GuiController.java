package das.tools.weather.gui;

import java.util.HashMap;
import java.util.Map;

public interface GuiController {
    Map<String,String> WIND_DIRECTIONS = new HashMap<>();
    void updateWeatherData();
    void updateWeatherDataForce();
}
