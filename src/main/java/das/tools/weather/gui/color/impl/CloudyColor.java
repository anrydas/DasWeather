package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CloudyColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#DEFFFA");
        COLOR_MAP.put(10, "#B0F6FF");
        COLOR_MAP.put(40, "#87F0FE");
        COLOR_MAP.put(60, "#87C6FE");
        COLOR_MAP.put(80, "#63B5FE");
        COLOR_MAP.put(100, "#637AFE");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.CLOUDY;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
