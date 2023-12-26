package das.tools.weather.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public interface GuiConfigService {
    public static final String GUI_CONFIG_DEFAULT_FILE_NAME_KEY = "gui.config";
    public static final String GUI_CONFIG_UPDATE_INTERVAL_KEY = "app.update.interval.msec";
    public static final String GUI_CONFIG_CONFIRM_EXIT_KEY = "app.confirm-exit";
    public static final String GUI_CONFIG_API_KEY_KEY = "app.api-key";
    public static final String GUI_CONFIG_WEATHER_LOCATION_KEY = "app.weather.location";
    public static final String GUI_CONFIG_CONDITION_LANGUAGE_KEY = "app.weather.condition.lang";
    public static final String GUI_CONFIG_FORECAST_URL_KEY = "app.weather.forecast.url";
    public static final Map<String,String> GUI_SUPPORTED_CONDITION_LANGUAGES = new HashMap<>();
    Properties getCurrentConfig();

    void saveConfig(Properties props);

    String getConfigStringValue(String key, String defValue);

    String getLangName(String code);

    String getLangCode(String name);
}
