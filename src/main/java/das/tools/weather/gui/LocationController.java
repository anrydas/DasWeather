package das.tools.weather.gui;

import javafx.scene.image.Image;

public interface LocationController extends Localized, GuiWindow {
    boolean isLocationChanged();

    void setLocation(String location);

    void setApiKey(String key);

    void setWindowIcon(Image icon);
}
