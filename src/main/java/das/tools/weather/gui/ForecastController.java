package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public interface ForecastController extends Localized {
    Map<Integer,String> TAB_NAMES = new LinkedHashMap<>();
    Map<String,String> FILE_FORMAT_NAMES = new HashMap<>();
    double MINUTES_TO_DECIMAL_FACTOR = 0.167;

    void onShowing();

    void setData(ForecastWeatherResponse data);

    String getTimeLength(String start, String stop);
}
