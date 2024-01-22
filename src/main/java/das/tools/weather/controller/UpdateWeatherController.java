package das.tools.weather.controller;

import das.tools.weather.gui.GuiController;
import das.tools.weather.gui.GuiControllerImpl;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class UpdateWeatherController implements Job {
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

    public void updateWeather() {
        if (log.isDebugEnabled()) log.debug("[UpdateWeatherController] started updating");
        GuiController guiController = GuiControllerImpl.getInstance();
        // New thread needs to prevent IllegalStateException:
        // This operation is permitted on the event thread only; currentThread = task-1
        Thread t = new Thread(() -> Platform.runLater(guiController::updateWeatherData));
        t.start();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        updateWeather();
    }
}
