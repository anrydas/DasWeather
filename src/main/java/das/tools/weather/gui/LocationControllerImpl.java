package das.tools.weather.gui;

import das.tools.weather.entity.current.WeatherLocation;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.LocalizeResourcesService;
import das.tools.weather.service.WeatherService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@FxmlView("/fxml/Location.fxml")
@Slf4j
public class LocationControllerImpl implements LocationController {
    private final LocalizeResourcesService localizeService;
    private final WeatherService weatherService;
    private final GuiConfigService configService;
    private final BuildProperties buildProperties;
    private WeatherLocation[] foundLocations;
    private String key;

    @FXML private AnchorPane root;
    @FXML private Label lbLocationName;
    @FXML private Label lbLocationsList;
    @FXML private TextField edLocationName;
    @FXML private Button btSearch;
    @FXML private ListView<String> lstLocations;
    @FXML private Button btOk;
    @FXML private Button btCancel;
    private Stage stage;

    public LocationControllerImpl(LocalizeResourcesService localizeService, WeatherService weatherService, GuiConfigService configService, BuildProperties buildProperties) {
        this.localizeService = localizeService;
        this.weatherService = weatherService;
        this.configService = configService;
        this.buildProperties = buildProperties;
    }

    @FXML
    private void initialize() {
        this.stage = new Stage();
        stage.setScene(new Scene(root));

        edLocationName.setOnKeyReleased(keyEvent -> locationNamePressed());
        btSearch.setOnAction(actionEvent -> searchForLocations());
        btOk.setOnAction(actionEvent -> saveLocation());
        btOk.setDisable(true);
        btCancel.setOnAction(actionEvent -> ((Stage) btCancel.getScene().getWindow()).close());
        lstLocations.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    btOk.setDisable(lstLocations.getSelectionModel().getSelectedIndex() < 0);
                }
            }
        });
    }

    private void saveLocation() {
        WeatherLocation location = foundLocations[lstLocations.getSelectionModel().getSelectedIndex()];
        Properties props = configService.getCurrentConfig();
        props.setProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_ID_KEY, String.valueOf(location.getId()));
        props.setProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY, location.getName());
        props.setProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_LATITUDE_KEY, String.valueOf(location.getLatitude()));
        props.setProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_LONGITUDE_KEY, String.valueOf(location.getLongitude()));
        props.setProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_COUNTRY_KEY, String.valueOf(location.getCountry()));
        configService.saveConfig(props);
        ((Stage) btOk.getScene().getWindow()).close();
    }

    private void locationNamePressed() {
        btSearch.setDisable(!(edLocationName.getText().length() > 0));
    }

    @Override
    public void initLocale() {
        lbLocationName.setText(localizeService.getLocalizedResource("label.location.name"));
        lbLocationsList.setText(localizeService.getLocalizedResource("label.locations.list"));
        btSearch.setText(localizeService.getLocalizedResource("button.search.location"));
        btCancel.setText(localizeService.getLocalizedResource("button.cancel.location"));
    }

    @Override
    public void setLocation(String location) {
        edLocationName.setText(location);
        locationNamePressed();
    }

    @Override
    public void show() {
        stage.setTitle(String.format("Das Weather Location (v.%s)", buildProperties.getVersion()));

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        initLocale();
        stage.showAndWait();
    }

    @Override
    public void setApiKey(String key) {
        this.key = key;
    }

    @Override
    public void setWindowIcon(Image icon) {
        stage.getIcons().clear();
        stage.getIcons().add(icon);
    }

    private void searchForLocations() {
        foundLocations = weatherService.getLocations(edLocationName.getText(), this.key);
        lstLocations.getItems().clear();
        for (WeatherLocation loc : foundLocations) {
            String locName = String.format("%s, %s (%s)", loc.getName(), loc.getRegion(), loc.getCountry());
            lstLocations.getItems().add(locName);
        }
    }
}
