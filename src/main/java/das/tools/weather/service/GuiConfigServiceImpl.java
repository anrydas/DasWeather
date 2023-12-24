package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Service
@Slf4j
public class GuiConfigServiceImpl implements GuiConfigService {
    public static final String GUI_CONFIG_DEFAULT_FILE_NAME = "gui.config";

    @Value("${app.config}")
    private String configFile = "config" + System.getProperty("file.separator") + GUI_CONFIG_DEFAULT_FILE_NAME;

    @Override
    public Properties getCurrentConfig() {
        if (log.isDebugEnabled()) log.debug("configFile={}", configFile);
        Properties properties = readAppProperties();
        if (log.isDebugEnabled()) log.debug("got properties={}", properties);
        return properties;
    }

    @Override
    public String getConfigStringValue(String key, String defValue) {
        return getCurrentConfig().getProperty(key, defValue);
    }

    private String getConfigDefaultFileName() {
        Path file = Paths.get(System.getProperty("user.dir") +
                System.getProperty("file.separator") +
                "config" +
                System.getProperty("file.separator") +
                GUI_CONFIG_DEFAULT_FILE_NAME
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
