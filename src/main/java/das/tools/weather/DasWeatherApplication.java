package das.tools.weather;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Lazy
@EnableScheduling
@EnableAsync
public class DasWeatherApplication {
    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}
