package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class GuiConfigServiceImpl implements GuiConfigService {

    private final String configFile;
    private static volatile GuiConfigServiceImpl instance;

    public static GuiConfigServiceImpl getInstance() {
        if (instance == null) {
            synchronized (GuiConfigServiceImpl.class) {
                if (instance == null) {
                    instance = new GuiConfigServiceImpl();
                }
            }
        }
        return instance;
    }

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
    }

    private GuiConfigServiceImpl() {
        Properties properties = readAppProperties();
        this.configFile = properties.getProperty("app.config", GUI_CONFIG_DEFAULT_FILE_NAME_KEY);
    }

    private String getConfigFileName() {
        String profileName = System.getProperty("profiles.active");
        profileName = profileName != null && !"".equals(profileName) ? profileName : "default";
        String fileName = "application-"+profileName+".properties";
        Path userDir = Paths.get(System.getProperty("user.dir") +
                System.getProperty("file.separator") +
                fileName
        );
        if (log.isDebugEnabled()) log.debug("got config file name={}", userDir);
        return userDir.toString();
    }

    @Override
    public String getDefaultConfigValue(String key) {
        return GUI_CONFIG_DEFAULT_VALUES.get(key);
    }

    @Override
    public Properties getCurrentConfig() {
        if (log.isDebugEnabled()) log.debug("configFile={}", configFile);
        Properties properties = readGuiProperties();
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

    private Properties readGuiProperties() {
        String fileName = !"".equals(configFile) ? getConfigCurrentFileName() : getConfigDefaultFileName();
        Properties appProps = new Properties();
        try {
            appProps.load(Files.newInputStream(Paths.get(fileName)));
        } catch (IOException e) {
            log.error("Couldn't read properties from '{}'", fileName);
        }
        return appProps;
    }

    private Properties readAppProperties() {
        Properties appProps = new Properties();
        try {
            appProps.load(Files.newInputStream(Paths.get(getConfigFileName())));
        } catch (IOException e) {
            log.error("Couldn't read properties from '{}'", getConfigFileName());
        }
        return appProps;
    }
}
