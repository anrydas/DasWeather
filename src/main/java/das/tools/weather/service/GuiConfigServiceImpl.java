package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class GuiConfigServiceImpl implements GuiConfigService {

    @Value("${app.config}")
    private String configFile = "config" + System.getProperty("file.separator") + GUI_CONFIG_DEFAULT_FILE_NAME_KEY;

    static {
        Map<String,String> mapConditions = GUI_SUPPORTED_CONDITION_LANGUAGES;
        mapConditions.put("en", "English");
        mapConditions.put("uk", "Ukrainian");
        mapConditions.put("bg", "Bulgarian");
        mapConditions.put("cs", "Czech");
        mapConditions.put("nl", "Dutch");
        mapConditions.put("fi", "Finnish");
        mapConditions.put("fr", "French");
        mapConditions.put("de", "German");
        mapConditions.put("el", "Greek");
        mapConditions.put("it", "Italian");
        mapConditions.put("ja", "Japanese");
        mapConditions.put("ko", "Korean");
        mapConditions.put("po", "Polish");
        mapConditions.put("pt", "Portuguese");
        mapConditions.put("sp", "Spanish");
        mapConditions.put("sk", "Slovak");
        mapConditions.put("sv", "Swedish");
        mapConditions.put("vi", "Vietnamese");

        Map<String,String> mapDefaults = GUI_CONFIG_DEFAULT_VALUES;
        mapDefaults.put(GUI_CONFIG_DEFAULT_FILE_NAME_KEY, "config/gui.config");
        mapDefaults.put(GUI_CONFIG_UPDATE_INTERVAL_KEY, "3600000");
        mapDefaults.put(GUI_CONFIG_CONFIRM_EXIT_KEY, "true");
        mapDefaults.put(GUI_CONFIG_API_KEY_KEY, "");
        mapDefaults.put(GUI_CONFIG_WEATHER_LOCATION_KEY, "Kyiv");
        mapDefaults.put(GUI_CONFIG_CONDITION_LANGUAGE_KEY, "en");
        mapDefaults.put(GUI_CONFIG_FORECAST_URL_KEY, "http://api.weatherapi.com/v1/forecast.json");
        mapDefaults.put(GUI_CONFIG_FORECAST_TABS_ORDER_KEY, "tab1,tab2,tab3,tab4,tab5,tab6,tab7");
    }

    @Override
    public String getDefaultConfigValue(String key) {
        return GUI_CONFIG_DEFAULT_VALUES.get(key);
    }

    @Override
    public Properties getCurrentConfig() {
        if (log.isDebugEnabled()) log.debug("configFile={}", configFile);
        Properties properties = readAppProperties();
        if (log.isDebugEnabled()) log.debug("got properties={}", properties);
        return properties;
    }

    @Override
    public void saveConfig(Properties props) {
        String fileName = !"".equals(configFile) ? getConfigCurrentFileName() : getConfigDefaultFileName();
        try (FileOutputStream os = new FileOutputStream(fileName)) {
            props.store(os, "Das Weather Properties");
        } catch (IOException e) {
            log.error("Error saving config into file '{}'",fileName, e);
        }
    }

    @Override
    public String getConfigStringValue(String key, String defValue) {
        return getCurrentConfig().getProperty(key, defValue);
    }

    @Override
    public String getLangName(String code) {
        return GUI_SUPPORTED_CONDITION_LANGUAGES.getOrDefault(code, GUI_SUPPORTED_CONDITION_LANGUAGES.get(getDefaultConfigValue(GUI_CONFIG_CONDITION_LANGUAGE_KEY)));
    }

    @Override
    public String getLangCode(String name) {
        for (Map.Entry<String,String> entry : GUI_SUPPORTED_CONDITION_LANGUAGES.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return GUI_SUPPORTED_CONDITION_LANGUAGES.get(getDefaultConfigValue(GUI_CONFIG_CONDITION_LANGUAGE_KEY));
    }

    private String getConfigDefaultFileName() {
        Path file = Paths.get(System.getProperty("user.dir") +
                System.getProperty("file.separator") +
                "config" +
                System.getProperty("file.separator") +
                GUI_CONFIG_DEFAULT_FILE_NAME_KEY
        );
        if (log.isDebugEnabled()) log.debug("got default config file={}", file);
        return file.toString();
    }

    private String getConfigCurrentFileName() {
        Path file = Paths.get(System.getProperty("user.dir") +
                System.getProperty("file.separator") +
                configFile
        );
        if (log.isDebugEnabled()) log.debug("got current config file={}", file);
        return file.toString();
    }

    private Properties readAppProperties() {
        String fileName = !"".equals(configFile) ? getConfigCurrentFileName() : getConfigDefaultFileName();
        Properties appProps = new Properties();
        try {
            appProps.load(Files.newInputStream(Paths.get(fileName)));
        } catch (IOException e) {
            log.error("Couldn't read properties from '{}'", fileName);
        }
        return appProps;
    }
}
