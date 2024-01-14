package das.tools.weather.gui;

import das.tools.weather.config.GuiConfig;
import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherAstro;
import das.tools.weather.entity.forecast.WeatherDay;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.WeatherService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class GuiControllerImpl implements GuiController {
    public static final String APPLICATION_TITLE = "Das Weather: %s %s";
    protected static final int MINIMAL_UPDATE_INTERVAL = 1800000;
    protected static final DateTimeFormatter DATE_FORMATTER_FOR_RESPONSE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected static final DateTimeFormatter DATE_FORMATTER_FOR_VIEW = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    protected static final DateTimeFormatter TIME_FORMATTER_FOR_VIEW = DateTimeFormatter.ofPattern("HH:mm");
    private final RemoteDataHolder dataHolder = RemoteDataHolder.builder().build();
    private Scene configScene;
    private Scene forecastScene;

    @Autowired private BuildProperties buildProperties;
    @Autowired private WeatherService weatherService;
    @Autowired private GuiConfigService configService;
    @Autowired private ConfigController configController;
    @Autowired private GuiConfig.ViewHolder guiConfigView;
    @Autowired private GuiConfig.ViewHolder guiForecastView;
    @Autowired private ForecastController forecastController;

    @FXML private ImageView imgAirQuality;
    @FXML private ImageView imgWindGists;
    @FXML private ImageView imgVisibility;
    @FXML private ImageView imgUvIndex;
    @FXML private ImageView imgWind;
    @FXML private ImageView imgHumidity;
    @FXML private ImageView imgTemperature;
    @FXML private ImageView imgPressure;
    @FXML private ImageView imgCloud;
    @FXML private ImageView imgPrecipitation;
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
    @FXML private Label lbWindGists;
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

    static {
        Map<String,String> mapDirection = WIND_DIRECTIONS;
        mapDirection.put("N", "North");
        mapDirection.put("NNE", "North-Northeast");
        mapDirection.put("NE", "Northeast");
        mapDirection.put("ENE", "East-Northeast");
        mapDirection.put("E", "East");
        mapDirection.put("ESE", "East-Southeast");
        mapDirection.put("SE", "Southeast");
        mapDirection.put("SSE", "South-Southeast");
        mapDirection.put("S", "South");
        mapDirection.put("SSW", "South-Southwest");
        mapDirection.put("SW", "Southwest");
        mapDirection.put("WSW", "West-Southwest");
        mapDirection.put("W", "West");
        mapDirection.put("WNW", "West-Northwest");
        mapDirection.put("NW", "Northwest");
        mapDirection.put("NNW", "North-Northwest");
    }

    public GuiControllerImpl() {
    }

    @FXML
    private void initialize() {
        btUpdate.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");
        btUpdate.setOnAction(event -> updateWeatherData());
        btConfig.setTooltip(getTooltip("Configure Application"));
        btConfig.setOnAction(actionEvent -> showConfigWindow());

        imgConfigure.setImage(new Image(IMAGE_CONFIGURE_PNG));
        imgWindDirection.setImage(new Image(IMAGE_WIND_ARROW_PNG));
        imgSunRise.setImage(new Image(IMAGE_SUNRISE_PNG));
        imgSunSet.setImage(new Image(IMAGE_SUNSET_PNG));
        imgMoonRise.setImage(new Image(IMAGE_MOONRISE_PNG));
        imgMoonSet.setImage(new Image(IMAGE_MOONSET_PNG));

        imgForecast01.setOnMouseClicked(mouseEvent -> showForecastWindow());
        imgForecast02.setOnMouseClicked(mouseEvent -> showForecastWindow());
        imgForecast03.setOnMouseClicked(mouseEvent -> showForecastWindow());
    }

    private void updateControls() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();

        Stage stage = (Stage) lbLocation.getScene().getWindow();
        stage.getIcons().clear();
        stage.getIcons().add(this.dataHolder.getImage());
        stage.setTitle(String.format(APPLICATION_TITLE,
                this.dataHolder.getResponse().getLocation().getName(),
                current.getLastUpdate()
        ));

        fillConditions();
        fillLocation();

        fillTemperature();
        fillHumidity();

        fillCloud();
        fillPrecipitation();
        fillPressure();

        fillWind();

        fillVisibilityAndUv();

        fillAirQuality();

        fillAstro();
        fillForecast();

        btUpdate.setTooltip(getTooltip(String.format("Last Time updated %s",
                new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm").format(Date.from(dataHolder.getLastUpdatedTimestamp()))
        )));
    }

    private void fillAirQuality() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        String MSG_AIR_QUALITY = "CO=%.00f,   NO2=%.00f,   O3=%.00f,   SO2=%.00f";
        lbAirQuality.setText(String.format(MSG_AIR_QUALITY,
                current.getAirQuality().getCo(),
                current.getAirQuality().getNo2(),
                current.getAirQuality().getO3(),
                current.getAirQuality().getSo2())
        );
        Tooltip tooltip = getTooltip(String.format("Air Quality:\n%s", lbAirQuality.getText()));
        ImageView iv = getTooltipImage(new Image(IMAGE_AIR_QUALITY_HINT_PNG), 100);
        tooltip.setGraphic(iv);
        lbAirQuality.setTooltip(tooltip);
        Tooltip.install(imgAirQuality, tooltip);
    }

    private void fillVisibilityAndUv() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        lbVisibility.setText(String.format("%.0f km", current.getVisibilityKm()));
        Tooltip tooltipVisibility = getTooltip(String.format("Visibility: %.0f km", current.getVisibilityKm()));
        ImageView iv = getTooltipImage(imgVisibility.getImage(), 100);
        tooltipVisibility.setGraphic(iv);
        lbVisibility.setTooltip(tooltipVisibility);
        Tooltip.install(imgVisibility, tooltipVisibility);

        lbUvIdx.setText(String.format("%.00f", current.getUvIndex()));
        Tooltip tooltipUv = getTooltip("UV Index:\n0-2 - OK, Green\n3-5 - Yellow, recommended to be inside\n6-7 - Orange\n8-10 - Red\n11+ - Violet, Dangerous");
        iv = getTooltipImage(imgUvIndex.getImage(), 100);
        tooltipUv.setGraphic(iv);
        lbUvIdx.setTooltip(tooltipUv);
        Tooltip.install(imgUvIndex, tooltipUv);
    }

    private void fillLocation() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        String updateDate = current.getLastUpdate().split(" ")[0];
        String updateTime = current.getLastUpdate().split(" ")[1];
        lbLocation.setText(String.format("%s %s at %s",
                this.dataHolder.getResponse().getLocation().getName(),
                updateDate,
                updateTime
        ));
        Tooltip tooltip = getTooltip(String.format("In %s, %s %s at %s\nthe weather is '%s'",
                this.dataHolder.getResponse().getLocation().getName(),
                this.dataHolder.getResponse().getLocation().getRegion(),
                updateDate,
                updateTime,
                current.getCondition().getText()
        ));
        ImageView iv = getTooltipImage(imgWeather.getImage(), 100);
        tooltip.setGraphic(iv);
        lbLocation.setTooltip(tooltip);
    }

    private void fillConditions() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        imgWeather.setImage(this.dataHolder.getImage());
        String conditionText = current.getCondition().getText();
        Tooltip.install(imgWeather, getTooltip(conditionText));
        lbCondition.setText(conditionText);
        lbCondition.setTooltip(getTooltip(conditionText));
    }

    private void fillTemperature() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        lbTemperature.setText(String.format("%.0f℃", current.getTemperatureC()));
        lbFills.setText(String.format("%.0f℃", current.getFeelsLike()));
        String imageName = current.getTemperatureC() > 0 ? IMAGE_TEMP_HOT_PNG : IMAGE_TEMP_COLD_PNG;
        Image image = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(imageName)));
        imgTemperature.setImage(image);
        ImageView iv = getTooltipImage(image, 100);
        Tooltip tooltip = getTooltip(
                String.format("Temperature: %.0f℃  %.0f°F\nFills Like:%.0f℃  %.0f°F",
                        current.getTemperatureC(),
                        current.getTemperatureF(),
                        current.getFeelsLike(),
                        current.getFeelsLikeF()
                )
        );
        tooltip.setGraphic(iv);
        lbTemperature.setTooltip(tooltip);
        lbFills.setTooltip(tooltip);
        Tooltip.install(imgTemperature, tooltip);
    }

    private void fillHumidity() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        lbHumidity.setText(String.format("%d％", current.getHumidity()));
        ImageView iv = getTooltipImage(imgHumidity.getImage(), 100);
        Tooltip tooltip = getTooltip(String.format("Humidity: %d％", current.getHumidity()));
        tooltip.setGraphic(iv);
        lbHumidity.setTooltip(tooltip);
        Tooltip.install(imgHumidity, tooltip);
    }

    private void fillPressure() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        lbPressure.setText(String.format("%.0f mmHg", millibarToMmHg(current.getPressureMb())));
        Tooltip tooltip = getTooltip(String.format("Pressure:\n%.0f mmHg\n%.0f mBar", millibarToMmHg(current.getPressureMb()), current.getPressureMb()));
        tooltip.setGraphic(getTooltipImage(imgPressure.getImage(), 100));
        lbPressure.setTooltip(tooltip);
        Tooltip.install(imgPressure, tooltip);
    }

    private ImageView getTooltipImage(Image image, int width) {
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setFitWidth(width);
        return iv;
    }

    private void fillCloud() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();
        lbCloud.setText(String.format("%d％", current.getCloud()));
        Tooltip tooltip = getTooltip(String.format("Cloud: %d％", current.getCloud()));
        tooltip.setGraphic(getTooltipImage(imgCloud.getImage(), 100));
        lbCloud.setTooltip(tooltip);
        Tooltip.install(imgCloud, tooltip);
    }

    private void fillWind() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();

        lbWindDirection.setText(current.getWindDirection());
        lbWindSpeed.setText(String.format("%.0f km/h", current.getWindKmh()));
        lbWindGists.setText(String.format("%.0f km/h", current.getGust()));

        imgWindDirection.setRotate(current.getWindDegree());
        Tooltip tooltip = getTooltip(
                String.format("Wind direction: %s - %s wind (%d degree)\nSpeed: %.0f km/h  %.0f mp/h\nGists: %.0f km/h  %.0f mp/h",
                        current.getWindDirection(),
                        getWindDirection(current.getWindDirection()),
                        current.getWindDegree(),
                        current.getWindKmh(),
                        current.getWindMph(),
                        current.getGust(),
                        current.getGustMph()
                )
        );
        ImageView iv = getTooltipImage(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(IMAGE_COMPASS_ARROW_PNG))), 40);
        iv.setRotate(current.getWindDegree());
        tooltip.setGraphic(iv);
        lbWindDirection.setTooltip(tooltip);
        lbWindSpeed.setTooltip(tooltip);
        lbWindGists.setTooltip(tooltip);
        Tooltip.install(imgWindDirection, tooltip);
        Tooltip.install(imgWind, tooltip);
        Tooltip.install(imgWindGists, tooltip);
    }

    private String getWindDirection(String s) {
        return WIND_DIRECTIONS.get(s);
    }

    private void fillPrecipitation() {
        WeatherDay day = this.dataHolder.getResponse().getForecast().getDayForecast()[0].getDay();

        float totalPrecipitation = day.getTotalPrecipitation();
        float totalSnow = day.getTotalSnow();
        imgPrecipitation.setImage(null);
        Image image = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(getPrecipitationImageResourceFileName(day))));
        imgPrecipitation.setImage(image);

        if (totalPrecipitation > 0 && totalSnow > 0) {
            lbPrecipitation.setText(String.format("%.0f/%.0f", totalPrecipitation, totalSnow));
        } else if (totalPrecipitation > 0 && totalSnow <= 0) {
            lbPrecipitation.setText(String.format("%.0f mm", totalPrecipitation));
        } else if (totalPrecipitation <= 0 && totalSnow > 0) {
            lbPrecipitation.setText(String.format("%.0f cm", totalSnow));
        } else {
            lbPrecipitation.setText("0 mm");
        }

        Tooltip tooltip = getTooltip(String.format("Precipitation:\nRain: %.0f mm\nShow: %.0f cm", totalPrecipitation, totalSnow));
        tooltip.setTextAlignment(TextAlignment.RIGHT);
        tooltip.setGraphic(getTooltipImage(image, 100));
        lbPrecipitation.setTooltip(tooltip);
        Tooltip.install(imgPrecipitation, tooltip);
    }

    private String getPrecipitationImageResourceFileName(WeatherDay day) {
        String res;
        boolean isWillRain = day.isWillBeRain();
        boolean isWillSnow = day.isWillBeSnow();
        if (isWillRain && isWillSnow) {
            res = IMAGE_SNOW_AND_RAIN_PNG;
        } else if (!isWillRain && isWillSnow) {
            res = IMAGE_SNOW_PNG;
        } else if (isWillRain && !isWillSnow) {
            res = IMAGE_RAIN_PNG;
        } else {
            res = IMAGE_NO_PRECIPITATION_PNG;
        }
        return res;
    }

    private void fillForecast() {
        imgForecast01.setImage(this.dataHolder.getImage());
        imgForecast02.setImage(this.dataHolder.getImageForecast1());
        imgForecast03.setImage(this.dataHolder.getImageForecast2());

        WeatherDayForecast[] dayForecasts = this.dataHolder.getResponse().getForecast().getDayForecast();
        lbForecast01.setText(DATE_FORMATTER_FOR_VIEW.format(LocalDate.now()));
        LocalDate dt1 = LocalDate.parse(dayForecasts[1].getDate(), DATE_FORMATTER_FOR_RESPONSE);
        lbgForecast02.setText(DATE_FORMATTER_FOR_VIEW.format(dt1));
        LocalDate dt2 = LocalDate.parse(dayForecasts[2].getDate(), DATE_FORMATTER_FOR_RESPONSE);
        lbgForecast03.setText(DATE_FORMATTER_FOR_VIEW.format(dt2));

        final String TOOLTIP_TEXT = "%s\nfrom %.0fc to %.0f℃";
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
        lbSunRise.setText(getProperlyFormattedTime(currentAstro.getSunRise()));
        lbSunSet.setText(getProperlyFormattedTime(currentAstro.getSunSet()));
        Tooltip dayLength = getTooltip(String.format("Day length: %s", getTimeLength(currentAstro.getSunRise(), currentAstro.getSunSet())));
        lbSunRise.setTooltip(dayLength);
        lbSunSet.setTooltip(dayLength);
        Tooltip.install(imgSunRise, dayLength);
        Tooltip.install(imgSunSet, dayLength);

        lbMoonRise.setText(getProperlyFormattedTime(currentAstro.getMoonRise()));
        imgMoonPhase.setImage(new Image(getMoonPhaseImageName(currentAstro.getMoonPhase())));
        lbMoonPhase.setText(currentAstro.getMoonPhase());
        lbMoonSet.setText(getProperlyFormattedTime(currentAstro.getMoonSet()));
        Tooltip moonPhaseTooltip = getTooltip(currentAstro.getMoonPhase());
        Tooltip.install(imgMoonPhase, moonPhaseTooltip);
        lbMoonPhase.setTooltip(moonPhaseTooltip);
    }

    private String getProperlyFormattedTime(String time) {
        String res;
        try {
            LocalTime dt = LocalTime.parse(time, TIME_FORMATTER_FOR_RESPONSE);
            res = TIME_FORMATTER_FOR_VIEW.format(dt);
            log.debug("got formatted time: {}", res);
        } catch (DateTimeParseException e) {
            log.error("Time formatting error: ", e);
            res = "n/a";
        }
        return res;
    }

    private String getTimeLength(String start, String end) {
        LocalTime startTime = LocalTime.parse(start, TIME_FORMATTER_FOR_RESPONSE);
        LocalTime endTime = LocalTime.parse(end, TIME_FORMATTER_FOR_RESPONSE);
        long diff = Duration.between(startTime, endTime).getSeconds();
        long hours = diff / (60 * 60) % 24;
        long minutes = diff / (60) % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    private String getMoonPhaseImageName(String phase) {
        return "/images/moon/" + phase.toLowerCase().replace(" ", "-") + ".png";
    }

    private void showConfigWindow() {
        configScene = configScene == null ? new Scene(guiConfigView.getView()) : configScene;
        Stage stage = new Stage();
        stage.setTitle(String.format("Das Weather Config (v.%s)", buildProperties.getVersion()));
        stage.setScene(configScene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnShowing(windowEvent -> configController.onShowingStage());
        stage.showAndWait();
        if (configController.isConfigChanged()) {
            updateWeatherDataForce();
        }
    }

    private void showForecastWindow() {
        forecastScene = forecastScene == null ? new Scene(guiForecastView.getView()) : forecastScene;
        Stage stage = new Stage();
        stage.setTitle(String.format("Das Weather Forecast (v.%s)", buildProperties.getVersion()));
        stage.setScene(forecastScene);
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        //stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnShowing(windowEvent -> forecastController.onShowing());
        forecastController.setData(this.dataHolder.getResponse());
        stage.showAndWait();
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
            pb.setVisible(false);
            btUpdate.setDisable(false);
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    task.getException().getCause().getLocalizedMessage(),
                    ButtonType.OK);
            alert.show();
        });
        return task;
    }

    public static double millibarToMmHg(float mbar) {
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
