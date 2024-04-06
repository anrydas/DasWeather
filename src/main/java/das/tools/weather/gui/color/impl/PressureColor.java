package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PressureColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(720, "#A3FFB9");
        COLOR_MAP.put(730, "#FFFFBC");
        COLOR_MAP.put(750, "#F5CC7B");
        COLOR_MAP.put(770, "#F57D7B");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.PRESSURE;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
