package das.tools.weather.service;

import com.sun.javafx.charts.Legend;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.entity.forecast.WeatherHour;
import das.tools.weather.gui.ForecastController;
import das.tools.weather.gui.GuiControllerImpl;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartDataProducer {
    private final int HOURS;

    private final List<DataHolder> dataList;

    public ChartDataProducer(WeatherDayForecast[] dayForecasts) {
        int daysCount = dayForecasts.length;
        HOURS = dayForecasts[0].getHour().length;
        this.dataList = new ArrayList<>(daysCount);
        initChartsData(dayForecasts);
    }

    private void initChartsData(WeatherDayForecast[] dayForecasts) {
        for (WeatherDayForecast day : dayForecasts) {
            DataHolder dataHolder = new DataHolder(day.getDate());
            int hourIndex = 0;
            Number[] temperatureValues = new Number[HOURS];
            Number[] pressureValues = new Number[HOURS];
            Number[] humidityValues = new Number[HOURS];
            Number[] cloudValues = new Number[HOURS];
            Number[] windValues = new Number[HOURS];
            for (WeatherHour hour : day.getHour()) {
                temperatureValues[hourIndex] = hour.getTemperature();
                pressureValues[hourIndex] = GuiControllerImpl.millibarToMmHg(hour.getPressure());
                humidityValues[hourIndex] = hour.getHumidity();
                cloudValues[hourIndex] = hour.getCloud();
                windValues[hourIndex] = hour.getWindKmh();
                hourIndex++;
            }
            dataHolder.getTabAndValue().put(ForecastController.TAB_NAMES.get(1), temperatureValues);
            dataHolder.getTabAndValue().put(ForecastController.TAB_NAMES.get(2), pressureValues);
            dataHolder.getTabAndValue().put(ForecastController.TAB_NAMES.get(3), humidityValues);
            dataHolder.getTabAndValue().put(ForecastController.TAB_NAMES.get(4), cloudValues);
            dataHolder.getTabAndValue().put(ForecastController.TAB_NAMES.get(5), windValues);
            dataList.add(dataHolder);
        }
    }

    private long getMinutesBetween(String start, String end) {
        LocalTime startTime = LocalTime.parse(start, GuiControllerImpl.TIME_FORMATTER_FOR_RESPONSE);
        LocalTime endTime = LocalTime.parse(end, GuiControllerImpl.TIME_FORMATTER_FOR_RESPONSE);
        return Duration.between(startTime, endTime).getSeconds();
    }

    public void fillChart(XYChart<String, Number> chart, String tabName) {
        chart.getData().clear();
        for (DataHolder dataHolder: this.dataList) {
            XYChart.Series<String,Number> series = new XYChart.Series<>();
            Map<String, Number[]> dayValues = dataHolder.getTabAndValue();
            series.setName(dataHolder.getName());
            Number[] numbers = dayValues.get(tabName);
            for (int i = 0; i < numbers.length; i++) {
                series.getData().add(new XYChart.Data<>(String.valueOf(i), numbers[i]));
            }
            chart.getData().add(series);
        }
    }

    public void makeLegendClickable(XYChart<String,Number> chart) {
        for (Node node : chart.getChildrenUnmodifiable()) {
            if (node instanceof Legend) {
                Legend legend = (Legend) node;
                for (Legend.LegendItem legendItem: legend.getItems()) {
                    for (XYChart.Series<String, Number> series: chart.getData()){
                        if (series.getName().equals(legendItem.getText())) {
                            Node item = legendItem.getSymbol();
                            item.setCursor(Cursor.HAND);
                            Tooltip.install(item, new Tooltip("Click to toggle chart\n" +
                                    "Click with Ctrl to toggle cart's line but points aren't\n" +
                                    "It could be also used to toggle points onto chart"));
                            item.setOnMouseClicked(event -> {
                                if (event.getButton() == MouseButton.PRIMARY) {
                                    series.getNode().setVisible(!series.getNode().isVisible());
                                    if (!event.isControlDown()) {
                                        for (XYChart.Data<String, Number> data : series.getData()) {
                                            if (data.getNode() != null) {
                                                data.getNode().setVisible(series.getNode().isVisible());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
             }
        }
    }

    @Getter
    @Setter
    private static class DataHolder {
        private String name;
        private Map<String,Number[]> tabAndValue;

        public DataHolder(String name) {
            this.name = name;
            this.tabAndValue = new LinkedHashMap<>();
        }
    }
}
