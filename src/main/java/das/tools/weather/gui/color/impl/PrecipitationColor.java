package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PrecipitationColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#B0F6FF");
        COLOR_MAP.put(1, "#87F0FE");
        COLOR_MAP.put(3, "#87C6FE");
        COLOR_MAP.put(5, "#63B5FE");
        COLOR_MAP.put(6, "#637AFE");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.PRECIPITATIONS;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
