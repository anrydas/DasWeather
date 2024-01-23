package das.tools.weather.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlertServiceImpl implements AlertService {

    @Override
    public void showError(String header, String content) {
        showDialog(Alert.AlertType.ERROR, "Error", header, content);
    }
    @Override
    public void showError(String title, String header, String content) {
        showDialog(Alert.AlertType.ERROR, title, header, content);
    }

    @Override
    public void showInfo(String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, "Information", header, content);
    }

    @Override
    public void showInfo(String title, String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    @Override
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
