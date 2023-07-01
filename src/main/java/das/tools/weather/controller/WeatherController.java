package das.tools.weather.controller;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.gui.WeatherTrayIcon;
import das.tools.weather.service.WeatherService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherController {
    private final WeatherService service;
    private final WeatherTrayIcon trayIcon;

    public WeatherController(WeatherService service, WeatherTrayIcon trayIcon) {
        this.service = service;
        this.trayIcon = trayIcon;
    }

    @Async
    @Scheduled(fixedRate = 10800000) // Starts every 3 hours
    public void updateWeather() {
        ForecastWeatherResponse response = service.getForecastWeather();
        trayIcon.updateControls(response);
    }
}
