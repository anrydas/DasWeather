package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
public class LocalizeResourcesServiceImpl implements LocalizeResourcesService {
    private final GuiConfigService configService;
    private ResourceBundle storedLocale;
    private static volatile LocalizeResourcesServiceImpl instance;

    public static LocalizeResourcesServiceImpl getInstance() {
        if (instance == null) {
            synchronized (LocalizeResourcesServiceImpl.class) {
                if (instance == null) {
                    instance = new LocalizeResourcesServiceImpl();
                }
            }
        }
        return instance;
    }

    private LocalizeResourcesServiceImpl() {
        configService = GuiConfigServiceImpl.getInstance();
    }

    @Override
    public ResourceBundle getLocale() {
        return storedLocale;
    }

    @Override
    public void initLocale() {
        String lang = configService.getConfigStringValue("app.weather.condition.lang", "en");
        Locale locale = new Locale(lang);
        if (log.isDebugEnabled()) log.debug("Current Language have been set to {}", lang);
        ResourceBundle.Control utf8Control = new UTF8ControlImpl();
        this.storedLocale = ResourceBundle.getBundle("languages.lang", locale, utf8Control);
    }

    @Override
    public String getLocalizedResource(String key) {
        return storedLocale.getString(key);
    }
}
