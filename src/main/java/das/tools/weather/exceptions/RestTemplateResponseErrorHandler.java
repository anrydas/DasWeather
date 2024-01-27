package das.tools.weather.exceptions;

import das.tools.weather.service.AlertService;
import das.tools.weather.service.LocalizeResourcesService;
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
    private final LocalizeResourcesService localizeService;

    public RestTemplateResponseErrorHandler(AlertService alertService, LocalizeResourcesService localizeService) {
        this.alertService = alertService;
        this.localizeService = localizeService;
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
                showFxError(localizeService.getLocalizedResource("alert.ex.header.common"),
                        localizeService.getLocalizedResource("alert.ex.message.key"));
            } else {
                showFxError(localizeService.getLocalizedResource("alert.ex.header.common"),
                        localizeService.getLocalizedResource("alert.ex.message.client") + "\n"
                                + response.getStatusText());
            }
        } else if (response.getStatusCode().series() == SERVER_ERROR) {
            showFxError(localizeService.getLocalizedResource("alert.ex.header.common"),
                    localizeService.getLocalizedResource("alert.ex.message.server") + "\n" +
                            response.getStatusText());
        } else {
            showFxError(localizeService.getLocalizedResource("alert.ex.header.error"),
                    localizeService.getLocalizedResource("alert.ex.message.common") + "\n" +
                            response.getStatusText());
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
