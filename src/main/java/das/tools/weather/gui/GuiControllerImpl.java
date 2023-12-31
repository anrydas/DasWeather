package das.tools.weather.gui;

import das.tools.weather.config.GuiConfig;
import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherAstro;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.WeatherService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@Slf4j
public class GuiControllerImpl implements GuiController {
    public static final String APPLICATION_TITLE = "Das Weather: %s %s";
    protected static final int MINIMAL_UPDATE_INTERVAL = 1800000;
    private final RemoteDataHolder dataHolder = RemoteDataHolder.builder().build();
    @FXML private Label lbForecastCond03;
    @FXML private Label lbForecastCond02;
    @FXML private Label lbForecastCond01;
    @FXML private ImageView imgForecast01;
    @FXML private Label lbForecast01;
    @FXML private ImageView imgForecast02;
    @FXML private Label lbgForecast02;
    @FXML private ImageView imgForecast03;
    @FXML private Label lbgForecast03;
    @FXML private ImageView imgMoonPhase;
    @FXML private Label lbMoonPhase;
    @FXML private Label lbHumidity;
    @FXML private Label lbFills;
    @FXML private Label lbWindSpeed;
    @FXML private Label lbWindGusts;
    @FXML private Label lbPrecipitation;
    @FXML private Label lbPressure;
    @FXML private Label lbVisibility;
    @FXML private Label lbUvIdx;
    @FXML public Label lbAirQuality;
    @FXML private ImageView imgSunRise;
    @FXML private Label lbSunRise;
    @FXML private ImageView imgSunSet;
    @FXML private Label lbSunSet;
    @FXML private ImageView imgMoonRise;
    @FXML private Label lbMoonRise;
    @FXML private ImageView imgMoonSet;
    @FXML private Label lbMoonSet;
    @FXML private Label lbLocation;
    @FXML private Label lbCondition;
    @FXML private Label lbTemperature;
    @FXML private Label lbWindDirection;
    @FXML private Label lbCloud;
    @FXML private Button btUpdate;
    @FXML private Button btConfig;
    @FXML private ImageView imgWeather;
    @FXML private ProgressBar pb;
    @FXML private ImageView imgConfigure;
    @FXML public ImageView imgWindDirection;

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
        imgWindDirection.setImage(new Image("/images/wind_arrow.png"));
        imgSunRise.setImage(new Image("/images/sunrise.png"));
        imgSunSet.setImage(new Image("/images/sunset.png"));
        imgMoonRise.setImage(new Image("/images/moonrise.png"));
        imgMoonSet.setImage(new Image("/images/moonset.png"));
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

        lbTemperature.setText(String.format("%.0f℃", current.getTemperatureC()));
        lbFills.setText(String.format("%.0f℃", current.getFeelsLike()));
        lbHumidity.setText(String.format("%d％", current.getHumidity()));

        lbWindDirection.setText(current.getWindDirection());
        lbWindSpeed.setText(String.format("%.0f km/h", current.getWindKmh()));
        lbWindGusts.setText(String.format("%.0f km/h", current.getGust()));

        lbCloud.setText(String.format("%d％", current.getCloud()));
        lbPrecipitation.setText(String.format("%.0f mm", current.getPrecipitation()));
        lbPressure.setText(String.format("%.0f mmHg", millibarToMmHg(current.getPressureMb())));

        lbVisibility.setText(String.format("%.0f km", current.getVisibilityKm()));
        lbUvIdx.setText(String.format("%.00f", current.getUvIndex()));
        lbUvIdx.setTooltip(getTooltip("0-2 - OK, Green\n3-5 - Yellow, recommended to be inside\n6-7 - Orange\n8-10 - Red\n11+ - Violet, Dangerous"));

        String MSG_AIR_QUALITY = "CO=%.00f,   NO2=%.00f,   O3=%.00f,   SO2=%.00f";
        lbAirQuality.setText(String.format(MSG_AIR_QUALITY,
                current.getAirQuality().getCo(),
                current.getAirQuality().getNo2(),
                current.getAirQuality().getO3(),
                current.getAirQuality().getSo2())
        );

        fillAstro();
        fillForecast();

        btUpdate.setTooltip(getTooltip(String.format("Last Time updated %s",
                new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm").format(Date.from(dataHolder.getLastUpdatedTimestamp()))
        )));

