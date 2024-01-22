package das.tools.weather.gui;

import das.tools.weather.DasWeatherApplication;
import das.tools.weather.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;

@Slf4j
public class ConfigControllerImpl implements ConfigController {
    private GuiConfigService configService;
    private LocalizeResourcesService localizeService;
    private Properties appProps;
    private boolean isConfigChanged;
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
        localizeService = LocalizeResourcesServiceImpl.getInstance();
        configService = GuiConfigServiceImpl.getInstance();
        btSearchLocation.setText(localizeService.getLocalizedResource("button.search"));
        btSearchLocation.setTooltip(new Tooltip(localizeService.getLocalizedResource("button.search.tooltip")));
    }

    @FXML
    private void initialize() {
        btOk.setOnAction(actionEvent -> saveConfigAndClose());
        btCancel.setOnAction(actionEvent -> closeStage());
        btSearchLocation.setOnAction(actionEvent -> showCheckWindow());
    }

    private void showCheckWindow() {
        try(InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream("fxml/CheckLocation.fxml")) {
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            Scene scene = new Scene(loader.getRoot());
            Stage stage = new Stage();
            stage.setTitle(String.format("Search Location (v.%s)", DasWeatherApplication.APP_VERSION));
            stage.setScene(scene);
            CheckLocationController controller = loader.getController();
            controller.initLocale();
            controller.setLocation(edLocation.getText());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        String msgEmpty = "%s couldn't be empty";
        String msgNotValid = "%s is not valid";
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
        Matcher matchKey = API_KEY_PATTERN.matcher(edApiKey.getText());
        if (!matchKey.find()) {
            showError(String.format(msgNotValid, "API key"));
            return false;
        }
        matchKey = FORECAST_URL_PATTERN.matcher(edForecastUrl.getText());
        if (!matchKey.find()) {
            showError(String.format(msgNotValid, "Forecast URL"));
            return false;
        }
        return true;
    }

    private void showError(String message) {
        AlertService.getInstance().showError("Configuration error", message);
    }
}
