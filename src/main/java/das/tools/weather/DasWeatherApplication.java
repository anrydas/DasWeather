package das.tools.weather;

import das.tools.weather.controller.UpdateWeatherController;
import das.tools.weather.gui.GuiController;
import das.tools.weather.service.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
public class DasWeatherApplication extends Application {
    public static final String APP_VERSION = "3.3.7-RELEASE";
    private final LocalizeResourcesService localizeService = LocalizeResourcesServiceImpl.getInstance();
    //private ScheduledExecutorService scheduler;
    private Scheduler scheduler;

    @Override
    public void start(Stage stage) {
        try(InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream("fxml/Main.fxml")) {
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            Scene scene = new Scene(loader.getRoot());
            stage.getIcons().add(LoadingService.getInstance().getResourceImage(GuiController.IMAGE_WEATHER_DEFAULT_ICON_PNG));
            stage.setTitle("Das Weather");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                boolean isConfirmExit = Boolean.parseBoolean(GuiConfigServiceImpl.getInstance().getConfigStringValue(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY, "true"));
                if (isConfirmExit) {
                    Optional<ButtonType> option = AlertService.getInstance().showConfirm(localizeService.getLocalizedResource("alert.app.exit.text"),
                            localizeService.getLocalizedResource("alert.app.exit.title"),
                            "Das Weather Application");
                    if (ButtonType.CANCEL.equals(option.orElse(null))) {
                        event.consume();
                        return;
                    }
                }
                try {
                    scheduler.shutdown(true);
                } catch (SchedulerException e) {
                    log.error("Error shutting down the scheduler: {}", e.getLocalizedMessage());
                }
                Platform.exit();
                System.exit(0);
            });
            GuiController guiController = loader.getController();
            stage.setOnShowing(event -> guiController.onShowingStage());
            stage.show();
            guiController.updateWeatherData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        scheduleUpdate();
    }

    private void scheduleUpdate() {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            scheduler = sf.getScheduler();
            Date startTime = DateBuilder.nextGivenSecondDate(null, 10);
            JobDetail job = newJob(UpdateWeatherController.class)
                    .withIdentity("WeatherUpdate", "group1")
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity("UpdateTrigger", "group1")
                    .startAt(startTime)
                    .withSchedule(
                            simpleSchedule().withIntervalInMinutes(10).repeatForever()
                    )
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
