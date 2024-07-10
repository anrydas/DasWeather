package das.tools.weather.service;

public interface UptimeService {
    long getUptimeMillis();

    String getFormattedUptime();
}
