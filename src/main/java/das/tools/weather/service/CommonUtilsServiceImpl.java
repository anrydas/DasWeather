package das.tools.weather.service;

import das.tools.weather.gui.GuiController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
public class CommonUtilsServiceImpl implements CommonUtilsService {
    private static final int DEFAULT_COLOR = 0xFFFFFF;

    private final GuiConfigService configService;

    public CommonUtilsServiceImpl(GuiConfigService configService) {
        this.configService = configService;
    }

    @Override
    public int toIntTime(String s) {
        try {
            String[] split = s.split(":");
            int h = Integer.parseInt(split[0].trim());
            int m = Integer.parseInt(split[1].trim());
            return h*60 + m;
        } catch (RuntimeException e) {
            log.warn("Couldn't parse time '{}': {}", s, e.toString());
            return 0;
        }
    }

    @Override
    public int toIntColor(String c) {
        try {
            return Integer.parseInt(c.substring(1), 16);
        } catch (RuntimeException e) {
            log.warn("Couldn't parse color '{}': {}", c, e.toString());
            return DEFAULT_COLOR;
        }
    }

    @Override
    public String getTimeLength(String start, String stop) {
        long diff = getDiffSeconds(start, stop);
        long hours = diff / (60 * 60) % 24;
        long minutes = diff / (60) % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public long getDiffSeconds(String start, String stop) {
        // API returns text like 'No sunset' for polar days and nights
        try {
            LocalTime startTime = LocalTime.parse(start, GuiController.TIME_FORMATTER_FOR_RESPONSE);
            LocalTime stopTime = LocalTime.parse(stop, GuiController.TIME_FORMATTER_FOR_RESPONSE);
            return Duration.between(startTime, stopTime).getSeconds();
        } catch (DateTimeParseException | NullPointerException e) {
            log.warn("Couldn't calculate time length between '{}' and '{}': {}", start, stop, e.toString());
            return 0;
        }
    }

    @Override
    public int getCorrectedPressureValue(double sourcePressure) {
        return (int) (sourcePressure + configService.getConfigIntValue(GuiConfigService.GUI_PRESSURE_CORRECTION_KEY));
    }
}
