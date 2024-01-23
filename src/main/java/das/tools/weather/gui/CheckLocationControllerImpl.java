package das.tools.weather.gui;

import das.tools.weather.entity.current.WeatherLocation;
import das.tools.weather.service.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class CheckLocationControllerImpl implements CheckLocationController {
    @Autowired private LocalizeResourcesService localizeService;
    @Autowired private WeatherService weatherService;
    @Autowired private GuiConfigService configService;
    private WeatherLocation[] foundLocations;

    @FXML private Label lbLocationName;
    @FXML private Label lbLocationsList;
    @FXML private TextField edLocationName;
    @FXML private Button btSearch;
    @FXML private ListView<String> lstLocations;
    @FXML private Button btOk;
    @FXML private Button btCancel;

    @FXML
    private void initialize() {
        edLocationName.setOnKeyPressed(keyEvent -> locationNamePressed());
        btSearch.setOnAction(actionEvent -> searchForLocations());
        btOk.setOnAction(actionEvent -> saveLocation());
        btSearch.setDisable(edLocationName.getText().isEmpty());
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
        btSearch.setDisable(edLocationName.getText().isEmpty());
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
    }

    private void searchForLocations() {
        foundLocations = weatherService.getLocations(edLocationName.getText());
        lstLocations.getItems().clear();
        for (WeatherLocation loc : foundLocations) {
            String locName = String.format("%s, %s (%s)", loc.getName(), loc.getRegion(), loc.getCountry());
            lstLocations.getItems().add(locName);
        }
    }
}
