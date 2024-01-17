package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public interface ForecastController extends Localized {
    Map<Integer,String> TAB_NAMES = new LinkedHashMap<>();
    Map<String,String> FILE_FORMAT_NAMES = new HashMap<>();

    void onShowing();

    void setData(ForecastWeatherResponse data);
}
