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
        if (value <= keys.get(0)) {
            return keys.get(0);
        }
        if (value >= keys.get(keys.size()-1)) {
            return keys.get(keys.size()-1);
        }
        for (int i = 1; i < keys.size(); i++) {
            if (value > keys.get(i - 1) && value <= keys.get(i)) {
                return keys.get(i);
            }
        }
        return keys.get(keys.size() - 1);
    }
}
