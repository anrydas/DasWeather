package das.tools.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.ResourceBundle;

@Service
@Slf4j
public class LocalizeResourcesServiceImpl implements LocalizeResourcesService {
    private final GuiConfigService configService;
    private final ResourceBundle.Control utf8Control;
    private ResourceBundle storedLocale;

    public LocalizeResourcesServiceImpl(GuiConfigService configService, ResourceBundle.Control utf8Control) {
        this.configService = configService;
        this.utf8Control = utf8Control;
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
        this.storedLocale = ResourceBundle.getBundle("languages.lang", locale, utf8Control);
    }

    @Override
    public String getLocalizedResource(String key) {
        return storedLocale.getString(key);
    }
}
