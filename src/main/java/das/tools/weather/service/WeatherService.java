package das.tools.weather.service;

import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import javafx.scene.image.Image;


public interface WeatherService {
    CurrenWeatherResponse getCurrentWeather();

    ForecastWeatherResponse getForecastWeather();

    Image getRemoteImage(String imageUrl);
}
