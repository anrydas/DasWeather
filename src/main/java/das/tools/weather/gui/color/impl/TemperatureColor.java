package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TemperatureColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(-10, "#7B82F5");
        COLOR_MAP.put(-5, "#7BC3F5");
        COLOR_MAP.put(0, "#7BEAF5");
        COLOR_MAP.put(5, "#7BF5C7");
        COLOR_MAP.put(17, "#C9F57B");
        COLOR_MAP.put(25, "#A3FFB9");
        COLOR_MAP.put(30, "#F5CC7B");
        COLOR_MAP.put(31, "#F57D7B");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.TEMPERATURE;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
