package das.tools.weather.gui;

import das.tools.weather.service.GuiConfigService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;

@Component
@Slf4j
public class ConfigControllerImpl implements ConfigController {
    @Autowired
    private GuiConfigService configService;
    private Properties appProps;
    private boolean isConfigChanged;

    @FXML
    private TextField edApiKey;
    @FXML
    private TextField edForecastUrl;
    @FXML
    private TextField edLocation;
    @FXML
    private Spinner<Integer> spUpdateInterval;
    @FXML
    private ComboBox<String> cbCondLang;
    @FXML
    private CheckBox chbConfirmExit;
    @FXML
    private Button btOk;
    @FXML
    private Button btCancel;
    public ConfigControllerImpl() {
    }

    @FXML
    private void initialize() {
        btOk.setOnAction(actionEvent -> saveConfigAndClose());
        btCancel.setOnAction(actionEvent -> closeStage());
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
        String msgEmpty = "%s couldn't be empty";
        if ("".equals(edApiKey.getText())) {
            showError(String.format(msgEmpty, "API key"));
            return false;
        }
        if ("".equals(edLocation.getText())) {
            showError(String.format(msgEmpty, "Location"));
            return false;
        }
        if ("".equals(edForecastUrl.getText())) {
            showError(String.format(msgEmpty, "Forecast URL"));
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                message,
                ButtonType.OK);
        alert.setTitle("Configuration error");
        alert.showAndWait();
    }
}
