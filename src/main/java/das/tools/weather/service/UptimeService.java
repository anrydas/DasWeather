package das.tools.weather.service;

public interface UptimeService {
    int MIN_UPTIME_TEXT_WIDTH = 5;
    double MIN_UPTIME_LABEL_WIDTH = 30;
    double MAX_UPTIME_LABEL_WIDTH = 65;
    long getUptimeMillis();

    String getFormattedUptime();
}
