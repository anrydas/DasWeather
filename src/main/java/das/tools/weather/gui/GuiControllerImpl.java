package das.tools.weather.gui;

import das.tools.weather.config.GuiConfig;
import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherAstro;
import das.tools.weather.entity.forecast.WeatherDay;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.service.AlertService;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.LocalizeResourcesService;
import das.tools.weather.service.WeatherService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
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
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class GuiControllerImpl implements GuiController {
    private Scene configScene;
    private Scene forecastScene;

    @Autowired private GuiConfigService configService;
    @Autowired private BuildProperties buildProperties;
    @Autowired private WeatherService weatherService;
    @Autowired private ConfigController configController;
    @Autowired private GuiConfig.ViewHolder guiConfigView;
    @Autowired private GuiConfig.ViewHolder guiForecastView;
    @Autowired private ForecastController forecastController;
    @Autowired private LocalizeResourcesService localizeService;
    @Autowired private AlertService alertService;

    @FXML private Label lbWindSpeedText;
    @FXML private Label lbFillsLikeText;
    @FXML private ImageView imgDayLength;
    @FXML private Label lbDayLength;
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

    @Override
    public void initLocale() {
        Map<String,String> map = WIND_DIRECTIONS;
        map.put("N", localizeService.getLocalizedResource("wind.n"));
        map.put("NNE", localizeService.getLocalizedResource("wind.nne"));
        map.put("NE", localizeService.getLocalizedResource("wind.ne"));
        map.put("ENE", localizeService.getLocalizedResource("wind.ene"));
        map.put("E", localizeService.getLocalizedResource("wind.e"));
        map.put("ESE", localizeService.getLocalizedResource("wind.ese"));
        map.put("SE", localizeService.getLocalizedResource("wind.se"));
        map.put("SSE", localizeService.getLocalizedResource("wind.sse"));
        map.put("S", localizeService.getLocalizedResource("wind.s"));
        map.put("SSW", localizeService.getLocalizedResource("wind.ssw"));
        map.put("SW", localizeService.getLocalizedResource("wind.sw"));
        map.put("WSW", localizeService.getLocalizedResource("wind.wsw"));
        map.put("W", localizeService.getLocalizedResource("wind.w"));
        map.put("WNW", localizeService.getLocalizedResource("wind.wnw"));
        map.put("NW", localizeService.getLocalizedResource("wind.nw"));
        map.put("NNW", localizeService.getLocalizedResource("wind.nnw"));

        map = MOON_PHASES;
        map.put("new-moon", localizeService.getLocalizedResource("moon.new-moon"));
        map.put("waxing-crescent", localizeService.getLocalizedResource("moon.waxing-crescent"));
        map.put("waning-gibbous", localizeService.getLocalizedResource("moon.waning-gibbous"));
        map.put("first-quarter", localizeService.getLocalizedResource("moon.first-quarter"));
        map.put("full-moon", localizeService.getLocalizedResource("moon.full-moon"));
        map.put("waxing-gibbous", localizeService.getLocalizedResource("moon.waxing-gibbous"));
        map.put("last-quarter", localizeService.getLocalizedResource("moon.last-quarter"));
        map.put("waning-crescent", localizeService.getLocalizedResource("moon.waning-crescent"));
    }

    @FXML
    private void initialize() {
        btUpdate.setOnAction(event -> updateWeatherData());
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

    @Override
    public void onShowingStage() {
        localizeService.initLocale();
        initLocale();
        setLocalizedResources();
    }

    private void setLocalizedResources() {
        btUpdate.setText(localizeService.getLocalizedResource("button.update.text"));
        btConfig.setTooltip(getTooltip(localizeService.getLocalizedResource("button.configure.tooltip")));
        lbFillsLikeText.setText(localizeService.getLocalizedResource("label.fillsLikeText.text"));
        lbWindSpeedText.setText(localizeService.getLocalizedResource("label.windSpeed.text"));
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

        fillConditions(current);
        fillLocation(current);

        fillTemperature(current);
        fillHumidity(current);

        WeatherDayForecast[] dayForecasts = this.dataHolder.getResponse().getForecast().getDayForecast();
        fillCloud(current);
        fillPrecipitation(dayForecasts[0].getDay());
        fillPressure(current);

        fillWind(current);

        fillVisibilityAndUvAndDayLen(current);
        fillDayLength(dayForecasts[0].getAstro());

        fillAirQuality(current);

        fillAstro(dayForecasts[0].getAstro());
        fillForecast(dayForecasts);

        btUpdate.setTooltip(getTooltip(String.format(localizeService.getLocalizedResource("button.update.tooltip"),
                new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm").format(Date.from(dataHolder.getLastUpdatedTimestamp()))
        )));
    }

    private void fillAirQuality(WeatherCurrent current) {
        String MSG_AIR_QUALITY = "CO=%.00f,   NO2=%.00f,   O3=%.00f,   SO2=%.00f";
        lbAirQuality.setText(String.format(MSG_AIR_QUALITY,
                current.getAirQuality().getCo(),
                current.getAirQuality().getNo2(),
                current.getAirQuality().getO3(),
                current.getAirQuality().getSo2())
        );
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("airQuality.tooltip"), lbAirQuality.getText()));
        ImageView iv = getTooltipImage(new Image(IMAGE_AIR_QUALITY_HINT_PNG), 100);
        tooltip.setGraphic(iv);
        lbAirQuality.setTooltip(tooltip);
        Tooltip.install(imgAirQuality, tooltip);
    }

    private void fillVisibilityAndUvAndDayLen(WeatherCurrent current) {
        lbVisibility.setText(String.format("%.0f km", current.getVisibilityKm()));
        Tooltip tooltipVisibility = getTooltip(String.format(localizeService.getLocalizedResource("visibility.tooltip"), current.getVisibilityKm()));
        ImageView iv = getTooltipImage(imgVisibility.getImage(), 100);
        tooltipVisibility.setGraphic(iv);
        lbVisibility.setTooltip(tooltipVisibility);
        Tooltip.install(imgVisibility, tooltipVisibility);

        lbUvIdx.setText(String.format("%.00f", current.getUvIndex()));
        Tooltip tooltipUv = getTooltip(
                String.format(localizeService.getLocalizedResource("uvIndex.tooltip"),
                        current.getUvIndex()
                )
        );
        iv = getTooltipImage(imgUvIndex.getImage(), 100);
        tooltipUv.setGraphic(iv);
        lbUvIdx.setTooltip(tooltipUv);
        Tooltip.install(imgUvIndex, tooltipUv);
    }

    private void fillLocation(WeatherCurrent current) {
        String updateDate = current.getLastUpdate().split(" ")[0];
        String updateTime = current.getLastUpdate().split(" ")[1];
        lbLocation.setText(String.format(localizeService.getLocalizedResource("label.location.text"),
                this.dataHolder.getResponse().getLocation().getName(),
                updateDate,
                updateTime
        ));
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("location.tooltip"),
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

    private void fillConditions(WeatherCurrent current) {
        imgWeather.setImage(this.dataHolder.getImage());
        String conditionText = current.getCondition().getText();
        Tooltip.install(imgWeather, getTooltip(conditionText));
        lbCondition.setText(conditionText);
        lbCondition.setTooltip(getTooltip(conditionText));
    }

    private void fillTemperature(WeatherCurrent current) {
        lbTemperature.setText(String.format("%.0f℃", current.getTemperatureC()));
        lbFills.setText(String.format("%.0f℃", current.getFeelsLike()));
        String imageName = current.getTemperatureC() > 0 ? IMAGE_TEMP_HOT_PNG : IMAGE_TEMP_COLD_PNG;
        Image image = new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(imageName)));
        imgTemperature.setImage(image);
        ImageView iv = getTooltipImage(image, 100);
        Tooltip tooltip = getTooltip(
                String.format(localizeService.getLocalizedResource("temperature.tooltip"),
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

    private void fillHumidity(WeatherCurrent current) {
        lbHumidity.setText(String.format("%d％", current.getHumidity()));
        ImageView iv = getTooltipImage(imgHumidity.getImage(), 100);
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("humidity.tooltip"), current.getHumidity()));
        tooltip.setGraphic(iv);
        lbHumidity.setTooltip(tooltip);
        Tooltip.install(imgHumidity, tooltip);
    }

    private void fillPressure(WeatherCurrent current) {
        lbPressure.setText(String.format("%.0f mmHg", millibarToMmHg(current.getPressureMb())));
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("pressure.tooltip"), millibarToMmHg(current.getPressureMb()), current.getPressureMb()));
        tooltip.setGraphic(getTooltipImage(imgPressure.getImage(), 100));
        lbPressure.setTooltip(tooltip);
        Tooltip.install(imgPressure, tooltip);
    }

    public static ImageView getTooltipImage(Image image, int width) {
        ImageView iv = new ImageView(image);
        iv.setPreserveRatio(true);
        iv.setFitWidth(width);
        return iv;
    }

    private void fillCloud(WeatherCurrent current) {
        lbCloud.setText(String.format("%d％", current.getCloud()));
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("cloud.tooltip"), current.getCloud()));
        tooltip.setGraphic(getTooltipImage(imgCloud.getImage(), 100));
        lbCloud.setTooltip(tooltip);
        Tooltip.install(imgCloud, tooltip);
    }

    private void fillWind(WeatherCurrent current) {
        lbWindDirection.setText(current.getWindDirection());
        lbWindSpeed.setText(String.format("%.0f km/h", current.getWindKmh()));
        lbWindGists.setText(String.format("%.0f km/h", current.getGust()));

        imgWindDirection.setRotate(current.getWindDegree());
        Tooltip tooltip = getTooltip(
                String.format(localizeService.getLocalizedResource("wind.tooltip"),
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

    private void fillPrecipitation(WeatherDay day) {
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

        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("precipitation.tooltip"), totalPrecipitation, totalSnow));
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

    private void fillForecast(WeatherDayForecast[] dayForecasts) {
        imgForecast01.setImage(this.dataHolder.getImage());
        imgForecast02.setImage(this.dataHolder.getImageForecast1());
        imgForecast03.setImage(this.dataHolder.getImageForecast2());

        lbForecast01.setText(DATE_FORMATTER_FOR_VIEW.format(LocalDate.now()));
        LocalDate dt1 = LocalDate.parse(dayForecasts[1].getDate(), DATE_FORMATTER_FOR_RESPONSE);
        lbgForecast02.setText(DATE_FORMATTER_FOR_VIEW.format(dt1));
        LocalDate dt2 = LocalDate.parse(dayForecasts[2].getDate(), DATE_FORMATTER_FOR_RESPONSE);
        lbgForecast03.setText(DATE_FORMATTER_FOR_VIEW.format(dt2));

        final String TOOLTIP_TEXT = localizeService.getLocalizedResource("forecast.tooltip");
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

        final String LABEL_TEXT = localizeService.getLocalizedResource("label.forecast");
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

    private void fillDayLength(WeatherAstro currentAstro) {
        String dayLength = forecastController.getTimeLength(currentAstro.getSunRise(), currentAstro.getSunSet());
        lbDayLength.setText(dayLength);
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("dayLength.tooltip"), dayLength));
        tooltip.setGraphic(getTooltipImage(imgDayLength.getImage(), 100));
        lbDayLength.setTooltip(tooltip);
        Tooltip.install(imgDayLength, tooltip);
    }

    private void fillAstro(WeatherAstro currentAstro) {
        lbSunRise.setText(getProperlyFormattedTime(currentAstro.getSunRise()));
        lbSunSet.setText(getProperlyFormattedTime(currentAstro.getSunSet()));
        Tooltip dayLength = getTooltip(String.format(localizeService.getLocalizedResource("dayLength.tooltip"),
                forecastController.getTimeLength(currentAstro.getSunRise(), currentAstro.getSunSet())));
        lbSunRise.setTooltip(dayLength);
        lbSunSet.setTooltip(dayLength);
        Tooltip.install(imgSunRise, dayLength);
        Tooltip.install(imgSunSet, dayLength);

        String moonPhase = currentAstro.getMoonPhase();
        String moonPhaseLocalized = MOON_PHASES.get(getMoonPhaseAcronim(moonPhase));
        lbMoonRise.setText(getProperlyFormattedTime(currentAstro.getMoonRise()));
        imgMoonPhase.setImage(new Image(getMoonPhaseImageName(moonPhase)));
        lbMoonPhase.setText(moonPhaseLocalized);
        lbMoonSet.setText(getProperlyFormattedTime(currentAstro.getMoonSet()));
        Tooltip moonPhaseTooltip = getTooltip(moonPhaseLocalized);
        Tooltip.install(imgMoonPhase, moonPhaseTooltip);
        lbMoonPhase.setTooltip(moonPhaseTooltip);
    }

    private String getProperlyFormattedTime(String time) {
        String res;
        try {
            LocalTime dt = LocalTime.parse(time, TIME_FORMATTER_FOR_RESPONSE);
            res = TIME_FORMATTER_FOR_VIEW.format(dt);
            if (log.isDebugEnabled()) log.debug("got formatted time: {}", res);
        } catch (DateTimeParseException e) {
            log.error("Time formatting error: ", e);
            res = "n/a";
        }
        return res;
    }

    private String getMoonPhaseImageName(String phase) {
        return "/images/moon/" + getMoonPhaseAcronim(phase) + ".png";
    }

    private String getMoonPhaseAcronim(String phase) {
        return phase.toLowerCase().replace(" ", "-");
    }

    private void showConfigWindow() {
        configScene = configScene == null ? new Scene(guiConfigView.getView()) : configScene;
        Stage stage = new Stage();
        stage.setTitle(String.format("Das Weather Config (v.%s)", buildProperties.getVersion()));
        stage.setScene(configScene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnShowing(windowEvent -> configController.onShowingStage());
        configController.initLocale();
        stage.showAndWait();
        if (configController.isConfigChanged()) {
            localizeService.initLocale();
            setLocalizedResources();
            updateWeatherDataForce();
        }
    }

    private void showForecastWindow() {
        forecastScene = forecastScene == null ? new Scene(guiForecastView.getView()) : forecastScene;
        Stage stage = new Stage();
        stage.setTitle(String.format("Das Weather Forecast (v.%s)", buildProperties.getVersion()));
        stage.setScene(forecastScene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnShowing(windowEvent -> forecastController.onShowing());
        forecastController.initLocale();
        forecastController.setData(this.dataHolder.getResponse());
        stage.showAndWait(); // ToDo: find possibility to show many forecast windows
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
                dataHolder.setImage(weatherService.getWeatherIcon(dataHolder.getResponse().getCurrent().getCondition().getCode(), dataHolder.getResponse().getCurrent().isDay()));
                dataHolder.setLastUpdatedTimestamp(Instant.now());
                updateProgress(30, MAX_VALUE);
                Thread.sleep(10);
                dataHolder.setImageForecast1(weatherService.getWeatherIcon(
                        dataHolder.getResponse().getForecast().getDayForecast()[1].getDay().getCondition().getCode(), true));
                updateProgress(40, MAX_VALUE);
                Thread.sleep(10);
                dataHolder.setImageForecast2(weatherService.getWeatherIcon(
                        dataHolder.getResponse().getForecast().getDayForecast()[2].getDay().getCondition().getCode(), true));
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
        task.setOnSucceeded(e -> updateControls());
        task.setOnFailed(e -> {
            pb.setVisible(false);
            btUpdate.setDisable(false);
            alertService.showError(task.getException().getCause().getLocalizedMessage(), "");
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
