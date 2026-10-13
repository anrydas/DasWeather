package das.tools.weather.exceptions;

public class ServerException extends ApplicationException {
    public ServerException(String message) {
        super(message);
    }

    public ServerException(String header, String message) {
        super(header, message);
    }
}
