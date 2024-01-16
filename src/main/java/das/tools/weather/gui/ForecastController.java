package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public interface ForecastController {
    Map<Integer,String> TAB_NAMES = new LinkedHashMap<>();
    public static final Map<String,String> FILE_FORMAT_NAMES = new HashMap<>();
    void onShowing();

    void onShow();

    void setData(ForecastWeatherResponse data);
}
