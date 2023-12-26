package das.tools.weather.config;

import das.tools.weather.gui.ConfigController;
import das.tools.weather.gui.ConfigControllerImpl;
import das.tools.weather.gui.GuiController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.io.InputStream;


@Configuration
public class GuiConfig {
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
