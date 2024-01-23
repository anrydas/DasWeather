package das.tools.weather.service;

import javafx.scene.control.ButtonType;

import java.util.Optional;

public interface AlertService {
    void showError(String header, String content);

    void showError(String title, String header, String content);

    void showInfo(String header, String content);

    void showInfo(String title, String header, String content);

    Optional<ButtonType> showConfirm(String title, String header, String content);
}
