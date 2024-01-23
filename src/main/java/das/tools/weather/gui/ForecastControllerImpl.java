package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.service.AlertService;
import das.tools.weather.service.ChartDataProducer;
import das.tools.weather.service.LocalizeResourcesService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
    }
}
