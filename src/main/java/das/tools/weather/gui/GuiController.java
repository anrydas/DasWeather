package das.tools.weather.gui;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public interface GuiController {
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
    String IMAGE_NO_PRECIPITATION_PNG = "/images/precip/no_precipitation_1.png";
    String IMAGE_AIR_QUALITY_PNG = "/images/AirQuality.png";
    String IMAGE_AIR_QUALITY_HINT_PNG = "/images/AirQualityHint.png";
    Map<String,String> WIND_DIRECTIONS = new HashMap<>();
    String IMAGE_WEATHER_DEFAULT_ICON_PNG = "/images/weather-default-01.png";

    void updateWeatherData();
    void updateWeatherDataForce();
}
