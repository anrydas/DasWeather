package das.tools.weather.gui;

import das.tools.weather.entity.current.WeatherLocation;
import das.tools.weather.service.AlertService;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.LocalizeResourcesService;
import das.tools.weather.service.WeatherService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;

@Component
@FxmlView("/fxml/Config.fxml")
@Slf4j
public class ConfigControllerImpl implements ConfigController {
    public static final String SPINNER_TOOLTIP = "%s %d %s";
    private final GuiConfigService configService;
    private final LocalizeResourcesService localizeService;
    private final BuildProperties buildProperties;
    private final AlertService alertService;
    private final FxWeaver fxWeaver;
    private final WeatherService weatherService;
    private Properties appProps;
    private boolean isConfigChanged = false;
    private boolean isLocationChanged = false;
    private String oldLocationName;

    private Stage stage;
    @FXML private AnchorPane root;
    @FXML private Label lbApiKey;
    @FXML private Label lbUrl;
    @FXML private Label lbLocation;
    @FXML private Label lbInterval;
    @FXML private Label lbLanguage;
    @FXML private TextField edApiKey;
    @FXML private TextField edForecastUrl;
    @FXML private TextField edLocation;
    @FXML private Spinner<Integer> spUpdateInterval;
    @FXML private ComboBox<String> cbCondLang;
    @FXML private CheckBox chbConfirmExit;
    @FXML private Button btOk;
    @FXML private Button btCancel;
    @FXML private Button btSearchLocation;
    @FXML private ImageView imgConfirmed;
    @FXML private Label lbPressureCorrection;
    @FXML private Spinner<Integer> spPressureCorrection;
    @FXML private HBox hbInterval;
    @FXML private HBox hbCorrection;
    public ConfigControllerImpl(GuiConfigService configService, LocalizeResourcesService localizeService, BuildProperties buildProperties, AlertService alertService, FxWeaver fxWeaver, WeatherService weatherService) {
        this.configService = configService;
        this.localizeService = localizeService;
        this.buildProperties = buildProperties;
        this.alertService = alertService;
        this.fxWeaver = fxWeaver;
        this.weatherService = weatherService;
    }

    @Override
    public void initLocale() {
        setLabelNames();
        btSearchLocation.setText(localizeService.getLocalizedResource("button.search"));
        btSearchLocation.setTooltip(new Tooltip(localizeService.getLocalizedResource("button.search.tooltip")));
        Tooltip.install(hbInterval, new Tooltip(String.format(SPINNER_TOOLTIP, lbInterval.getText(), spUpdateInterval.getValue(), "min.")));
        Tooltip.install(hbCorrection, new Tooltip(String.format(SPINNER_TOOLTIP, lbPressureCorrection.getText(), spPressureCorrection.getValue(), "mBar")));
    }

    @FXML
    private void initialize() {
        isConfigChanged = false;
        isLocationChanged = false;
        this.stage = new Stage();
        this.stage.setScene(new Scene(root));
        btOk.setOnAction(actionEvent -> saveConfigAndClose());
        btCancel.setOnAction(actionEvent -> {
            isConfigChanged = false;
            isLocationChanged = false;
            closeStage();
        });
        btSearchLocation.setOnAction(actionEvent -> showLocationWindow());
        edApiKey.setOnKeyReleased(event -> apiKeyPressed());
    }

    @Override
    public void show() {
        stage.setTitle(String.format("Das Weather Config (v.%s)", buildProperties.getVersion()));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnShowing(windowEvent -> onShowingStage());
        initLocale();
        stage.showAndWait();
    }

    @Override
    public void setWindowIcon(Image icon) {
        stage.getIcons().clear();
        stage.getIcons().add(icon);
    }

    private void apiKeyPressed() {
        btSearchLocation.setDisable(!(edApiKey.getText().length() > 0));
    }

