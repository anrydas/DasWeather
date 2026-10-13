package das.tools.weather.controller;

import das.tools.weather.gui.GuiController;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeatherController {
    private final GuiController guiController;

    public WeatherController(GuiController guiController) {
        this.guiController = guiController;
    }

    @Async
    @Scheduled(fixedDelay = 600000, initialDelay = 3000)
    public void updateWeather() {
        // GUI controls could be accessed on the FX Application Thread only
        Platform.runLater(guiController::updateWeatherData);
    }

    @Async
    @Scheduled(fixedDelay = 30000, initialDelay = 60000)
    public void setUptime() {
        Platform.runLater(guiController::setUptime);
    }
}
