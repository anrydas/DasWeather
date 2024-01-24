package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.forecast.WeatherAstro;
import das.tools.weather.entity.forecast.WeatherDay;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.service.AlertService;
import das.tools.weather.service.ChartDataProducer;
import das.tools.weather.service.LocalizeResourcesService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

@Component
@Slf4j
public class ForecastControllerImpl implements ForecastController {
    @FXML private Button btClose;
    @FXML private Button btSave;
    @FXML private LineChart<String, Number> chTemperature;
    @FXML private LineChart<String, Number> chPressure;
    @FXML private LineChart<String, Number> chHumidity;
    @FXML private LineChart<String, Number> chCloud;
    @FXML private LineChart<String, Number> chWind;
    @FXML private AreaChart<String, Number> chSun;
    @FXML private AreaChart<String, Number> chPrecipitation;
    @FXML private TabPane tabPane;
    private ForecastWeatherResponse data;
    private File saveFileInitialDirectory = new File(System.getProperty("user.dir"));

    @Autowired private ChartDataProducer chartDataProducer;
    @Autowired private LocalizeResourcesService localizeService;
    @Autowired private AlertService alertService;

    static {
        Map<String,String> extMap = FILE_FORMAT_NAMES;
        extMap.put("PNG", "PNG");
        extMap.put("JPG", "JPEG");
        extMap.put("JPEG", "JPEG");
        extMap.put("GIF", "GIF");
        extMap.put("BMP", "BMP");
    }

    @Override
    public void initLocale() {
        Map<Integer,String> map = TAB_NAMES;
        map.put(1, localizeService.getLocalizedResource("chart.tab.1"));
        map.put(2, localizeService.getLocalizedResource("chart.tab.2"));
        map.put(3, localizeService.getLocalizedResource("chart.tab.3"));
        map.put(4, localizeService.getLocalizedResource("chart.tab.4"));
        map.put(5, localizeService.getLocalizedResource("chart.tab.5"));
        map.put(6, localizeService.getLocalizedResource("chart.tab.6"));
        map.put(7, localizeService.getLocalizedResource("chart.tab.7"));
    }

    @FXML
    private void initialize() {
        btClose.setOnAction(actionEvent -> ((Stage) btClose.getScene().getWindow()).close());
        btSave.setOnAction(actionEvent -> saveChartToFile());
    }

