package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class GuiConfigServiceImpl implements GuiConfigService {

    @Value("${app.config}")
    private String configFile;

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
        mapDefaults.put(GUI_PRESSURE_CORRECTION_KEY, "0");
        mapDefaults.put(GUI_CONFIG_CONFIRM_EXIT_KEY, "true");
        mapDefaults.put(GUI_CONFIG_API_KEY_KEY, "");
        mapDefaults.put(GUI_CONFIG_WEATHER_LOCATION_KEY, "Kyiv");
        mapDefaults.put(GUI_CONFIG_CONDITION_LANGUAGE_KEY, "en");
        mapDefaults.put(GUI_CONFIG_FORECAST_URL_KEY, "http://api.weatherapi.com/v1/forecast.json");
        mapDefaults.put(GUI_CONFIG_FORECAST_TABS_ORDER_KEY, "tab1,tab2,tab3,tab4,tab5,tab6,tab7");
    }

    private Properties cachedProperties;
    private Path cachedFile;
    private long cachedLastModified;

    @Override
    public String getDefaultConfigValue(String key) {
        return GUI_CONFIG_DEFAULT_VALUES.get(key);
    }

    @Override
    public Properties getCurrentConfig() {
        if (log.isDebugEnabled()) log.debug("configFile={}", configFile);
        Properties properties = readAppProperties();
        if (log.isDebugEnabled()) log.debug("got properties={}", maskApiKey(properties));
        return properties;
    }

    @Override
    public synchronized void saveConfig(Properties props) {
        Path file = getConfigFile();
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (OutputStream os = Files.newOutputStream(file)) {
                props.store(os, "Das Weather Properties");
            }
            cacheProperties(file, props);
        } catch (IOException e) {
            log.error("Error saving config into file '{}'", file, e);
            cachedProperties = null;
        }
    }

    @Override
    public String getConfigStringValue(String key, String defValue) {
        return getCurrentConfig().getProperty(key, defValue);
    }

    @Override
    public String getConfigStringValue(String key) {
        String defValue = getDefaultConfigValue(key);
        return getCurrentConfig().getProperty(key, defValue);
    }

    @Override
    public long getConfigLongValue(String key) {
        String value = getConfigStringValue(key);
        try {
            return Long.parseLong(value.trim());
        } catch (NullPointerException | NumberFormatException e) {
            String defValue = getDefaultConfigValue(key);
            log.warn("Wrong config value '{}' for key '{}', default value '{}' will be used", value, key, defValue);
            try {
                return Long.parseLong(defValue);
            } catch (NumberFormatException ex) {
                return 0;
            }
        }
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
        return getDefaultConfigValue(GUI_CONFIG_CONDITION_LANGUAGE_KEY);
    }

    private Path getConfigFile() {
        String fileName = (configFile != null && !"".equals(configFile.trim())) ?
                configFile.trim() :
                "config" + File.separator + GUI_CONFIG_DEFAULT_FILE_NAME_KEY;
        // relative path is resolved against working directory, absolute one is used as is
        Path file = Paths.get(System.getProperty("user.dir")).resolve(fileName);
        if (log.isDebugEnabled()) log.debug("got current config file={}", file);
        return file;
    }

    // Config file is cached and re-read only when it has been changed
    private synchronized Properties readAppProperties() {
        Path file = getConfigFile();
        long lastModified = getLastModified(file);
        if (cachedProperties == null || !file.equals(cachedFile) || lastModified != cachedLastModified) {
            Properties appProps = new Properties();
            try (InputStream is = Files.newInputStream(file)) {
                appProps.load(is);
            } catch (IOException e) {
                log.error("Couldn't read properties from '{}'", file);
            }
            cachedProperties = appProps;
            cachedFile = file;
            cachedLastModified = lastModified;
        }
        // copy is returned to prevent cached properties from being modified by callers
        return (Properties) cachedProperties.clone();
    }

    private void cacheProperties(Path file, Properties props) {
        cachedProperties = (Properties) props.clone();
        cachedFile = file;
        cachedLastModified = getLastModified(file);
    }

    private static long getLastModified(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException e) {
            return -1;
        }
    }

    private static Properties maskApiKey(Properties props) {
        Properties res = (Properties) props.clone();
        if (res.containsKey(GUI_CONFIG_API_KEY_KEY)) {
            res.setProperty(GUI_CONFIG_API_KEY_KEY, "***");
        }
        return res;
    }
}
