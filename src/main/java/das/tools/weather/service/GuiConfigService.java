package das.tools.weather.service;

import java.util.Properties;

public interface GuiConfigService {
    Properties getCurrentConfig();

    String getConfigStringValue(String key, String defValue);
}
