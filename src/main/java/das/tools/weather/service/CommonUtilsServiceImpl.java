package das.tools.weather.service;

import das.tools.weather.gui.GuiController;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
public class CommonUtilsServiceImpl implements CommonUtilsService {

    private final GuiConfigService configService;

    public CommonUtilsServiceImpl(GuiConfigService configService) {
        this.configService = configService;
    }

    @Override
    public int toIntTime(String s) {
        String[] split = s.split(":");
        int h = Integer.parseInt(split[0]);
        int m = Integer.parseInt(split[1]);
        return h*60 + m;
    }

    @Override
    public int toIntColor(String c) {
        return Integer.parseInt(c.substring(1), 16);
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
        LocalTime startTime = LocalTime.parse(start, GuiController.TIME_FORMATTER_FOR_RESPONSE);
        LocalTime stopTime = LocalTime.parse(stop, GuiController.TIME_FORMATTER_FOR_RESPONSE);
        return Duration.between(startTime, stopTime).getSeconds();
    }

    @Override
    public int getCorrectedPressureValue(double sourcePressure) {
        return (int) (sourcePressure + Integer.parseInt(configService.getConfigStringValue(GuiConfigService.GUI_PRESSURE_CORRECTION_KEY)));
    }
}