    @Override
    public void onShowingStage() {
        appProps = configService.getCurrentConfig();
        edApiKey.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY)));
        edForecastUrl.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY)));
        edLocation.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY)));
        oldLocationName = edLocation.getText();
        chbConfirmExit.setSelected(Boolean.parseBoolean(appProps.getProperty(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY))));
        spUpdateInterval.getValueFactory().setValue(
                mSecToMin(
                        Integer.parseInt(
                                appProps.getProperty(GuiConfigService.GUI_CONFIG_UPDATE_INTERVAL_KEY,
                                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_UPDATE_INTERVAL_KEY)
                                )
                        )
                )
        );
        spPressureCorrection.getValueFactory().setValue(
                Integer.parseInt(
                        appProps.getProperty(GuiConfigService.GUI_PRESSURE_CORRECTION_KEY,
                                configService.getDefaultConfigValue(GuiConfigService.GUI_PRESSURE_CORRECTION_KEY)
                )
        ));
        cbCondLang.setItems(getLanguagesList());
        String langName = configService.getLangName(
                appProps.getProperty(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY)
                )
        );
        cbCondLang.getSelectionModel().select(langName);
        edLocation.setOnKeyReleased(event -> setLocationConfirmation());
        apiKeyPressed();
        setLocationConfirmation();
    }

    @Override
    public void setLocationConfirmation() {
        boolean locationConfirmed = isLocationConfirmed();
        String msg = locationConfirmed ?
                localizeService.getLocalizedResource("location.confirm.tooltip") :
                localizeService.getLocalizedResource("location.unConfirm.tooltip");
        String img = locationConfirmed ?
                GuiController.IMAGE_LOCATION_CONFIRMED_PNG :
                GuiController.IMAGE_LOCATION_UN_CONFIRMED_PNG;
        imgConfirmed.setImage(new Image(img));
        Tooltip tooltip = new Tooltip(msg);
        Tooltip.install(imgConfirmed, tooltip);
    }

    private boolean isLocationConfirmed() {
        appProps = configService.getCurrentConfig();
        String storedLocationId = appProps.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_ID_KEY, "");
        String locationId = getCurrentLocationId();
        return storedLocationId != null && !"".equals(storedLocationId)
                && locationId.equals(storedLocationId);
    }

    private String getCurrentLocationId() {
        String res = "";
        try {
            WeatherLocation[] locations = weatherService.getLocations(edLocation.getText(), appProps.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY));
            assert locations != null;
            if (locations.length > 0) {
                res = String.valueOf(locations[0].getId());
            }
        } catch (Exception e) {
            log.error("Error getting current location: ", e);
        }
        return res;
    }

    private void showLocationWindow() {
        LocationController controller = fxWeaver.loadController(LocationControllerImpl.class);
        controller.setWindowIcon(((Stage) root.getScene().getWindow()).getIcons().get(0));
        controller.setLocation(edLocation.getText());
        controller.setApiKey(edApiKey.getText());
        controller.show();
        isLocationChanged = controller.isLocationChanged();
        if (isLocationChanged) {
            oldLocationName = edLocation.getText();
        }
        appProps = configService.getCurrentConfig();
        edLocation.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY)));
        setLocationConfirmation();
    }

    private void setLabelNames() {
        lbApiKey.setText(localizeService.getLocalizedResource("label.key"));
        lbUrl.setText(localizeService.getLocalizedResource("label.url"));
        lbLocation.setText(localizeService.getLocalizedResource("label.location"));
        lbInterval.setText(localizeService.getLocalizedResource("label.interval"));
        lbLanguage.setText(localizeService.getLocalizedResource("label.language"));
        chbConfirmExit.setText(localizeService.getLocalizedResource("box.confirm.exit"));
        btCancel.setText(localizeService.getLocalizedResource( "button.close"));
        lbPressureCorrection.setText(localizeService.getLocalizedResource("label.pressure.correction"));
    }

    @Override
    public boolean isConfigChanged() {
        return isConfigChanged;
    }

    private ObservableList<String> getLanguagesList() {
        String[] langs = GuiConfigService.GUI_SUPPORTED_CONDITION_LANGUAGES.values().toArray(new String[0]);
        Arrays.sort(langs);
        return FXCollections.observableArrayList(langs);
    }

    private void saveConfigAndClose() {
        if (isFieldsValid()) {
            saveConfig();
            closeStage();
        }
    }

    private void closeStage() {
        ((Stage) btCancel.getScene().getWindow()).close();
    }

    private void saveConfig() {
        Properties oldProps = (Properties) appProps.clone();
        updateConfigFromForm();
        if (!oldProps.equals(appProps)) {
            isConfigChanged = true;
            configService.saveConfig(appProps);
            if (log.isDebugEnabled()) log.debug("Stored updated application's properties: {}", appProps);
        } else {
            isConfigChanged = isLocationChanged;
        }
    }

    private void updateConfigFromForm() {
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_API_KEY_KEY, edApiKey.getText());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY, edForecastUrl.getText());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY, edLocation.getText());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY, String.valueOf(chbConfirmExit.isSelected()));
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_UPDATE_INTERVAL_KEY, String.valueOf(minToMSec(spUpdateInterval.getValue())));
        String langCode = configService.getLangCode(cbCondLang.getValue());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY, langCode);
        setPropertyIfChanged(GuiConfigService.GUI_PRESSURE_CORRECTION_KEY, String.valueOf(spPressureCorrection.getValue()));
    }

    private void setPropertyIfChanged(String key, String value) {
        String oldProperty = appProps.getProperty(key, configService.getDefaultConfigValue(key));
        if (!oldProperty.equals(value)) {
            appProps.setProperty(key, value);
        }
    }

    private Integer minToMSec(int min) {
        return min * 60 * 1000;
    }

    private Integer mSecToMin(int msec) {
        return Math.round((float) msec / (60 * 1000));
    }

    private boolean isFieldsValid() {
        String msgEmpty = localizeService.getLocalizedResource("alert.app.config.empty.message");
        String msgNotValid = localizeService.getLocalizedResource("alert.app.config.valid.message");
        if ("".equals(edApiKey.getText())) {
            showError(String.format(msgEmpty, localizeService.getLocalizedResource("alert.app.config.field.key")));
            return false;
        }
        if ("".equals(edLocation.getText())) {
            showError(String.format(msgEmpty, localizeService.getLocalizedResource("alert.app.config.field.location")));
            return false;
        }
        if ("".equals(edForecastUrl.getText())) {
            showError(String.format(msgEmpty, localizeService.getLocalizedResource("alert.app.config.field.url")));
            return false;
        }
        Matcher matchKey = API_KEY_PATTERN.matcher(edApiKey.getText());
        if (!matchKey.find()) {
            showError(String.format(msgNotValid, localizeService.getLocalizedResource("alert.app.config.field.key")));
            return false;
        }
        matchKey = FORECAST_URL_PATTERN.matcher(edForecastUrl.getText());
        if (!matchKey.find()) {
            showError(String.format(msgNotValid, localizeService.getLocalizedResource("alert.app.config.field.location")));
            return false;
        }
        return true;
    }

    private void showError(String message) {
        alertService.showError(localizeService.getLocalizedResource("alert.app.config.header"), message);
    }
}
