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
        Map<String,String> map = GUI_SUPPORTED_CONDITION_LANGUAGES;
        map.put("en", "English");
        map.put("uk", "Ukrainian");
        map.put("bg", "Bulgarian");
        map.put("cs", "Czech");
        map.put("nl", "Dutch");
        map.put("fi", "Finnish");
        map.put("fr", "French");
        map.put("de", "German");
        map.put("el", "Greek");
        map.put("it", "Italian");
        map.put("ja", "Japanese");
        map.put("ko", "Korean");
        map.put("po", "Polish");
        map.put("pt", "Portuguese");
        map.put("sp", "Spanish");
        map.put("sk", "Slovak");
        map.put("sv", "Swedish");
        map.put("vi", "Vietnamese");
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
        return GUI_SUPPORTED_CONDITION_LANGUAGES.getOrDefault(code, GUI_SUPPORTED_CONDITION_LANGUAGES.get("en"));
    }

    @Override
    public String getLangCode(String name) {
        for (Map.Entry<String,String> entry : GUI_SUPPORTED_CONDITION_LANGUAGES.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return GUI_SUPPORTED_CONDITION_LANGUAGES.get("en");
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
