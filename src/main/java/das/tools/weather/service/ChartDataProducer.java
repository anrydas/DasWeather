package das.tools.weather.service;

import das.tools.weather.entity.forecast.WeatherDayForecast;
import javafx.scene.chart.XYChart;

public interface ChartDataProducer {
    void initChartsData(WeatherDayForecast[] dayForecasts);

    void fillChart(XYChart<String, Number> chart, String tabName);

    void makeLegendClickable(XYChart<String, Number> chart);
}
