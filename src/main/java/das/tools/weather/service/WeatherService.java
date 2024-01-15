package das.tools.weather.service;

import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;


public interface WeatherService {
    Map<Integer,Integer> WEATHER_CODE_CONDITION_IMAGES = new HashMap<>(48);
    CurrenWeatherResponse getCurrentWeather();

    ForecastWeatherResponse getForecastWeather();

    Image getRemoteImage(String imageUrl);

    Image getWeatherIcon(int weatherCode, boolean isDay);
}
