package das.tools.weather.exceptions;

import das.tools.weather.service.LocalizeResourcesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private final LocalizeResourcesService localizeService;

    public RestTemplateResponseErrorHandler(LocalizeResourcesService localizeService) {
        this.localizeService = localizeService;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus.Series series = HttpStatus.Series.resolve(response.getRawStatusCode());
        return series == CLIENT_ERROR || series == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        int statusCode = response.getRawStatusCode();
        HttpStatus.Series series = HttpStatus.Series.resolve(statusCode);
        String header = localizeService.getLocalizedResource("alert.ex.header.common");
        if (series == CLIENT_ERROR) {
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                throw new ClientException(header, localizeService.getLocalizedResource("alert.ex.message.key"));
            }
            throw new ClientException(header,
                    localizeService.getLocalizedResource("alert.ex.message.client") + "\n" + response.getStatusText());
        }
        if (series == SERVER_ERROR) {
            throw new ServerException(header,
                    localizeService.getLocalizedResource("alert.ex.message.server") + "\n" + response.getStatusText());
        }
        throw new ApplicationException(localizeService.getLocalizedResource("alert.ex.header.error"),
                localizeService.getLocalizedResource("alert.ex.message.common") + "\n" + response.getStatusText());
    }
}
