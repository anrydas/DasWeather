package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class WindSpeedColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#A3FFB9");
        COLOR_MAP.put(5, "#FFFFBC");
        COLOR_MAP.put(15, "#F5CC7B");
        COLOR_MAP.put(20, "#F57D7B");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.WIND_SPEED;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
