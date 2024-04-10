package das.tools.weather.service;

import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import javafx.scene.chart.XYChart;

public interface ChartDataProducer {
    void initLocale();

    void initChartsData(WeatherDayForecast[] dayForecasts);

    void fillChart(XYChart<String, Number> chart, String tabName, WeatherCurrent current);

    void makeLegendClickable(XYChart<String, Number> chart);
}
