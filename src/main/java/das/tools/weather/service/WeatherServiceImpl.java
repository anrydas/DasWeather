package das.tools.weather.service;

import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherLocation;
import das.tools.weather.exceptions.RestTemplateResponseErrorHandler;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;
    private final GuiConfigService configService;
    private final RestTemplateResponseErrorHandler responseErrorHandler;
    private final AlertService alertService;

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

    public WeatherServiceImpl(RestTemplateBuilder restTemplateBuilder, GuiConfigService configService, RestTemplateResponseErrorHandler responseErrorHandler, AlertService alertService) {
        this.alertService = alertService;
        this.restTemplate = restTemplateBuilder
                .errorHandler(responseErrorHandler)
                .build();
        this.configService = configService;
        this.responseErrorHandler = responseErrorHandler;
    }

    @Override
    public CurrenWeatherResponse getCurrentWeather() {
        return null;
    }

    @Override
    public ForecastWeatherResponse getForecastWeather() {
        Properties props = configService.getCurrentConfig();
        ForecastWeatherResponse response = null;
        String url = ServletUriComponentsBuilder.fromHttpUrl(props.getProperty(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY)))
                .queryParam("key", props.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY)))
                .queryParam("q", getLocationParameterValue(props))
                .queryParam("aqi", "yes")
                .queryParam("lang", props.getProperty(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY)))
                .queryParam("days", "3")
                .toUriString();
        if(log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: got url={}", url);

        try {
            response = getResponseAsync(url);
            if(log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: response={}", response);
            log.info("Weather was updated");
        } catch (HttpClientErrorException e) {
            log.error("Couldn't get response from server: ", e);
            throw new RuntimeException(e);
        }

        return response;
    }

    private String getLocationParameterValue(Properties props) {
        String locationId = props.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_ID_KEY);
        String res = (locationId != null && !"".equals(locationId)) ?
                "id:"+locationId :
                props.getProperty(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_WEATHER_LOCATION_KEY));
        if (log.isDebugEnabled()) log.debug("got location parameter={}", res);
        return res;
    }

    private ForecastWeatherResponse getResponseAsync(String url) {
        CompletableFuture<ResponseEntity<ForecastWeatherResponse>> completableFuture =
                CompletableFuture.supplyAsync(() -> restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), ForecastWeatherResponse.class));
        ResponseEntity<ForecastWeatherResponse> response;
        try {
            response = completableFuture.get();
            return response.getBody();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting response from server: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeatherLocation[] getLocations(String location, String key) {
        Properties props = configService.getCurrentConfig();
        WeatherLocation[] res = null;
        String url = ServletUriComponentsBuilder.fromHttpUrl("http://api.weatherapi.com/v1/search.json")
                .queryParam("key", getApiKey(props, key))
                .queryParam("q", location)
                .toUriString();
        if(log.isDebugEnabled()) log.debug("[WeatherService].getLocation: got url={}", url);
        res = getLocationResponseAsync(url);
        if(log.isDebugEnabled()) log.debug("[WeatherService].getLocations: result={}", res);
        return res;
    }

    private String getApiKey(Properties props, String key) {
        String storedKey = props.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY));
        return (storedKey != null && !"".equals(storedKey)) ? storedKey : key;
    }

    private WeatherLocation[] getLocationResponseAsync(String url) {
        CompletableFuture<ResponseEntity<WeatherLocation[]>> completableFuture =
                CompletableFuture.supplyAsync(() -> restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),WeatherLocation[].class));
        ResponseEntity<WeatherLocation[]> response;
        try {
            response = completableFuture.get();
            return response.getBody();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting response from server: ", e);
            throw new RuntimeException(e);
        }
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
        return new Image(url);
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
