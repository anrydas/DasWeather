package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherAstro;
import das.tools.weather.entity.forecast.WeatherDay;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngineFactory;
import das.tools.weather.service.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
@FxmlView("/fxml/Main.fxml")
@Slf4j
public class GuiControllerImpl implements GuiController {

    private final GuiConfigService configService;
    private final WeatherService weatherService;
    private final ConfigController configController;
    private final LocalizeResourcesService localizeService;
    private final AlertService alertService;
    private final FxWeaver fxWeaver;
    private final CommonUtilsService commonUtils;
    private final UptimeService uptimeService;
    private final CbwmService cbwmService;

    @FXML private AnchorPane root;
    @FXML private HBox airQualityBox;
    @FXML private Label lbForecast;
    @FXML private Label lbWindSpeedText;
    @FXML private ImageView imgFillsLikeTemp;
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
    @FXML private Label lbForecast02;
    @FXML private ImageView imgForecast03;
    @FXML private Label lbForecast03;
    @FXML private ImageView imgMoonPhase;
    @FXML private Label lbMoonPhase;
    @FXML private Label lbHumidity;
    @FXML private Label lbFeels;
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
    @FXML private VBox vboxForecast0;
    @FXML private VBox vboxForecast1;
    @FXML private VBox vboxForecast2;
    @FXML private VBox vboxForecast3;
    @FXML private Label lbAverage;
    @FXML private ImageView imgAvgTemp;
    @FXML private HBox uvIndexBox;
    @FXML private HBox tempBox;
    @FXML private HBox fillsTempBox;
    @FXML private HBox avgTempBox;
    @FXML private HBox visibilityBox;
    @FXML private HBox humidityBox;
    @FXML private HBox pressureBox;
    @FXML private HBox windSpeedBox;
    @FXML private HBox windGustBox;
    @FXML private HBox dayLenBox;
    @FXML private HBox windBox;
    @FXML private HBox cloudyBox;
    @FXML private HBox precipBox;
    @FXML private Label lbUptime;
    @FXML private Label lbUptimeLabel;
    @FXML private HBox hbUptime;
    @FXML private GridPane gridPane;

    public GuiControllerImpl(GuiConfigService configService, WeatherService weatherService, ConfigController configController, LocalizeResourcesService localizeService, AlertService alertService, FxWeaver fxWeaver, CommonUtilsService commonUtils, UptimeService uptimeService, CbwmService cbwmService) {
        this.configService = configService;
        this.weatherService = weatherService;
        this.configController = configController;
        this.localizeService = localizeService;
        this.alertService = alertService;
        this.fxWeaver = fxWeaver;
        this.commonUtils = commonUtils;
        this.uptimeService = uptimeService;
        this.cbwmService = cbwmService;
    }

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
        root.setOnKeyPressed(event -> {
            if (event.isShiftDown()) {
                btUpdate.setText(localizeService.getLocalizedResource("button.update.now.text"));
            }
        });
        root.setOnKeyReleased(event -> {
            if (!event.isShiftDown()) {
                btUpdate.setText(localizeService.getLocalizedResource("button.update.text"));
            }
        });

        btUpdate.setOnMouseClicked(event -> {
            if (event.isShiftDown()) {
                updateWeatherDataForce();
            } else {
                updateWeatherData();
            }
        });

        btConfig.setOnAction(event -> showConfigWindow());

        imgConfigure.setImage(new Image(IMAGE_CONFIGURE_PNG));
        imgWindDirection.setImage(new Image(IMAGE_WIND_ARROW_PNG));
        imgSunRise.setImage(new Image(IMAGE_SUNRISE_PNG));
        imgSunSet.setImage(new Image(IMAGE_SUNSET_PNG));
        imgMoonRise.setImage(new Image(IMAGE_MOONRISE_PNG));
        imgMoonSet.setImage(new Image(IMAGE_MOONSET_PNG));

        vboxForecast0.setOnMouseClicked(event -> showForecastWindow());
        vboxForecast1.setOnMouseClicked(event -> showForecastWindow());
        vboxForecast2.setOnMouseClicked(event -> showForecastWindow());
        vboxForecast3.setOnMouseClicked(event -> showForecastWindow());

