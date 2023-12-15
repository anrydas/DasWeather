package das.tools.weather.controller;

import das.tools.weather.gui.GuiControllerImpl;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WeatherController {
    private final GuiControllerImpl guiController;

    public WeatherController(GuiControllerImpl guiController) {
        this.guiController = guiController;
    }

    @Async
    @Scheduled(fixedRate = 600000, initialDelay = 3000)
    public void updateWeather() {
        // New thread needs to prevent IllegalStateException:
        // This operation is permitted on the event thread only; currentThread = task-1
        Thread t = new Thread(() -> {
            Platform.runLater(guiController::updateWeatherData);
        });
        t.start();
    }
}