    private File selectFileToSaveChart() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(localizeService.getLocalizedResource("file.dialog.title"));
        fileChooser.setInitialDirectory(this.saveFileInitialDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF Files", "*.gif"),
                new FileChooser.ExtensionFilter("BitMap Files", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setInitialFileName("forecast.png");
        Window window = btClose.getScene().getWindow();
        return fileChooser.showSaveDialog(window);
    }

    private void saveChartToFile() {
        File file = selectFileToSaveChart();
        if (file != null) {
            XYChart<String, Number> activeCart = getActiveChart();
            WritableImage image = activeCart.snapshot(
                    new SnapshotParameters(),
                    new WritableImage((int) activeCart.getWidth(), (int) activeCart.getHeight())
            );
            try {
                String fileFormatName = getFileFormatName(file);
                BufferedImage img = SwingFXUtils.fromFXImage(image, null);
                if ("JPEG".equals(fileFormatName) || "JPG".equals(fileFormatName)) {
                    // W/A for JPEG from https://bugs.openjdk.org/browse/JDK-8119048
                    BufferedImage bImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    bImage.getGraphics().drawImage(img, 0, 0, null);
                    ImageIO.write(bImage, fileFormatName, file);
                } else {
                    ImageIO.write(img, fileFormatName, file);
                }
                this.saveFileInitialDirectory = file.getParentFile();
                alertService.showInfo(
                        String.format(localizeService.getLocalizedResource("alert.saveFile.ok"), file.getName()),
                        "");
            } catch (IOException e) {
                log.error("Couldn't save chart into file", e);
                alertService.showError(
                        String.format(localizeService.getLocalizedResource("alert.saveFile.error"),
                                file.getName(),
                                e.getLocalizedMessage()),
                        e.getLocalizedMessage());
            }
        }
    }

    private String getFileFormatName(File file) {
        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase();
        if (log.isDebugEnabled()) log.debug("got ext={}", ext);
        String res = FILE_FORMAT_NAMES.get(ext);
        if (log.isDebugEnabled()) log.debug("got resulted file extension={}", res);
        return !"".equals(res) ? ext : "PNG";
    }

    @Override
    public void onShowing() {
        setTabNames();
    }

    private void setTabNames() {
        int i = 1;
        for (Tab tab : tabPane.getTabs()) {
            tab.setText(TAB_NAMES.get(i));
            i++;
        }
    }

    @Override
    public void setData(ForecastWeatherResponse data) {
        this.data = data;
        fillGraphics();
    }

    private XYChart<String, Number> getActiveChart() {
        XYChart<String, Number> res = null;
        for (Node node: ((Parent) tabPane.getSelectionModel().getSelectedItem().getContent()).getChildrenUnmodifiable()) {
            if (node instanceof XYChart) {
                res = (XYChart<String, Number>) node;
                break;
            }
        };
        return res;
    }

    private void fillGraphics() {
        chartDataProducer.initChartsData(this.data.getForecast().getDayForecast());
        chartDataProducer.fillChart(chTemperature, TAB_NAMES.get(1));
        chartDataProducer.fillChart(chPressure, TAB_NAMES.get(2));
        chartDataProducer.fillChart(chHumidity, TAB_NAMES.get(3));
        chartDataProducer.fillChart(chCloud, TAB_NAMES.get(4));
        chartDataProducer.fillChart(chWind, TAB_NAMES.get(5));

        chartDataProducer.makeLegendClickable(chTemperature);
        chartDataProducer.makeLegendClickable(chPressure);
        chartDataProducer.makeLegendClickable(chHumidity);
        chartDataProducer.makeLegendClickable(chCloud);
        chartDataProducer.makeLegendClickable(chWind);

        fillSunTab();
        fillPrecipitationTab();
    }

    private void fillSunTab() {
        chSun.getData().clear();
        XYChart.Series<String,Number> sunRiseSeries = new XYChart.Series<>();
        XYChart.Series<String,Number> dayLengthSeries = new XYChart.Series<>();
        XYChart.Series<String,Number> sunSetSeries = new XYChart.Series<>();

        sunRiseSeries.setName(localizeService.getLocalizedResource("sun.rise"));
        dayLengthSeries.setName(localizeService.getLocalizedResource("sun.set"));
        sunSetSeries.setName(localizeService.getLocalizedResource("sun.day.length"));

        chSun.getData().add(sunRiseSeries);
        chSun.getData().add(dayLengthSeries);
        chSun.getData().add(sunSetSeries);

        for (WeatherDayForecast dayForecast : data.getForecast().getDayForecast()) {
            XYChart.Data<String, Number> sunRiseData = new XYChart.Data<>(dayForecast.getDate(), getParsedTime(dayForecast.getAstro().getSunRise()));
            sunRiseSeries.getData().add(sunRiseData);
            installTooltipOnSunNode(sunRiseData.getNode(), dayForecast.getAstro());

            XYChart.Data<String, Number> dayLengthData = new XYChart.Data<>(dayForecast.getDate(),
                    getDayLength(dayForecast.getAstro().getSunRise(), dayForecast.getAstro().getSunSet()));
            dayLengthSeries.getData().add(dayLengthData);
            installTooltipOnSunNode(dayLengthData.getNode(), dayForecast.getAstro());

            XYChart.Data<String, Number> sunSetData = new XYChart.Data<>(dayForecast.getDate(), getParsedTime(dayForecast.getAstro().getSunSet()));
            sunSetSeries.getData().add(sunSetData);
            installTooltipOnSunNode(sunSetData.getNode(), dayForecast.getAstro());
        }
        chartDataProducer.makeLegendClickable(chSun);
    }

    private void fillPrecipitationTab() {
        chPrecipitation.getData().clear();
        XYChart.Series<String,Number> rainSeries = new XYChart.Series<>();
        XYChart.Series<String,Number> snowSeries = new XYChart.Series<>();

        rainSeries.setName(localizeService.getLocalizedResource("rain.legend"));
        snowSeries.setName(localizeService.getLocalizedResource("snow.legend"));

        chPrecipitation.getData().add(rainSeries);
        chPrecipitation.getData().add(snowSeries);

        for (WeatherDayForecast dayForecast : data.getForecast().getDayForecast()) {
            XYChart.Data<String, Number> rainData = new XYChart.Data<>(dayForecast.getDate(), dayForecast.getDay().getTotalPrecipitation());
            rainSeries.getData().add(rainData);
            installTooltipOnPrecipitationNode(rainData.getNode(), dayForecast.getDay());

            XYChart.Data<String, Number> snowData = new XYChart.Data<>(dayForecast.getDate(), dayForecast.getDay().getTotalSnow() * 10);
            snowSeries.getData().add(snowData);
            installTooltipOnPrecipitationNode(snowData.getNode(), dayForecast.getDay());
        }
        chartDataProducer.makeLegendClickable(chPrecipitation);

    }

    private void installTooltipOnSunNode(Node node, WeatherAstro astro) {
        if (node != null) {
            node.setOnMouseEntered(event -> node.getStyleClass().add("chart-on-hover"));
            node.setOnMouseExited(event -> node.getStyleClass().remove("chart-on-hover"));
            String msg = String.format(localizeService.getLocalizedResource("sun.point.tooltip"),
                    astro.getSunRise(),
                    astro.getSunSet(),
                    getTimeLength(astro.getSunRise(), astro.getSunSet()));
            Tooltip tooltip = new Tooltip(msg);
            Tooltip.install(node, tooltip);
        }
    }

    private void installTooltipOnPrecipitationNode(Node node, WeatherDay day) {
        if (node != null) {
            node.setOnMouseEntered(event -> node.getStyleClass().add("chart-on-hover"));
            node.setOnMouseExited(event -> node.getStyleClass().remove("chart-on-hover"));
            String msg = String.format(localizeService.getLocalizedResource("precipitation.point.tooltip"),
                    day.getTotalPrecipitation(),
                    day.getChanceOfRain(),
                    day.getTotalSnow(),
                    day.getChanceOfSnow());
            Tooltip tooltip = new Tooltip(msg);
            Tooltip.install(node, tooltip);
        }
    }

    private double getParsedTime(String value) {
        LocalTime time = LocalTime.parse(value, GuiController.TIME_FORMATTER_FOR_RESPONSE);
        if (log.isDebugEnabled()) log.debug("got parsed time={}", time);
        double res = time.getHour() + (time.getMinute() * MINUTES_TO_DECIMAL_FACTOR);
        if (log.isDebugEnabled()) log.debug("got parsed time res={}", res);
        return res;
    }

    private double getDayLength(String start, String stop) {
        long diff = getDiffSeconds(start, stop);
        long hours = diff / (60 * 60) % 24;
        long minutes = diff / (60) % 60;
        double res = hours + (minutes * MINUTES_TO_DECIMAL_FACTOR);
        if (log.isDebugEnabled()) log.debug("got parsed length={}", res);
        return res;
    }

    @Override
    public String getTimeLength(String start, String stop) {
        long diff = getDiffSeconds(start, stop);
        long hours = diff / (60 * 60) % 24;
        long minutes = diff / (60) % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    private long getDiffSeconds(String start, String stop) {
        LocalTime startTime = LocalTime.parse(start, GuiController.TIME_FORMATTER_FOR_RESPONSE);
        LocalTime stopTime = LocalTime.parse(stop, GuiController.TIME_FORMATTER_FOR_RESPONSE);
        return Duration.between(startTime, stopTime).getSeconds();
    }
}
