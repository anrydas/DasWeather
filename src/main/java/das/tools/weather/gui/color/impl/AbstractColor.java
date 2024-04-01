package das.tools.weather.gui.color.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractColor {
    protected Map<Integer, String> COLOR_MAP;

    protected int getIndex(int value) {
        List<Integer> keys = new ArrayList<>(COLOR_MAP.keySet());
        Collections.sort(keys);
        for (int i = 1; i < keys.size(); i++) {
            if (keys.get(i - 1) < value && value <= keys.get(i)) {
                return keys.get(i-1);
            }
        }
        return keys.get(keys.size() - 1);
    }
}
