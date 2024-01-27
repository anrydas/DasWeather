package das.tools.weather.exceptions;

import das.tools.weather.service.AlertService;
import javafx.application.Platform;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private final AlertService alertService;

    public RestTemplateResponseErrorHandler(AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == CLIENT_ERROR
                || response.getStatusCode().series() == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == CLIENT_ERROR) {
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                showFxError("Error getting data from server", "There was an error getting weather data. Try to change API key and try again");
            } else {
                showFxError("Error getting data from server", "There was a client error:\n" + response.getStatusText());
            }
        } else if (response.getStatusCode().series() == SERVER_ERROR) {
            showFxError("Error getting data from server", "There was a server error:\n" + response.getStatusText());
        } else {
            showFxError("Error", "There was an error during communication with remote:\n" + response.getStatusText());
        }
    }

    private void showFxError(String head, String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alertService.showError(head, message);
            }
        });
    }
}
