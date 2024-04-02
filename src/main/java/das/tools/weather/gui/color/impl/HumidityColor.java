package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class HumidityColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#FFFFBC");
        COLOR_MAP.put(40, "#A3FFB9");
        COLOR_MAP.put(60, "#7BF3F5");
        COLOR_MAP.put(70, "#7BD4F5");
        COLOR_MAP.put(80, "#7B88F5");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.HUMIDITY;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
