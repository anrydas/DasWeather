package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AirQualityColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(1, "#A3FFB9");
        COLOR_MAP.put(2, "#FFFFBC");
        COLOR_MAP.put(3, "#FAD86F");
        COLOR_MAP.put(4, "#F57D7B");
        COLOR_MAP.put(5, "#CC6FFA");
        COLOR_MAP.put(6, "#9F4C4C");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.AIR_QUALITY;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(value);
    }
}
