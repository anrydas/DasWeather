package das.tools.weather.gui.color.impl;

import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngine;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UvIndexColor  extends AbstractColor implements ColorEngine {
    {
        COLOR_MAP = new HashMap<>();
        COLOR_MAP.put(1, "#7BF57B");
        COLOR_MAP.put(2, "#FFFFBC");
        COLOR_MAP.put(3, "#FAD86F");
        COLOR_MAP.put(4, "#F57D7B");
        COLOR_MAP.put(5, "#CC6FFA");
    }
    @Override
    public ColorElement getEngine() {
        return ColorElement.UV_INDEX;
    }

    @Override
    public String getColor(int value) {
        return COLOR_MAP.get(value);
    }
}
