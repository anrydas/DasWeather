package das.tools.weather.gui;

import das.tools.weather.DasWeatherApplication;
import das.tools.weather.entity.StageReadyEvent;
import das.tools.weather.service.AlertService;
import das.tools.weather.service.GuiConfigService;
import das.tools.weather.service.LocalizeResourcesService;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;
    private final GuiConfigService guiConfig;
    private final GuiController guiController;
    private final LocalizeResourcesService localizeService;
    private final AlertService alertService;

    public PrimaryStageInitializer(FxWeaver fxWeaver, GuiConfigService guiConfig, GuiController guiController, LocalizeResourcesService localizeService, AlertService alertService) {
        this.fxWeaver = fxWeaver;
        this.guiConfig = guiConfig;
        this.guiController = guiController;
        this.localizeService = localizeService;
        this.alertService = alertService;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.stage;
        Scene scene = new Scene(fxWeaver.loadView(GuiControllerImpl.class));
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(DasWeatherApplication.class.getResourceAsStream(GuiController.IMAGE_WEATHER_DEFAULT_ICON_PNG))));
        stage.setTitle("Das Weather");
        stage.setResizable(false);
        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.setIconified(true);
            }
        });

        stage.setOnCloseRequest(e -> {
            boolean isConfirmExit = Boolean.parseBoolean(guiConfig.getConfigStringValue(GuiConfigService.GUI_CONFIG_CONFIRM_EXIT_KEY, "true"));
            if (isConfirmExit) {
                Optional<ButtonType> option = alertService.showConfirm(
                        localizeService.getLocalizedResource("alert.app.exit.text"),
                        localizeService.getLocalizedResource("alert.app.exit.title"),
                        "Das Weather Application");
                if (ButtonType.CANCEL.equals(option.orElse(null))) {
                    e.consume();
                    return;
                }
            }
            Platform.exit();
            System.exit(0);
        });
        stage.setOnShowing(e -> guiController.onShowingStage());
        stage.show();
    }
}
