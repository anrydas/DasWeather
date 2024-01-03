package das.tools.weather.exceptions;

import java.io.IOException;

public class ServerException extends IOException {
    public ServerException(String message) {
        super(message);
    }
}
