package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;

import java.awt.*;

public interface WeatherTrayIcon {
    void updateControls(ForecastWeatherResponse response);
}
