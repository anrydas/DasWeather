package das.tools.weather.service;

import das.tools.weather.entity.current.WeatherCurrent;
import das.tools.weather.entity.forecast.WeatherAstro;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.entity.forecast.WeatherHour;
import das.tools.weather.gui.GuiControllerImpl;
import das.tools.weather.gui.color.ColorElement;
import das.tools.weather.gui.color.ColorEngineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class CbwmServiceImpl implements CbwmService { // CBWM - Color Bricks Weather Map
    private final int WIDTH = 159;
    private final int HEIGHT = 111;
    private final CommonUtilsService commonUtils;

    public CbwmServiceImpl(CommonUtilsService commonUtils) {
        this.commonUtils = commonUtils;
    }

    @Override
    public BufferedImage getCbwmImage(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex, WeatherCurrent current) {
        BufferedImage cbwmImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cbwmImage.createGraphics();
        drawCbwmImage(g2d, dataHolder, hourIndex, current);
        g2d.dispose();
        return cbwmImage;
    }

    private void drawCbwmImage(Graphics2D g2d, ChartDataProducerImpl.DataHolder dataHolder, int hourIndex, WeatherCurrent current) {
        drawRectsLine(g2d, 0, get0LineColors(dataHolder, hourIndex));
        drawRectsLine(g2d, 1, get1LineColors(dataHolder, hourIndex));
        drawRectsLine(g2d, 2, get2LineColors(dataHolder, hourIndex));
        drawRectsLine(g2d, 3, get3LineColors(dataHolder, hourIndex));
        drawRectsLine(g2d, 4, get4LineColors(dataHolder, hourIndex, current));
    }

    private int[] get0LineColors(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex) {
        WeatherDayForecast day = dataHolder.getDayForecastData();
        WeatherHour hour = day.getHour()[hourIndex];
        int[] res = new int[4];
        res[0] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.TEMPERATURE).getColor((int) hour.getTemperature()));
        res[1] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.TEMPERATURE).getColor((int) hour.getFeelsLike()));
        res[2] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.TEMPERATURE).getColor((int) day.getDay().getAvgTemperature()));
        res[3] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.HUMIDITY).getColor(hour.getHumidity()));
        return res;
    }

    private int[] get1LineColors(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex) {
        WeatherHour hour = dataHolder.getDayForecastData().getHour()[hourIndex];
        int[] res = new int[3];
        res[0] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.CLOUDY).getColor(hour.getCloud()));
        res[1] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.PRECIPITATIONS).getColor((int) hour.getPrecipitation()));
        double pressureMmHg = GuiControllerImpl.millibarToMmHg(commonUtils.getCorrectedPressureValue(hour.getPressure()));
        res[2] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.PRESSURE).getColor((int) pressureMmHg));
        return res;
    }

    private int[] get2LineColors(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex) {
        WeatherHour hour = dataHolder.getDayForecastData().getHour()[hourIndex];
        int[] res = new int[3];
        res[0] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.WIND_DIRECTION).getColor(hour.getWindDegree()));
        res[1] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.WIND_SPEED).getColor((int) hour.getWindKmh()));
        res[2] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.WIND_SPEED).getColor((int) hour.getGustKmh()));
        return res;
    }

    private int[] get3LineColors(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex) {
        WeatherHour hour = dataHolder.getDayForecastData().getHour()[hourIndex];
        WeatherAstro astro = dataHolder.getDayForecastData().getAstro();
        String dayLength = commonUtils.getTimeLength(astro.getSunRise(), astro.getSunSet());
        int[] res = new int[3];
        res[0] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.VISIBILITY).getColor((int) hour.getVisibilityKm() * 1000));
        res[1] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.UV_INDEX).getColor((int) hour.getUvIndex()));
        res[2] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.DAY_LENGTH).getColor(commonUtils.toIntTime(dayLength)));
        return res;
    }

    private int[] get4LineColors(ChartDataProducerImpl.DataHolder dataHolder, int hourIndex, WeatherCurrent current) {
        WeatherHour hour = dataHolder.getDayForecastData().getHour()[hourIndex];
        int[] res = new int[1];
        res[0] = commonUtils.toIntColor(ColorEngineFactory.getEngine(ColorElement.AIR_QUALITY).getColor((int) current.getAirQuality().getIndex()));
        return res;
    }

    private void drawRectsLine(Graphics2D g2d, int lineNumber, int[] colors) {
        int blocksPerLine = colors.length;
        int fullW = WIDTH - (blocksPerLine == 1 ? 1 : 2);
        int eachHeight = (HEIGHT-1) / 5 - 1;
        int eachWidth = fullW/blocksPerLine;
        int y0 = 1 + lineNumber * (1 + eachHeight);
        for (int i = 0; i < blocksPerLine; i++) {
            drawRect(g2d, 1 + i * eachWidth, y0,
                    eachWidth + (fullW % blocksPerLine == 0 ? -1 : 1) - 2,
                    eachHeight, new Color(colors[i]));
        }
    }

    private void drawRect(Graphics2D g2d, int x, int y, int w, int h, Color color) {
        g2d.setColor(color);
        g2d.fillRect(x, y, w, h);
    }
}
