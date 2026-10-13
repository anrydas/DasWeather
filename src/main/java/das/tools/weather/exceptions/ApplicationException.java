package das.tools.weather.exceptions;

import java.io.IOException;

public class ApplicationException extends IOException {
    private final String header;

    public ApplicationException(String message) {
        this(null, message);
    }

    public ApplicationException(String header, String message) {
        super(message);
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