        gridPane.setHgap(2);
    }

    @Override
    public void show() {
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
        lbForecast.setText(localizeService.getLocalizedResource("label.forecast.text"));
    }

    private void updateControls() {
        WeatherCurrent current = this.dataHolder.getResponse().getCurrent();

        Stage stage = (Stage) lbLocation.getScene().getWindow();
        stage.getIcons().clear();
        stage.getIcons().add(this.dataHolder.getImage());
        stage.setTitle(String.format(APPLICATION_TITLE,
                this.dataHolder.getResponse().getLocation().getName(),
                current.getLastUpdate(),
                (new SimpleDateFormat("HH:mm")).format(new Date())
        ));

        fillConditions(current);
        fillLocation(current);

        fillTemperature(current);
        fillHumidity(current);

        WeatherDayForecast[] dayForecasts = this.dataHolder.getResponse().getForecast().getDayForecast();
        fillDailyAverageTemp(dayForecasts);
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

    private void fillDailyAverageTemp(WeatherDayForecast[] dayForecasts) {
        lbAverage.setText(String.format("%.0f℃", dayForecasts[0].getDay().getAvgTemperature()));
        Tooltip tooltip = getTooltip(String.format(
                localizeService.getLocalizedResource("temp.average.tooltip"),
                dayForecasts[0].getDay().getAvgTemperature(),
                dayForecasts[0].getDay().getAvgTemperatureF())
        );
        ImageView iv = getTooltipImage(imgAvgTemp.getImage(), 100);
        tooltip.setGraphic(iv);
        Tooltip.install(avgTempBox, tooltip);

        avgTempBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.TEMPERATURE).getColor((int) dayForecasts[0].getDay().getAvgTemperature())));
    }

    private void fillAirQuality(WeatherCurrent current) {
        String MSG_AIR_QUALITY = "Idx=%d, CO=%.1f, NO2=%.1f, O3=%.1f, SO2=%.1f";
        int colorIndex = current.getAirQuality().getIndex();
        lbAirQuality.setText(String.format(MSG_AIR_QUALITY,
                colorIndex,
                current.getAirQuality().getCo(),
                current.getAirQuality().getNo2(),
                current.getAirQuality().getO3(),
                current.getAirQuality().getSo2())
        );
        Tooltip tooltip = getTooltip(
                String.format(localizeService.getLocalizedResource("airQuality.tooltip"), lbAirQuality.getText()) +
                        "\n" +localizeService.getLocalizedResource("airQuality.tooltip.1")
        );
        ImageView iv = getTooltipImage(new Image(IMAGE_AIR_QUALITY_HINT_PNG), 100);
        tooltip.setGraphic(iv);
        Tooltip.install(airQualityBox, tooltip);
        if (colorIndex >= 1 && colorIndex <= 6) {
            airQualityBox.setStyle(String.format("-fx-background-color: %s",
                    ColorEngineFactory.getEngine(ColorElement.AIR_QUALITY).getColor(colorIndex)));
        }
    }

    private void fillVisibilityAndUvAndDayLen(WeatherCurrent current) {
        lbVisibility.setText(String.format("%.0f km", current.getVisibilityKm()));
        Tooltip tooltipVisibility = getTooltip(String.format(localizeService.getLocalizedResource("visibility.tooltip"), current.getVisibilityKm()));
        ImageView iv = getTooltipImage(imgVisibility.getImage(), 100);
        tooltipVisibility.setGraphic(iv);
        Tooltip.install(visibilityBox, tooltipVisibility);

        lbUvIdx.setText(String.format("%.2f", current.getUvIndex()));
        Tooltip tooltipUv = getTooltip(
                String.format(localizeService.getLocalizedResource("uvIndex.tooltip"),
                        current.getUvIndex()
                )
        );
        iv = getTooltipImage(imgUvIndex.getImage(), 100);
        tooltipUv.setGraphic(iv);
        Tooltip.install(uvIndexBox, tooltipUv);

        visibilityBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.VISIBILITY).getColor((int) current.getVisibilityKm() * 1000)));
        uvIndexBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.UV_INDEX).getColor((int) current.getUvIndex())));
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
        Image extImage = getCurrentExtendedImage(imgWeather.getImage());
        tooltip.setGraphic(getTooltipImage(extImage, (int) extImage.getWidth()));
        lbLocation.setTooltip(tooltip);
    }

    private void fillConditions(WeatherCurrent current) {
        imgWeather.setImage(this.dataHolder.getImage());
        String conditionText = current.getCondition().getText();
        Image extImage = getCurrentExtendedImage(imgWeather.getImage());
        Tooltip tooltip = getTooltip(conditionText);
        tooltip.setGraphic(getTooltipImage(extImage, (int) extImage.getWidth()));
        Tooltip.install(imgWeather, tooltip);
        lbCondition.setText(conditionText);
        lbCondition.setTooltip(getTooltip(conditionText));
    }

    private Image getCurrentExtendedImage(Image currentConditionImage) {
        ChartDataProducerImpl.DataHolder holder = new ChartDataProducerImpl.DataHolder(this.dataHolder.getResponse().getForecast().getDayForecast()[0].getDate());
        holder.setDayForecastData(this.dataHolder.getResponse().getForecast().getDayForecast()[0]);
        return cbwmService.getExtendedWeatherImage(holder, LocalTime.now().getHour(),
                this.dataHolder.getResponse().getCurrent(),
                currentConditionImage);
    }

    private void fillTemperature(WeatherCurrent current) {
        lbTemperature.setText(String.format("%.0f℃", current.getTemperatureC()));
        lbFeels.setText(String.format("%.0f℃", current.getFeelsLike()));
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
        Tooltip.install(tempBox, tooltip);
        Tooltip.install(fillsTempBox, tooltip);

        tempBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.TEMPERATURE).getColor((int) current.getTemperatureC())));
        fillsTempBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.TEMPERATURE).getColor((int) current.getFeelsLike())));
    }

    private void fillHumidity(WeatherCurrent current) {
        lbHumidity.setText(String.format("%d％", current.getHumidity()));
        ImageView iv = getTooltipImage(imgHumidity.getImage(), 100);
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("humidity.tooltip"), current.getHumidity()));
        tooltip.setGraphic(iv);
        Tooltip.install(humidityBox, tooltip);
        humidityBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.HUMIDITY).getColor(current.getHumidity())));
    }

    private void fillPressure(WeatherCurrent current) {
        float pressureMb = commonUtils.getCorrectedPressureValue(current.getPressureMb());
        double pressureMmHg = millibarToMmHg(pressureMb);
        lbPressure.setText(String.format("%.0f mmHg", pressureMmHg));
        int pressureCorrection = Integer.parseInt(configService.getConfigStringValue(GuiConfigService.GUI_PRESSURE_CORRECTION_KEY));
        String correctionText = (pressureCorrection != 0) ? String.format(localizeService.getLocalizedResource("pressure.tooltip.corrected"), pressureCorrection) : "";
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("pressure.tooltip") + correctionText,
                pressureMmHg, pressureMb));
        tooltip.setGraphic(getTooltipImage(imgPressure.getImage(), 100));
        Tooltip.install(pressureBox, tooltip);
        pressureBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.PRESSURE).getColor((int) pressureMmHg)));
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
        Tooltip.install(cloudyBox, tooltip);
        cloudyBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.CLOUDY).getColor(current.getCloud())));
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
        Tooltip.install(windBox, tooltip);
        Tooltip.install(windSpeedBox, tooltip);
        Tooltip.install(windGustBox, tooltip);

        windBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.WIND_DIRECTION).getColor(current.getWindDegree())));
        windSpeedBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.WIND_SPEED).getColor((int) current.getWindKmh())));
        windGustBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.WIND_SPEED).getColor((int) current.getGust())));
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

        int colorValue = 0;
        if (totalPrecipitation > 0 && totalSnow > 0) {
            lbPrecipitation.setText(String.format("%.0f/%.0f", totalPrecipitation, totalSnow));
            colorValue = Math.round(totalSnow);
        } else if (totalPrecipitation > 0 && totalSnow <= 0) {
            lbPrecipitation.setText(String.format("%.0f mm", totalPrecipitation));
            colorValue = Math.round(totalPrecipitation);
        } else if (totalPrecipitation <= 0 && totalSnow > 0) {
            lbPrecipitation.setText(String.format("%.0f cm", totalSnow));
            colorValue = Math.round(totalSnow);
        } else {
            lbPrecipitation.setText("0 mm");
        }

        colorValue = colorValue <= 10 ? colorValue :  colorValue / 10;

        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("precipitation.tooltip"), totalPrecipitation, totalSnow));
        tooltip.setTextAlignment(TextAlignment.RIGHT);
        tooltip.setGraphic(getTooltipImage(image, 100));
        Tooltip.install(precipBox, tooltip);
        precipBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.PRECIPITATIONS).getColor(colorValue)));
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

        LocalDate dt1 = LocalDate.now();
        lbForecast01.setText(DATE_FORMATTER_FOR_VIEW.format(dt1));
        lbForecast01.setTooltip(getTooltip(FULL_DATE_FORMATTER_FOR_VIEW.format(dt1)));
        LocalDate dt2 = LocalDate.parse(dayForecasts[1].getDate(), DATE_FORMATTER_FOR_RESPONSE);
        lbForecast02.setText(DATE_FORMATTER_FOR_VIEW.format(dt2));
        lbForecast02.setTooltip(getTooltip(FULL_DATE_FORMATTER_FOR_VIEW.format(dt2)));
        LocalDate dt3 = LocalDate.parse(dayForecasts[2].getDate(), DATE_FORMATTER_FOR_RESPONSE);
        lbForecast03.setText(DATE_FORMATTER_FOR_VIEW.format(dt3));
        lbForecast03.setTooltip(getTooltip(FULL_DATE_FORMATTER_FOR_VIEW.format(dt3)));

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
        String dayLength = commonUtils.getTimeLength(currentAstro.getSunRise(), currentAstro.getSunSet());
        lbDayLength.setText(dayLength);
        Tooltip tooltip = getTooltip(String.format(localizeService.getLocalizedResource("dayLength.tooltip"), dayLength));
        tooltip.setGraphic(getTooltipImage(imgDayLength.getImage(), 100));
        Tooltip.install(dayLenBox, tooltip);
        dayLenBox.setStyle(String.format("-fx-background-color: %s",
                ColorEngineFactory.getEngine(ColorElement.DAY_LENGTH).getColor(commonUtils.toIntTime(dayLength))));
    }

    private void fillAstro(WeatherAstro currentAstro) {
        lbSunRise.setText(getProperlyFormattedTime(currentAstro.getSunRise()));
        lbSunSet.setText(getProperlyFormattedTime(currentAstro.getSunSet()));
        Tooltip dayLength = getTooltip(String.format(localizeService.getLocalizedResource("dayLength.tooltip"),
                commonUtils.getTimeLength(currentAstro.getSunRise(), currentAstro.getSunSet())));
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
        ConfigController controller = fxWeaver.loadController(ConfigControllerImpl.class);
        controller.setWindowIcon(((Stage) root.getScene().getWindow()).getIcons().get(0));
        controller.show();
        if (configController.isConfigChanged()) {
            onShowingStage();
            updateWeatherDataForce();
        }
    }

    private void showForecastWindow() {
        ForecastController controller = fxWeaver.loadController(ForecastControllerImpl.class);
        controller.setWindowIcon(((Stage) root.getScene().getWindow()).getIcons().get(0));
        controller.setData(this.dataHolder.getResponse());
        controller.show();
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
            try {
                loadDataWithProgress();
            } finally {
                btUpdate.setDisable(false);
            }
        } else {
            log.info("Update Weather Data has been called but weather doesn't updated due to update interval doesn't reached yet.");
            if (log.isDebugEnabled()) log.debug("Update Weather Data has been called but after last data updated spent only {} msec " +
                    "with real update interval {} msec. So it doesn't updated.", msecsAfterUpdateData, updateInterval);
        }
    }

    @Override
    public void setUptime() {
        hbUptime.setVisible(uptimeService.getUptimeMillis() > 60000);
        String uptime = uptimeService.getFormattedUptime();
        lbUptime.setText(uptime);
        Tooltip.install(hbUptime, new Tooltip(String.format("%s %s", lbUptimeLabel.getText(), uptime)));
        if (uptime.length() <= UptimeService.MIN_UPTIME_TEXT_WIDTH) {
            lbUptime.setPrefWidth(UptimeService.MIN_UPTIME_LABEL_WIDTH);
            lbUptime.setMinWidth(Region.USE_PREF_SIZE);
        } else {
            lbUptime.setPrefWidth(UptimeService.MAX_UPTIME_LABEL_WIDTH);
        }
        lbUptime.setMinWidth(Region.USE_PREF_SIZE);
    }

    @Override
    public void updateWeatherDataForce() {
        try {
            loadDataWithProgress();
        } finally {
            btUpdate.setDisable(false);
        }
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
            alertService.showError(task.getException().getCause().getLocalizedMessage()
                            .replaceAll("; ","\n")
                            .replaceAll(": ", ":\n"),
                    "");
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
