package das.tools.weather.gui;

import javafx.scene.image.Image;

public interface LocationController extends Localized {
    void setLocation(String location);

    void show();

    void setApiKey(String key);

    void setWindowIcon(Image icon);
}
