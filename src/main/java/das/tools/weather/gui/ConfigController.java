package das.tools.weather.gui;

import java.util.regex.Pattern;

public interface ConfigController {
    Pattern FORECAST_URL_PATTERN = Pattern.compile("http\\:\\/\\/api\\.weatherapi\\.com\\/v1\\/forecast\\.json");
    Pattern API_KEY_PATTERN = Pattern.compile("^[a-fA-F0-9]+$");
    void onShowingStage();

    boolean isConfigChanged();
}
