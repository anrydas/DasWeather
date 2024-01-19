package das.tools.weather;

import das.tools.weather.controller.UpdateWeatherController;
import das.tools.weather.gui.GuiController;
import das.tools.weather.service.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;

public class DasWeatherApplication extends Application {
    public static final String APP_VERSION = "3.0.0-RELEASE";
    private final LocalizeResourcesService localizeService = LocalizeResourcesServiceImpl.getInstance();

    @Override
    public void start(Stage stage) {
        try(InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream("fxml/Main.fxml")) {
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            Scene scene = new Scene(loader.getRoot());
            stage.getIcons().add(LoadingService.getInstance().getResourceImage(GuiController.IMAGE_WEATHER_DEFAULT_ICON_PNG));
            stage.setTitle("Das Weather");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                boolean isConfirmExit = Boolean.parseBoolean(GuiConfigServiceImpl.getInstance().getConfigStringValue(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY, "true"));
                if (isConfirmExit) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            localizeService.getLocalizedResource("alert.app.exit.text"));
                    alert.setTitle(localizeService.getLocalizedResource("alert.app.exit.title"));
                    alert.setHeaderText("Das Weather Application");
                    Optional<ButtonType> option = alert.showAndWait();
                    if (ButtonType.CANCEL.equals(option.orElse(null))) {
                        event.consume();
                        return;
                    }
                }
                Platform.exit();
                System.exit(0);
            });
            GuiController guiController = loader.getController();
            stage.setOnShowing(event -> guiController.onShowingStage());
            stage.show();
            guiController.updateWeatherData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(UpdateWeatherController.getInstance(), 10000, 600000);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
