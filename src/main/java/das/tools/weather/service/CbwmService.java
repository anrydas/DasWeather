package das.tools.weather.service;

import das.tools.weather.entity.current.WeatherCurrent;

import java.awt.image.BufferedImage;

public interface CbwmService {

    BufferedImage getCbwmImage(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex, WeatherCurrent current);
}
