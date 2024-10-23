package das.tools.weather.service;

import com.sun.javafx.charts.Legend;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.entity.forecast.WeatherHour;
import das.tools.weather.gui.ForecastController;
import das.tools.weather.gui.GuiControllerImpl;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChartDataProducerImpl implements ChartDataProducer {
    private List<DataHolder> dataList;

    private final WeatherService weatherService;
    private final LocalizeResourcesService localizeService;
    private final CbwmService cbwmService;
    private final CommonUtilsService commonUtils;

    public ChartDataProducerImpl(WeatherService weatherService, LocalizeResourcesService localizeService, CbwmService cbwmService, CommonUtilsService commonUtils) {
        this.weatherService = weatherService;
        this.localizeService = localizeService;
        this.cbwmService = cbwmService;
        this.commonUtils = commonUtils;
    }

    @Override
    public void initLocale() {
    }

    @Override
    public void initChartsData(WeatherDayForecast[] dayForecasts) {
        int daysCount = dayForecasts.length;
        final int HOURS = dayForecasts[0].getHour().length;
        this.dataList = new ArrayList<>(daysCount);

        for (WeatherDayForecast day : dayForecasts) {
            DataHolder dataHolder = new DataHolder(day.getDate());
            dataHolder.setDayForecastData(day);
            int hourIndex = 0;
            Number[] temperatureValues = new Number[HOURS];
            Number[] pressureValues = new Number[HOURS];
            Number[] humidityValues = new Number[HOURS];
            Number[] cloudValues = new Number[HOURS];
            Number[] windValues = new Number[HOURS];
            for (WeatherHour hour : day.getHour()) {
                temperatureValues[hourIndex] = hour.getTemperature();
                float pressure = commonUtils.getCorrectedPressureValue(hour.getPressure());
                pressureValues[hourIndex] = GuiControllerImpl.millibarToMmHg(pressure);
                humidityValues[hourIndex] = hour.getHumidity();
                cloudValues[hourIndex] = hour.getCloud();
                windValues[hourIndex] = hour.getWindKmh();
                hourIndex++;
            }
            dataHolder.getTabAndValues().put(ForecastController.TAB_NAMES.get(1), temperatureValues);
            dataHolder.getTabAndValues().put(ForecastController.TAB_NAMES.get(2), pressureValues);
            dataHolder.getTabAndValues().put(ForecastController.TAB_NAMES.get(3), humidityValues);
            dataHolder.getTabAndValues().put(ForecastController.TAB_NAMES.get(4), cloudValues);
            dataHolder.getTabAndValues().put(ForecastController.TAB_NAMES.get(5), windValues);
            dataList.add(dataHolder);
        }
    }

    @Override
    public void fillChart(XYChart<String, Number> chart, String tabName, WeatherCurrent current) {
        chart.getData().clear();
        for (DataHolder dataHolder: this.dataList) {
            XYChart.Series<String,Number> series = new XYChart.Series<>();
            Map<String, Number[]> dayValues = dataHolder.getTabAndValues();
            series.setName(dataHolder.getName());
            chart.getData().add(series);
            Number[] numbers = dayValues.get(tabName);
            for (int i = 0; i < numbers.length; i++) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(i), numbers[i]);
                series.getData().add(data);
                Node dataNode = data.getNode();
                if (dataNode != null) {
                    dataNode.setOnMouseEntered(event -> dataNode.getStyleClass().add("chart-on-hover"));
                    dataNode.setOnMouseExited(event -> dataNode.getStyleClass().remove("chart-on-hover"));
                    installTooltipOnNode(data, dataHolder, current);
                }
            }
        }
    }

    private void installTooltipOnNode(XYChart.Data<String, Number> data, DataHolder dataHolder, WeatherCurrent current) {
        int hourIndex = Integer.parseInt(data.getXValue());
        WeatherHour hour = dataHolder.getDayForecastData().getHour()[hourIndex];
        Image weatherIcon = weatherService.getWeatherIcon(hour.getCondition().getCode(), hour.isDay());
        Image image = cbwmService.getExtendedWeatherImage(dataHolder, hourIndex, current, weatherIcon);
        Tooltip tooltip = new Tooltip(getTooltipText(hour));
        tooltip.setGraphic(GuiControllerImpl.getTooltipImage(image, (int) image.getWidth()));
        Tooltip.install(data.getNode(), tooltip);
    }

    private String getTooltipText(WeatherHour hour) {
        float pressure = commonUtils.getCorrectedPressureValue(hour.getPressure());
        return String.format(
                localizeService.getLocalizedResource("point.tooltip"),
                hour.getTime(),
                hour.getCondition().getText(),
                hour.getTemperature(), hour.getTemperatureF(), hour.getFeelsLike(), hour.getFeelsLikeF(),
                hour.getHumidity(),
                GuiControllerImpl.millibarToMmHg(pressure), pressure,
                hour.getWindDirection(), hour.getWindKmh(), hour.getWindMph(), hour.getGustKmh(), hour.getGustMph()
        );
    }

    @Override
    public void makeLegendClickable(XYChart<String, Number> chart) {
        for (Node node : chart.getChildrenUnmodifiable()) {
            if (node instanceof Legend) {
                Legend legend = (Legend) node;
                for (Legend.LegendItem legendItem: legend.getItems()) {
                    for (XYChart.Series<String, Number> series: chart.getData()){
                        if (series.getName().equals(legendItem.getText())) {
                            Node item = legendItem.getSymbol();
                            item.setCursor(Cursor.HAND);
                            Tooltip.install(item, new Tooltip(localizeService.getLocalizedResource("legend.tooltip")));
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
    public static class DataHolder {
        private String name;
        private Map<String,Number[]> tabAndValues;
        private WeatherDayForecast dayForecastData;

        public DataHolder(String name) {
            this.name = name;
            this.tabAndValues = new LinkedHashMap<>();
        }
    }
}
