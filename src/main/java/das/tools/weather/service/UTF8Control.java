package das.tools.weather.service;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public interface UTF8Control {
    ResourceBundle newBundle(String baseName, Locale locale,
                             String format, ClassLoader loader,
                             boolean reload) throws IllegalAccessException, InstantiationException, IOException;
}
