package das.tools.weather.gui;

import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.service.WeatherService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

//@Component
@Slf4j
public class GuiControllerImpl {
    public static final String APPLICATION_TITLE = "Das Weather: %s %s";
    @FXML
    private Label lbLocation;
    @FXML
    private Label lbCondition;
    @FXML
    private Label lbTemperature;
    @FXML
    private Label lbAdd1;
    @FXML
    private Label lbAdd2;
    @FXML
    private Button btUpdate;
    @FXML
    private ImageView imgWeather;

    @Autowired
    private WeatherService weatherService;

    private double currentProgress = 0;

    public GuiControllerImpl() {
    }

    @FXML
    private void initialize() {
        btUpdate.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");
        btUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateWeatherAndControls();
            }
        });
    }

    private void updateControls(ForecastWeatherResponse response) {
        WeatherCurrent current = response.getCurrent();
        String updateDate = current.getLastUpdate().split(" ")[0];
        String updateTime = current.getLastUpdate().split(" ")[1];
        Image remoteImage = weatherService.getRemoteImage(current.getCondition().getIcon());

        Stage stage = (Stage) lbLocation.getScene().getWindow();
        stage.getIcons().removeAll();
        stage.getIcons().add(remoteImage);

        imgWeather.setImage(remoteImage);
        String conditionText = current.getCondition().getText();
        Tooltip.install(imgWeather, new Tooltip(conditionText));

        lbCondition.setText(conditionText);
        lbCondition.setTooltip(getTooltip(conditionText));
        stage.setTitle(String.format(APPLICATION_TITLE,
                response.getLocation().getName(),
                current.getLastUpdate()
        ));

        String MSG_LOCATION = "%s %s at %s";
        lbLocation.setText(String.format(MSG_LOCATION,
                response.getLocation().getName(),
                updateDate,
                updateTime
        ));
        lbLocation.setTooltip(getTooltip(String.format("%s, %s %s at %s",
                response.getLocation().getName(),
                response.getLocation().getRegion(),
                updateDate,
                updateTime
        )));

        String MSG_TEMPERATURE = "\uD83D\uDD25 %.0f\u2103 (fills %.0f\u2103) \uD83C\uDF2B %d\uFF05";
        lbTemperature.setText(String.format(MSG_TEMPERATURE,
                current.getTemperatureC(),
                current.getFeelsLike(),
                current.getHumidity()));
        lbTemperature.setTooltip(getTooltip("Temperature (Fills like) Humidity"));

        String MSG_ADD1 = "\uD83D\uDCA8 %s %.0f (upto %.0f) km/h";
        lbAdd1.setText(String.format(MSG_ADD1,
                current.getWindDirection(),
                current.getWindKmh(),
                current.getGust()
        ));
        lbAdd1.setTooltip(getTooltip("Wind direction, Wind speed (Gusts)"));

        String MSG_ADD2 = "\u2601 %d\uFF05  \u2614 %.0f mm  \uD83D\uDD3D %.0f mmHg";
        lbAdd2.setText(String.format(MSG_ADD2,
                current.getCloud(),
                current.getPrecipitation(),
                millibarToMmHg(current.getPressureMb())
        ));
        lbAdd2.setTooltip(getTooltip("Cloud, Precipitation, Pressure"));
    }

    private Tooltip getTooltip(String caption) {
        return new Tooltip(caption);
    }

    public void updateWeatherAndControls() {
        ForecastWeatherResponse response = weatherService.getForecastWeather();
        updateControls(response);
    }

    private double millibarToMmHg(float mbar) {
        return mbar * 0.750062;
    }

}
