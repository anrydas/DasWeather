package das.tools.weather.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertService {
    private static volatile AlertService instance;

    public static AlertService getInstance() {
        if (instance == null) {
            synchronized (AlertService.class) {
                if (instance == null) {
                    instance = new AlertService();
                }
            }
        }
        return instance;
    }

    public AlertService() {
    }

    public void showError(String header, String content) {
        showDialog(Alert.AlertType.ERROR, "Error", header, content);
    }
    public void showError(String title, String header, String content) {
        showDialog(Alert.AlertType.ERROR, title, header, content);
    }

    public void showInfo(String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, "Information", header, content);
    }

    public void showInfo(String title, String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    public Optional<ButtonType> showConfirm(String title, String header, String content) {
        return showDialogAndWait(Alert.AlertType.CONFIRMATION, title, header, content);
    }

    private void showDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showDialogAndWait(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
