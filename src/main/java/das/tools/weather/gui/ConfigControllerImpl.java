package das.tools.weather.gui;

import das.tools.weather.config.GuiConfig;
import das.tools.weather.service.AlertService;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.LocalizeResourcesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;

@Component
@Slf4j
public class ConfigControllerImpl implements ConfigController {
    @Autowired private GuiConfigService configService;
    @Autowired private LocalizeResourcesService localizeService;
    @Autowired private BuildProperties buildProperties;
    @Autowired private AlertService alertService;
    @Autowired private GuiConfig.ViewHolder guiLocationView;
    @Autowired private LocationController locationController;
    private Properties appProps;
    private boolean isConfigChanged;
    private Scene locationScene;
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
    public ConfigControllerImpl() {
    }

    @Override
    public void initLocale() {
        btSearchLocation.setText(localizeService.getLocalizedResource("button.search"));
        btSearchLocation.setTooltip(new Tooltip(localizeService.getLocalizedResource("button.search.tooltip")));
    }

    @FXML
    private void initialize() {
        btOk.setOnAction(actionEvent -> saveConfigAndClose());
        btCancel.setOnAction(actionEvent -> closeStage());
        btSearchLocation.setOnAction(actionEvent -> showCheckWindow());
    }

    @Override
    public void onShowingStage() {
        setLabelNames();
        appProps = configService.getCurrentConfig();
        edApiKey.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY)));
        edForecastUrl.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY)));
        edLocation.setText(appProps.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY)));
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
        cbCondLang.setItems(getLanguagesList());
        String langName = configService.getLangName(
                appProps.getProperty(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY)
                )
        );
        cbCondLang.getSelectionModel().select(langName);
    }

    private void showCheckWindow() {
        locationScene = locationScene == null ? new Scene(guiLocationView.getView()) : locationScene;
        Stage stage = new Stage();
        stage.setTitle(String.format("Das Weather Location (v.%s)", buildProperties.getVersion()));
        stage.setScene(locationScene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        locationController.initLocale();
        locationController.setLocation(edLocation.getText());
        stage.showAndWait();
    }

    private void setLabelNames() {
        lbApiKey.setText(localizeService.getLocalizedResource("label.key"));
        lbUrl.setText(localizeService.getLocalizedResource("label.url"));
        lbLocation.setText(localizeService.getLocalizedResource("label.location"));
        lbInterval.setText(localizeService.getLocalizedResource("label.interval"));
        lbLanguage.setText(localizeService.getLocalizedResource("label.language"));
        chbConfirmExit.setText(localizeService.getLocalizedResource("box.confirm.exit"));
        btCancel.setText(localizeService.getLocalizedResource( "button.close"));
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
            isConfigChanged = true;
        }
    }

    private void closeStage() {
        isConfigChanged = false;
        ((Stage) btCancel.getScene().getWindow()).close();
    }

    private void saveConfig() {
        updateConfigFromForm();
        configService.saveConfig(appProps);
    }

    private void updateConfigFromForm() {
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_API_KEY_KEY, edApiKey.getText());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY, edForecastUrl.getText());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY, edLocation.getText());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY, String.valueOf(chbConfirmExit.isSelected()));
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_UPDATE_INTERVAL_KEY, String.valueOf(minToMSec(spUpdateInterval.getValue())));
        String langCode = configService.getLangCode(cbCondLang.getValue());
        setPropertyIfChanged(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY, langCode);
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
        alertService.showError("Configuration error", message);
    }
}