        imgWindDirection.setRotate(current.getWindDegree());
        Tooltip.install(imgWindDirection, getTooltip(String.format("Wind direction: %s (%d degree)", current.getWindDirection(), current.getWindDegree())));
    }

    private void fillForecast() {
        imgForecast01.setImage(this.dataHolder.getImage());
        imgForecast02.setImage(this.dataHolder.getImageForecast1());
        imgForecast03.setImage(this.dataHolder.getImageForecast2());

        WeatherDayForecast[] dayForecasts = this.dataHolder.getResponse().getForecast().getDayForecast();
        DateTimeFormatter formatterForView = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        DateTimeFormatter formatterForResponse = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        lbForecast01.setText(formatterForView.format(LocalDate.now()));
        LocalDate dt1 = LocalDate.parse(dayForecasts[1].getDate(), formatterForResponse);
        lbgForecast02.setText(formatterForView.format(dt1));
        LocalDate dt2 = LocalDate.parse(dayForecasts[2].getDate(), formatterForResponse);
        lbgForecast03.setText(formatterForView.format(dt2));

        final String TOOLTIP_TEXT = "%s\nfrom %.0f℃ to %.0f℃";
        Tooltip.install(imgForecast01, getTooltip(String.format(
                TOOLTIP_TEXT,
                this.dataHolder.getResponse().getCurrent().getCondition().getText(),
                dayForecasts[0].getDay().getMaxTempC(),
                dayForecasts[0].getDay().getMinTempC()
                ))
        );
        Tooltip.install(imgForecast02, getTooltip(String.format(
                TOOLTIP_TEXT,
                dayForecasts[1].getDay().getCondition().getText(),
                dayForecasts[1].getDay().getMaxTempC(),
                dayForecasts[1].getDay().getMinTempC()
                ))
        );
        Tooltip.install(imgForecast03, getTooltip(String.format(
                TOOLTIP_TEXT,
                dayForecasts[2].getDay().getCondition().getText(),
                dayForecasts[2].getDay().getMaxTempC(),
                dayForecasts[2].getDay().getMinTempC()
        )));

        final String LABEL_TEXT = "%.0f℃ to %.0f℃";
        lbForecastCond01.setText(String.format(
                LABEL_TEXT,
                dayForecasts[0].getDay().getMaxTempC(),
                dayForecasts[0].getDay().getMinTempC()
        ));
        lbForecastCond02.setText(String.format(
                LABEL_TEXT,
                dayForecasts[1].getDay().getMaxTempC(),
                dayForecasts[1].getDay().getMinTempC()
        ));
        lbForecastCond03.setText(String.format(
                LABEL_TEXT,
                dayForecasts[2].getDay().getMaxTempC(),
                dayForecasts[2].getDay().getMinTempC()
        ));
    }

    private void fillAstro() {
        WeatherAstro currentAstro = this.dataHolder.getResponse().getForecast().getDayForecast()[0].getAstro();
        lbSunRise.setText(currentAstro.getSunRise());
        lbSunSet.setText(currentAstro.getSunSet());
        lbMoonRise.setText(currentAstro.getMoonRise());
        imgMoonPhase.setImage(new Image(getMoonPhaseImageName(currentAstro.getMoonPhase())));
        lbMoonPhase.setText(currentAstro.getMoonPhase());
        lbMoonSet.setText(currentAstro.getMoonSet());
        Tooltip moonPhaseTooltip = getTooltip(currentAstro.getMoonPhase());
        Tooltip.install(imgMoonPhase, moonPhaseTooltip);
        lbMoonPhase.setTooltip(moonPhaseTooltip);
    }

    private String getMoonPhaseImageName(String phase) {
        return "/images/moon/" + phase.toLowerCase().replace(" ", "-") + ".png";
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
                updateProgress(20, MAX_VALUE);
                Thread.sleep(10);
                dataHolder.setImage(weatherService.getRemoteImage(dataHolder.getResponse().getCurrent().getCondition().getIcon()));
                dataHolder.setLastUpdatedTimestamp(Instant.now());
                updateProgress(30, MAX_VALUE);
                Thread.sleep(10);
                dataHolder.setImageForecast1(weatherService.getRemoteImage(dataHolder.getResponse().getForecast().getDayForecast()[1].getDay().getCondition().getIcon()));
                updateProgress(40, MAX_VALUE);
                Thread.sleep(10);
                dataHolder.setImageForecast2(weatherService.getRemoteImage(dataHolder.getResponse().getForecast().getDayForecast()[2].getDay().getCondition().getIcon()));
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
        private Image imageForecast1;
        private Image imageForecast2;
    }
}
