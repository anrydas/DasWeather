package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VisibilityColor extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(0, "#FEC28A");
        COLOR_MAP.put(200, "#FCDE99");
        COLOR_MAP.put(500, "#FFFFBC");
        COLOR_MAP.put(1000, "#DEFFFA");
        COLOR_MAP.put(10000, "#F1FEFC");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.VISIBILITY;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(getIndex(value));
    }
}
