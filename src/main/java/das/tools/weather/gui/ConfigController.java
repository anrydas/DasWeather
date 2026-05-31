package das.tools.weather.gui;

import javafx.scene.image.Image;

import java.util.regex.Pattern;

public interface ConfigController extends Localized, GuiWindow {
    Pattern FORECAST_URL_PATTERN = Pattern.compile("https?\\:\\/\\/[a-z\\/1-9.]+");
    Pattern API_KEY_PATTERN = Pattern.compile("^\\w+$");

    void setWindowIcon(Image icon);

    void onShowingStage();

    void setLocationConfirmation();

    boolean isConfigChanged();
}
