package das.tools.weather.service;

import das.tools.weather.entity.current.WeatherCurrent;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public interface CbwmService {

    BufferedImage getCbwmImage(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex, WeatherCurrent current);

    javafx.scene.image.Image getExtendedWeatherImage(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex, WeatherCurrent current, Image weatherIcon);
}
