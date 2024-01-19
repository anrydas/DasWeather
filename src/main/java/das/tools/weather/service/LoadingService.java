package das.tools.weather.service;

import javafx.scene.image.Image;

import java.util.Objects;

public class LoadingService {
    private static volatile LoadingService instance;

    public static LoadingService getInstance() {
        if (instance == null) {
            synchronized (LoadingService.class) {
                if (instance == null) {
                    instance = new LoadingService();
                }
            }
        }
        return instance;
    }

    public Image getResourceImage(String resource) {
        return new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(resource)));
    }
}
