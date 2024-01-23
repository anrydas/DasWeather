package das.tools.weather.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == CLIENT_ERROR
                || response.getStatusCode().series() == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == CLIENT_ERROR) {
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ClientException("There was an error getting weather data. Try to change API key ant try again.");
            } else {
                throw new ClientException("There was a client error.");
            }
        } else if (response.getStatusCode().series() == SERVER_ERROR) {
            throw new ServerException("There was a server error.");
        } else {
            throw new ApplicationException("There was an error during communication with remote.");
        }
    }
}
