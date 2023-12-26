package das.tools.weather.gui;

import das.tools.weather.config.GuiConfig;
import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.WeatherService;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class GuiControllerImpl implements GuiController {
    public static final String APPLICATION_TITLE = "Das Weather: %s %s";
    protected static final int MINIMAL_UPDATE_INTERVAL = 1800000;
    private final RemoteDataHolder dataHolder = RemoteDataHolder.builder().build();
    @FXML
    private Label lbLocation;
    @FXML
    private Label lbCondition;
    @FXML
    private Label lbTemperature;
    @FXML
    private Label lbAdd1;
    @FXML
    private Label lbAdd2;
    @FXML
    private Button btUpdate;
    @FXML
    private Button btConfig;
    @FXML
    private ImageView imgWeather;
    @FXML
    private ProgressBar pb;
    @FXML
    private ImageView imgConfigure;

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private GuiConfigService configService;
    @Autowired
    private ConfigController configController;
    @Autowired
    private GuiConfig.ViewHolder guiConfigView;

    private Scene configScene;

    public GuiControllerImpl() {
    }

    @FXML
    private void initialize() {
        btUpdate.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");
        btUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateWeatherData();
            }
        });
        btConfig.setTooltip(getTooltip("Configure Application"));
        btConfig.setOnAction(actionEvent -> showConfigWindow());
        imgConfigure.setImage(new Image("/images/configure.png"));
    }

    private void updateControls() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        String updateDate = current.getLastUpdate().split(" ")[0];
        String updateTime = current.getLastUpdate().split(" ")[1];

        Stage stage = (Stage) lbLocation.getScene().getWindow();
        stage.getIcons().clear();
        stage.getIcons().add(this.dataHolder.getImage());

        imgWeather.setImage(this.dataHolder.getImage());
        String conditionText = current.getCondition().getText();
        Tooltip.install(imgWeather, getTooltip(conditionText));

        lbCondition.setText(conditionText);
        lbCondition.setTooltip(getTooltip(conditionText));
        stage.setTitle(String.format(APPLICATION_TITLE,
                this.dataHolder.getResponse().getLocation().getName(),
                current.getLastUpdate()
        ));

        String MSG_LOCATION = "%s %s at %s";
        lbLocation.setText(String.format(MSG_LOCATION,
                this.dataHolder.getResponse().getLocation().getName(),
                updateDate,
                updateTime
        ));
        lbLocation.setTooltip(getTooltip(String.format("%s, %s %s at %s",
                this.dataHolder.getResponse().getLocation().getName(),
                this.dataHolder.getResponse().getLocation().getRegion(),
                updateDate,
                updateTime
        )));

        String MSG_TEMPERATURE = "\uD83D\uDD25 %.0f℃ (fills %.0f℃) \uD83C\uDF2B %d％";
        lbTemperature.setText(String.format(MSG_TEMPERATURE,
                current.getTemperatureC(),
                current.getFeelsLike(),
                current.getHumidity()));
        lbTemperature.setTooltip(getTooltip("Temperature (Fills like) Humidity"));

        String MSG_ADD1 = "\uD83D\uDCA8 %s %.0f (upto %.0f) km/h";
        lbAdd1.setText(String.format(MSG_ADD1,
                current.getWindDirection(),
                current.getWindKmh(),
                current.getGust()
        ));
        lbAdd1.setTooltip(getTooltip("Wind direction, Wind speed (Gusts)"));

        String MSG_ADD2 = "\u2601 %d\uFF05  \u2614 %.0f mm  \uD83D\uDD3D %.0f mmHg";
        lbAdd2.setText(String.format(MSG_ADD2,
                current.getCloud(),
                current.getPrecipitation(),
                millibarToMmHg(current.getPressureMb())
        ));
        lbAdd2.setTooltip(getTooltip("Cloud, Precipitation, Pressure"));
        btUpdate.setTooltip(getTooltip(String.format("Last Time updated %s",
                new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm").format(Date.from(dataHolder.getLastUpdatedTimestamp()))
        )));
    }

    private void showConfigWindow() {
        configScene = configScene == null ? new Scene(guiConfigView.getView()) : configScene;
        Stage stage = new Stage();
        stage.setTitle("Weather Application Config");
        stage.setScene(configScene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnShowing(windowEvent -> configController.onShowingStage());
        stage.showAndWait();
        if (configController.isConfigChanged()) {
            updateWeatherDataForce();
        }
    }

    private static Tooltip getTooltip(String caption) {
        return new Tooltip(caption);
    }

    @Override
    public void updateWeatherData() {
        long updateInterval = Long.parseLong(configService.getConfigStringValue(GuiConfigService.GUI_CONFIG_UPDATE_INTERVAL_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_UPDATE_INTERVAL_KEY)));
        if (updateInterval < MINIMAL_UPDATE_INTERVAL) {
            updateInterval = MINIMAL_UPDATE_INTERVAL;
            if (log.isDebugEnabled()) log.debug("Update Interval corrected to {} msec.", MINIMAL_UPDATE_INTERVAL);
        }
        long lastUpdated = this.dataHolder.getLastUpdatedTimestamp() != null ? this.dataHolder.getLastUpdatedTimestamp().getEpochSecond() : 0;
        if (log.isDebugEnabled()) log.debug("got lastUpdated={}", lastUpdated);
        long msecsAfterUpdateData = Math.abs(Instant.now().getEpochSecond() - lastUpdated) * 1000;
        if (log.isDebugEnabled()) log.debug("after last data updated spent {} msec.", msecsAfterUpdateData);
        if (msecsAfterUpdateData >= updateInterval) {
            loadDataWithProgress();
        } else {
            log.info("Update Weather Data has been called but weather doesn't updated due to update interval doesn't reached yet.");
            if (log.isDebugEnabled()) log.debug("Update Weather Data has been called but after last data updated spent only {} msec " +
                    "with real update interval {} msec. So it doesn't updated.", msecsAfterUpdateData, updateInterval);
        }
    }

    @Override
    public void updateWeatherDataForce() {
        loadDataWithProgress();
    }

    private void loadDataWithProgress() {
        if (pb.progressProperty().isBound()) pb.progressProperty().unbind();
        final Task<Void> loadDataTask = getLoadingTask();
        pb.progressProperty().bind(loadDataTask.progressProperty());

        Thread thread = new Thread(loadDataTask, "progress-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private Task<Void> getLoadingTask() {
        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final int MAX_VALUE = 100;
                btUpdate.setDisable(true);
                pb.setVisible(true);
                updateProgress(10, MAX_VALUE);
                dataHolder.setResponse(weatherService.getForecastWeather());
                updateProgress(40, MAX_VALUE);
                Thread.sleep(10);
                dataHolder.setImage(weatherService.getRemoteImage(dataHolder.getResponse().getCurrent().getCondition().getIcon()));
                dataHolder.setLastUpdatedTimestamp(Instant.now());
                updateProgress(60, MAX_VALUE);
                Thread.sleep(10);
                updateProgress(80, MAX_VALUE);
                Thread.sleep(10);
                updateProgress(MAX_VALUE, MAX_VALUE);
                Thread.sleep(MAX_VALUE);
                pb.setVisible(false);
                btUpdate.setDisable(false);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            updateControls();
        });
        task.setOnFailed(e -> {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "There was error loading weather data.\nPlease try again later.",
                    ButtonType.OK);
            alert.show();
        });
        return task;
    }

    private double millibarToMmHg(float mbar) {
        return mbar * 0.750062;
    }

    @Getter @Setter @AllArgsConstructor @Builder
    static class RemoteDataHolder {
        private ForecastWeatherResponse response;
        private Image image;
        private Instant lastUpdatedTimestamp;
    }
}
