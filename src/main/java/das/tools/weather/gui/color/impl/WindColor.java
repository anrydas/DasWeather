package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class WindColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#6E77FF");
        COLOR_MAP.put(30, "#D88FDF");
        COLOR_MAP.put(75, "#FFA8EB");
        COLOR_MAP.put(120, "#FDCD8E");
        COLOR_MAP.put(165, "#FDB88E");
        COLOR_MAP.put(210, "#ECFD8E");
        COLOR_MAP.put(255, "#82FF6C");
        COLOR_MAP.put(300, "#6CF6FF");
        COLOR_MAP.put(345, "#6E77FF");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.WIND_DIRECTION;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
