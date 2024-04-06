package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DayLengthColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#7B82F5");
        COLOR_MAP.put(9*60, "#7BC3F5");
        COLOR_MAP.put(11*60, "#FFFFBC");
        COLOR_MAP.put(13*60, "#FFFFE9");
        COLOR_MAP.put(15*60, "#FFFFF7");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.DAY_LENGTH;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
