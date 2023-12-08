package das.tools.weather;

import das.tools.weather.config.GuiConfig;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@SpringBootApplication
@Lazy
@EnableScheduling
@EnableAsync
public class DasWeatherApplication extends AbstractJavaFxApplicationSupport {
    @Autowired
    private GuiConfig.ViewHolder guiView;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(guiView.getView());
        stage.getIcons().add(new Image(Objects.requireNonNull(DasWeatherApplication.class.getResourceAsStream("/images/weather-default-01.png"))));
        stage.setTitle("Das Weather");
        stage.setResizable(false);
        stage.setScene(scene);
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
