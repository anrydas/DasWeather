package das.tools.weather;

import das.tools.weather.entity.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        // Unexpected errors in event handlers and background threads shouldn't pass unnoticed
        Thread.setDefaultUncaughtExceptionHandler((thread, e) ->
                log.error("Uncaught exception in thread '{}': ", thread.getName(), e));
        this.context = new SpringApplicationBuilder()
                .sources(DasWeatherApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) throws Exception {
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() throws Exception {
        if (this.context != null) {
            this.context.close();
        }
        Platform.exit();
    }
}
