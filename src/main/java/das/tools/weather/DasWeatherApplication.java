package das.tools.weather;

import das.tools.weather.config.GuiConfig;
import das.tools.weather.gui.GuiController;
import das.tools.weather.service.GuiConfigService;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@SpringBootApplication
@Lazy
@EnableScheduling
@EnableAsync
public class DasWeatherApplication extends AbstractJavaFxApplicationSupport {
    @Autowired
    private GuiConfig.ViewHolder guiMainView;
    @Autowired
    private GuiConfigService guiConfig;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(guiMainView.getView());
        stage.getIcons().add(new Image(Objects.requireNonNull(DasWeatherApplication.class.getResourceAsStream(GuiController.IMAGE_WEATHER_DEFAULT_ICON_PNG))));
        stage.setTitle("Das Weather");
        stage.setResizable(false);
        stage.setScene(scene);

            stage.setOnCloseRequest(event -> {
                boolean isConfirmExit = Boolean.parseBoolean(guiConfig.getConfigStringValue(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY, "true"));
                if (isConfirmExit) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Do you really want exit?");
                    alert.setTitle("Confirm");
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

        stage.show();
    }

    public static void main(String[] args) {
        launchApp(DasWeatherApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
