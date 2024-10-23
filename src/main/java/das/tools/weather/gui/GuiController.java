package das.tools.weather.gui;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public interface GuiController extends Localized, GuiWindow {
    String APPLICATION_TITLE = "Das Weather: %s %s [%s]";
    int MINIMAL_UPDATE_INTERVAL = 1800000;
    DateTimeFormatter DATE_FORMATTER_FOR_RESPONSE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter DATE_FORMATTER_FOR_VIEW = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    DateTimeFormatter FULL_DATE_FORMATTER_FOR_VIEW = DateTimeFormatter.ofPattern("E, dd-MMM-yyyy");
    DateTimeFormatter TIME_FORMATTER_FOR_VIEW = DateTimeFormatter.ofPattern("HH:mm");
    GuiControllerImpl.RemoteDataHolder dataHolder = GuiControllerImpl.RemoteDataHolder.builder().build();
    DateTimeFormatter TIME_FORMATTER_FOR_RESPONSE = DateTimeFormatter.ofPattern("hh:mm a");
    String IMAGE_CONFIGURE_PNG = "/images/configure.png";
    String IMAGE_WIND_ARROW_PNG = "/images/wind_arrow.png";
    String IMAGE_SUNRISE_PNG = "/images/sunrise.png";
    String IMAGE_SUNSET_PNG = "/images/sunset.png";
    String IMAGE_MOONRISE_PNG = "/images/moonrise.png";
    String IMAGE_MOONSET_PNG = "/images/moonset.png";
    String IMAGE_TEMP_HOT_PNG = "/images/temp_hot.png";
    String IMAGE_TEMP_COLD_PNG = "/images/temp_cold.png";
    String IMAGE_COMPASS_ARROW_PNG = "/images/compass_arrow.png";
    String IMAGE_SNOW_AND_RAIN_PNG = "/images/precip/snow_and_rain.png";
    String IMAGE_SNOW_PNG = "/images/precip/snow.png";
    String IMAGE_RAIN_PNG = "/images/precip/rain.png";
    String IMAGE_NO_PRECIPITATION_PNG = "/images/precip/no_precipitation.png";
    String IMAGE_AIR_QUALITY_PNG = "/images/AirQuality.png";
    String IMAGE_LOCATION_CONFIRMED_PNG = "/images/gui/confirmOk.png";
    String IMAGE_LOCATION_UN_CONFIRMED_PNG = "/images/gui/confirmUn.png";
    String IMAGE_AIR_QUALITY_HINT_PNG = "/images/AirQualityHint.png";
    Map<String,String> WIND_DIRECTIONS = new HashMap<>();
    Map<String,String> MOON_PHASES = new HashMap<>();
    String IMAGE_WEATHER_DEFAULT_ICON_PNG = "/images/weather-default-01.png";

    void onShowingStage();
    void updateWeatherData();

    void setUptime();

    void updateWeatherDataForce();
}
