package das.tools.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final GuiConfigService configService;
    private static volatile WeatherServiceImpl instance;

    public static WeatherServiceImpl getInstance() {
        if (instance == null) {
            synchronized (WeatherServiceImpl.class) {
                if (instance == null) {
                    instance = new WeatherServiceImpl();
                }
            }
        }
        return instance;
    }

    static {
        Map<Integer,Integer> map = WEATHER_CODE_CONDITION_IMAGES;
        map.put(1000, 113);
        map.put(1003, 116);
        map.put(1006, 119);
        map.put(1009, 122);
        map.put(1030, 143);
        map.put(1063, 176);
        map.put(1066, 179);
        map.put(1069, 182);
        map.put(1072, 185);
        map.put(1087, 200);
        map.put(1114, 227);
        map.put(1117, 230);
        map.put(1135, 248);
        map.put(1147, 260);
        map.put(1150, 263);
        map.put(1153, 266);
        map.put(1168, 281);
        map.put(1171, 284);
        map.put(1180, 293);
        map.put(1183, 296);
        map.put(1186, 299);
        map.put(1189, 302);
        map.put(1192, 305);
        map.put(1195, 308);
        map.put(1198, 311);
        map.put(1201, 314);
        map.put(1204, 317);
        map.put(1207, 320);
        map.put(1210, 323);
        map.put(1213, 326);
        map.put(1216, 329);
        map.put(1219, 332);
        map.put(1222, 335);
        map.put(1225, 338);
        map.put(1237, 350);
        map.put(1240, 353);
        map.put(1243, 356);
        map.put(1246, 359);
        map.put(1249, 362);
        map.put(1252, 365);
        map.put(1255, 368);
        map.put(1258, 371);
        map.put(1261, 374);
        map.put(1264, 377);
        map.put(1273, 386);
        map.put(1276, 389);
        map.put(1279, 392);
        map.put(1282, 395);
    }

    private WeatherServiceImpl() {
        configService = GuiConfigServiceImpl.getInstance();
    }

    @Override
    public CurrenWeatherResponse getCurrentWeather() {
        return null;
    }

    @Override
    public ForecastWeatherResponse getForecastWeather() {
        Properties props = configService.getCurrentConfig();
        ForecastWeatherResponse response = null;
        try {
            URIBuilder uri = new URIBuilder(props.getProperty(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY,
                    configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY)));
            uri.addParameter("key", props.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                    configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY)));
            uri.addParameter("q", props.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY,
                    configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY)));
            uri.addParameter("aqi", "yes");
            uri.addParameter("lang", props.getProperty(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY,
                    configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY)));
            uri.addParameter("days", "3");
            String url = uri.toString();
            if(log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: got url={}", url);

            response = getConvertedResponse(url);
            if(log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: response={}", response);
            log.info("Weather was updated");
        } catch (URISyntaxException e) {
            log.error("Couldn't get response from server: ", e);
            throw new RuntimeException(e);
        }
        return response;
    }

    private ForecastWeatherResponse getConvertedResponse(String url) {
        if (log.isDebugEnabled()) log.debug("getConvertedResponse Thread="+ Thread.currentThread().getName());
        String response = apiRequest(url);
        ForecastWeatherResponse res = ForecastWeatherResponse.builder().build();
        if (!"".equals(response)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                res = objectMapper.readValue(response, ForecastWeatherResponse.class);
                if (log.isDebugEnabled()) log.debug("got converted response={}", res);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    private String apiRequest(String url) {
        String res = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (log.isDebugEnabled()) log.debug("API Request Thread="+ Thread.currentThread().getName());
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    res = EntityUtils.toString(entity);
                }
            } catch (IOException e) {
                log.error("Error 1: Couldn't get data from {}", url, e);
            }
        } catch (IOException e) {
            log.error("Error 2: Couldn't get data from {}", url, e);
        }
        return res;
    }

    @Override
    public Image getRemoteImage(String imageUrl) {
        String url = "https:" + imageUrl;
        Image image = null;
        BufferedImage bufferedImage = getImageAsync(url);
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        String msg = image != null ? "[WeatherService].getRemoteImage: got weather image from {}" : "[WeatherService].getRemoteImage: Couldn't get weather image from {}" ;
        if(log.isDebugEnabled()) log.debug(msg, url);
        return image;
    }

    @Override
    public Image getWeatherIcon(int weatherCode, boolean isDay) {
        String path = "/images/conditions" + (isDay ? "/day" : "/night");
        String url = path + "/" + WEATHER_CODE_CONDITION_IMAGES.get(weatherCode) + ".png";
        if (log.isDebugEnabled()) log.debug("got image file url={}", url);
        return LoadingService.getInstance().getResourceImage(url);
    }

    private BufferedImage getImageAsync(String urlString) {
        CompletableFuture<BufferedImage> completableFuture =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return ImageIO.read(new URL(urlString));
                    } catch (IOException e) {
                        log.error("Error getting image from '{}'", urlString, e);
                    }
                    return null;
                });
        BufferedImage res = null;
        try {
            res = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting Image from server: ", e);
            throw new RuntimeException(e);
        }
        return res;
    }
}
