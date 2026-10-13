package das.tools.weather.exceptions;

/**
 * Unchecked exception thrown by the weather service when data couldn't be obtained.
 * Carries a localized header and message ready to be shown to the user.
 */
public class WeatherDataException extends RuntimeException {
    private final String header;

    public WeatherDataException(String header, String message, Throwable cause) {
        super(message, cause);
        this.header = header;
    }

    public WeatherDataException(ApplicationException cause) {
        this(cause.getHeader(), cause.getMessage(), cause);
    }

    public String getHeader() {
        return header;
    }
}
