package das.tools.weather.exceptions;

public class ClientException extends ApplicationException {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String header, String message) {
        super(header, message);
    }
}
