package das.tools.weather.exceptions;

import java.io.IOException;

public class ApplicationException extends IOException {
    public ApplicationException(String message) {
        super(message);
    }
}
