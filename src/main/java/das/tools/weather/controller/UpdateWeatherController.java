package das.tools.weather.controller;

import das.tools.weather.gui.GuiController;
import das.tools.weather.gui.GuiControllerImpl;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

@Slf4j
public class UpdateWeatherController extends TimerTask {
    private boolean isFistRun = true;
    private static volatile UpdateWeatherController instance;

    public static UpdateWeatherController getInstance() {
        if (instance == null) {
            synchronized (UpdateWeatherController.class) {
                if (instance == null) {
                    instance = new UpdateWeatherController();
                }
            }
        }
        return instance;
    }

    public UpdateWeatherController() {
    }

    @Override
    public void run() {
        if (!isFistRun) {
            updateWeather();
        } else {
            isFistRun = false;
            if (log.isDebugEnabled()) log.debug("[UpdateWeatherController] skipped first run");
        }
    }

    public void updateWeather() {
        if (log.isDebugEnabled()) log.debug("[UpdateWeatherController] started updating");
        GuiController guiController = GuiControllerImpl.getInstance();
        // New thread needs to prevent IllegalStateException:
        // This operation is permitted on the event thread only; currentThread = task-1
        Thread t = new Thread(() -> Platform.runLater(guiController::updateWeatherData));
        t.start();
    }
}
