package das.tools.weather.service;

public interface CommonUtilsService {
    int toIntTime(String s);

    int toIntColor(String c);

    String getTimeLength(String start, String stop);

    long getDiffSeconds(String start, String stop);

    int getCorrectedPressureValue(double sourcePressure);
}
