package das.tools.weather;

import das.tools.weather.entity.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
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
        this.context.close();
        Platform.exit();
    }
}
