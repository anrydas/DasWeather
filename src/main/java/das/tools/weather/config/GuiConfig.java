package das.tools.weather.config;

import das.tools.weather.gui.CheckLocationController;
import das.tools.weather.gui.ConfigController;
import das.tools.weather.gui.ForecastController;
import das.tools.weather.gui.GuiController;
import das.tools.weather.service.UTF8ControlImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;


@Configuration
public class GuiConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean(name = "utf8Control")
    public ResourceBundle.Control getUtf8Control() {
        return new UTF8ControlImpl();
    }

    @Bean(name = "guiMainView")
    public ViewHolder getGuiView() throws IOException {
        return loadView("fxml/Main.fxml");
    }

    @Bean @Primary
    public GuiController getGuiController() throws IOException {
        return (GuiController) getGuiView().getController();
    }

    @Bean(name = "guiConfigView")
    public ViewHolder getConfigView() throws IOException {
        return loadView("fxml/Config.fxml");
    }

    @Bean @Primary
    public ConfigController getConfigController() throws IOException {
        return (ConfigController) getConfigView().getController();
    }

    @Bean(name = "guiForecastView")
    public ViewHolder getForecastView() throws IOException {
        return loadView("fxml/Forecast.fxml");
    }

    @Bean @Primary
    public ForecastController getForecastController() throws IOException {
        return (ForecastController) getForecastView().getController();
    }

    @Bean(name = "guiLocationView")
    public ViewHolder getSearchView() throws IOException {
        return loadView("fxml/CheckLocation.fxml");
    }

    @Bean @Primary
    public CheckLocationController geLocationController() throws IOException {
        return (CheckLocationController) getSearchView().getController();
    }

    protected ViewHolder loadView(String url) throws IOException {
        try (InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream(url)) {
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            return new ViewHolder(loader.getRoot(), loader.getController());
        }
    }

    @Getter @Setter @AllArgsConstructor @Builder
    public static class ViewHolder {
        private Parent view;
        private Object controller;
    }
}
