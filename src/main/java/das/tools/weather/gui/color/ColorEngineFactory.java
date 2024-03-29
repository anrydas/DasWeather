package das.tools.weather.gui.color;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ColorEngineFactory {
    private final List<ColorEngine> colorEngines;
    private static final Map<ColorElement, ColorEngine> cache = new HashMap<>();

    public ColorEngineFactory(List<ColorEngine> colorEngines) {
        this.colorEngines = colorEngines;
    }

    @PostConstruct
    public void initServiceCache() {
        for (ColorEngine service : colorEngines) {
            cache.put(service.getEngine(), service);
        }
    }

    public static ColorEngine getEngine(ColorElement type) {
        ColorEngine engine =cache.get(type);
        if (engine == null) {
            throw new RuntimeException("Unknown color engine: " + type);
        }
        return engine;
    }
}
