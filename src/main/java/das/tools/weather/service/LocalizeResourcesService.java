package das.tools.weather.service;

import java.util.ResourceBundle;

public interface LocalizeResourcesService {
    ResourceBundle getLocale();

    void initLocale();

    String getLocalizedResource(String key);
}
