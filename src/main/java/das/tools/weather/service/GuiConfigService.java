package das.tools.weather.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface GuiConfigService {
    String GUI_CONFIG_DEFAULT_FILE_NAME_KEY = "gui.config";
    String GUI_CONFIG_UPDATE_INTERVAL_KEY = "app.update.interval.msec";
    String GUI_PRESSURE_CORRECTION_KEY = "app.pressure.correction.mbar";
    String GUI_CONFIG_CONFIRM_EXIT_KEY = "app.confirm-exit";
    String GUI_CONFIG_API_KEY_KEY = "app.api-key";
    String GUI_CONFIG_WEATHER_LOCATION_KEY = "app.weather.location";
    String GUI_CONFIG_WEATHER_LOCATION_ID_KEY = "app.weather.location.id";
    String GUI_CONFIG_WEATHER_LOCATION_LATITUDE_KEY = "app.weather.location.lat";
    String GUI_CONFIG_WEATHER_LOCATION_LONGITUDE_KEY = "app.weather.location.lon";
    String GUI_CONFIG_WEATHER_LOCATION_URL_KEY = "app.weather.location.url";
    String GUI_CONFIG_WEATHER_LOCATION_COUNTRY_KEY = "app.weather.location.country";
    String GUI_CONFIG_CONDITION_LANGUAGE_KEY = "app.weather.condition.lang";
    String GUI_CONFIG_FORECAST_URL_KEY = "app.weather.forecast.url";
    String GUI_CONFIG_FORECAST_TABS_ORDER_KEY = "app.weather.forecast.tabs.order";
    Map<String,String> GUI_SUPPORTED_CONDITION_LANGUAGES = new HashMap<>();
    Map<String,String> GUI_CONFIG_DEFAULT_VALUES = new HashMap<>();

    String getDefaultConfigValue(String key);

    Properties getCurrentConfig();

    void saveConfig(Properties props);

    String getConfigStringValue(String key, String defValue);

    String getConfigStringValue(String key);

    String getLangName(String code);

    String getLangCode(String name);
}
