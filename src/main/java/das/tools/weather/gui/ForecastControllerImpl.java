package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.service.ChartDataProducer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
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
    @FXML private TabPane tabPane;
    private ForecastWeatherResponse data;
    private File saveFileInitialDirectory = new File(System.getProperty("user.dir"));
    private static final Map<String,XYChart<String,Number>> TABS_TO_CHART_MAP = new LinkedHashMap<>();

    static {
        Map<Integer,String> map = TAB_NAMES;
        map.put(1, "Temperature");
        map.put(2, "Pressure");
        map.put(3, "Humidity");
        map.put(4, "Cloud");
        map.put(5, "Wind");

        Map<String,String> extMap = FILE_FORMAT_NAMES;
        extMap.put("PNG", "PNG");
        extMap.put("JPG", "JPEG");
        extMap.put("JPEG", "JPEG");
        extMap.put("GIF", "GIF");
        extMap.put("BMP", "BMP");
    }

    @FXML
    private void initialize() {
        TABS_TO_CHART_MAP.put(TAB_NAMES.get(1), chTemperature);
        TABS_TO_CHART_MAP.put(TAB_NAMES.get(2), chPressure);
        TABS_TO_CHART_MAP.put(TAB_NAMES.get(3), chHumidity);
        TABS_TO_CHART_MAP.put(TAB_NAMES.get(4), chCloud);
        TABS_TO_CHART_MAP.put(TAB_NAMES.get(5), chWind);

        btClose.setOnAction(actionEvent -> ((Stage) btClose.getScene().getWindow()).close());
        btSave.setOnAction(actionEvent -> saveToFile());
    }

    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chose file name to save the Chart");
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
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            WritableImage image = getActiveCart().snapshot(
                    new SnapshotParameters(),
                    new WritableImage((int) window.getWidth(), (int) window.getHeight())
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        String.format("Chart was saved into '%s' file", file.getName()));
                alert.showAndWait();
            } catch (IOException e) {
                log.error("Couldn't save chart into file", e);
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        String.format("Couldn't save chart into '%s' file:\n%s", file.getName(), e.getLocalizedMessage()));
                alert.showAndWait();
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
        fillGraphics();
    }

    @Override
    public void setData(ForecastWeatherResponse data) {
        this.data = data;
        fillGraphics();
    }

    private XYChart<String, Number> getActiveCart() {
        return TABS_TO_CHART_MAP.get(tabPane.getSelectionModel().getSelectedItem().getText());
    }

    private void fillGraphics() {
        ChartDataProducer chartsData = new ChartDataProducer(this.data.getForecast().getDayForecast());
        chartsData.fillChart(chTemperature, TAB_NAMES.get(1));
        chartsData.fillChart(chPressure, TAB_NAMES.get(2));
        chartsData.fillChart(chHumidity, TAB_NAMES.get(3));
        chartsData.fillChart(chCloud, TAB_NAMES.get(4));
        chartsData.fillChart(chWind, TAB_NAMES.get(5));

        chartsData.makeLegendClickable(chTemperature);
        chartsData.makeLegendClickable(chPressure);
        chartsData.makeLegendClickable(chHumidity);
        chartsData.makeLegendClickable(chCloud);
        chartsData.makeLegendClickable(chWind);
    }
}
