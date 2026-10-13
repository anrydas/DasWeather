package das.tools.weather.service;

import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import das.tools.weather.entity.current.WeatherLocation;
import das.tools.weather.entity.forecast.WeatherDayForecast;
import das.tools.weather.exceptions.ApplicationException;
import das.tools.weather.exceptions.RestTemplateResponseErrorHandler;
import das.tools.weather.exceptions.WeatherDataException;
import das.tools.weather.gui.GuiController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(30);
    private static final int FORECAST_DAYS = 3;
    private static final Pattern API_KEY_IN_URL_PATTERN = Pattern.compile("(?i)(key=)[^&\\s\"]*");

    private final RestTemplate restTemplate;
    private final GuiConfigService configService;
    private final LocalizeResourcesService localizeService;
    private final Map<String, Image> iconsCache = new ConcurrentHashMap<>();

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

    public WeatherServiceImpl(RestTemplateBuilder restTemplateBuilder, GuiConfigService configService, RestTemplateResponseErrorHandler responseErrorHandler, LocalizeResourcesService localizeService) {
        this.localizeService = localizeService;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .errorHandler(responseErrorHandler)
                .build();
        this.configService = configService;
    }

    @Override
    public CurrenWeatherResponse getCurrentWeather() {
        return null;
    }

    @Override
    public ForecastWeatherResponse getForecastWeather() {
        Properties props = configService.getCurrentConfig();
        String url = UriComponentsBuilder.fromHttpUrl(props.getProperty(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY)))
                .queryParam("key", props.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY)))
                .queryParam("q", getLocationParameterValue(props))
                .queryParam("aqi", "yes")
                .queryParam("lang", props.getProperty(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY,
                        configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_CONDITION_LANGUAGE_KEY)))
                .queryParam("days", FORECAST_DAYS)
                .toUriString();
        if (log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: got url={}", maskApiKey(url));

        ForecastWeatherResponse response = requestData(url, ForecastWeatherResponse.class);
        if (log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: response={}", response);
        validateForecastResponse(response);
        log.info("Weather was updated");
        return response;
    }

    private void validateForecastResponse(ForecastWeatherResponse response) {
        String problem = null;
        if (response == null) {
            problem = "empty response";
        } else if (response.getCurrent() == null || response.getCurrent().getCondition() == null) {
            problem = "no current weather";
        } else if (response.getLocation() == null) {
            problem = "no location";
        } else if (response.getForecast() == null || response.getForecast().getDayForecast() == null
                || response.getForecast().getDayForecast().length < FORECAST_DAYS) {
            problem = "not enough forecast days";
        } else {
            for (WeatherDayForecast day : response.getForecast().getDayForecast()) {
                if (day == null || day.getDay() == null || day.getDay().getCondition() == null
                        || day.getAstro() == null || day.getHour() == null || day.getHour().length == 0) {
                    problem = "incomplete forecast day";
                    break;
                }
            }
        }
        if (problem != null) {
            log.error("Got invalid forecast response ({}): {}", problem, response);
            throw new WeatherDataException(
                    localizeService.getLocalizedResource("alert.ex.header.common"),
                    localizeService.getLocalizedResource("alert.ex.message.common") + "\n" + problem,
                    null);
        }
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

    private <T> T requestData(String url, Class<T> responseType) {
        try {
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), responseType).getBody();
        } catch (RestClientException e) {
            ApplicationException appException = findApplicationException(e);
            if (appException != null) {
                log.error("Error response from server: {}", appException.getMessage());
                throw new WeatherDataException(appException);
            }
            String message = maskApiKey(e.getMessage());
            log.error("Error getting response from server: {}", message);
            throw new WeatherDataException(
                    localizeService.getLocalizedResource("alert.ex.header.common"),
                    localizeService.getLocalizedResource("alert.ex.message.common") + "\n" + message,
                    null);
        }
    }

    private static ApplicationException findApplicationException(Throwable e) {
        for (Throwable t = e; t != null; t = (t.getCause() == t) ? null : t.getCause()) {
            if (t instanceof ApplicationException) {
                return (ApplicationException) t;
            }
        }
        return null;
    }

    static String maskApiKey(String s) {
        return s == null ? "" : API_KEY_IN_URL_PATTERN.matcher(s).replaceAll("$1***");
    }

    @Override
    public WeatherLocation[] getLocations(String location, String key) {
        Properties props = configService.getCurrentConfig();
        String baseUrl = props.getProperty(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_FORECAST_URL_KEY))
                .replace("forecast", "search");
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("key", getApiKey(props, key))
                .queryParam("q", location)
                .toUriString();
        if (log.isDebugEnabled()) log.debug("[WeatherService].getLocation: got url={}", maskApiKey(url));
        WeatherLocation[] res = requestData(url, WeatherLocation[].class);
        if (log.isDebugEnabled()) log.debug("[WeatherService].getLocations: result={}", (Object) res);
        return res != null ? res : new WeatherLocation[0];
    }

    private String getApiKey(Properties props, String key) {
        String storedKey = props.getProperty(GuiConfigService.GUI_CONFIG_API_KEY_KEY,
                configService.getDefaultConfigValue(GuiConfigService.GUI_CONFIG_API_KEY_KEY));
        return (storedKey != null && !"".equals(storedKey)) ? storedKey : key;
    }

    @Override
    public Image getRemoteImage(String imageUrl) {
        String url = "https:" + imageUrl;
        BufferedImage bufferedImage = readImage(url);
        if (bufferedImage == null) {
            log.warn("[WeatherService].getRemoteImage: Couldn't get weather image from {}", url);
            return null;
        }
        if (log.isDebugEnabled()) log.debug("[WeatherService].getRemoteImage: got weather image from {}", url);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    @Override
    public Image getWeatherIcon(int weatherCode, boolean isDay) {
        Integer imageCode = WEATHER_CODE_CONDITION_IMAGES.get(weatherCode);
        String url = imageCode != null ?
                "/images/conditions" + (isDay ? "/day" : "/night") + "/" + imageCode + ".png" :
                GuiController.IMAGE_WEATHER_DEFAULT_ICON_PNG;
        if (imageCode == null) log.warn("Unknown weather code {}, default icon will be used", weatherCode);
        return iconsCache.computeIfAbsent(url, this::loadResourceImage);
    }

    private Image loadResourceImage(String path) {
        if (log.isDebugEnabled()) log.debug("loading image file url={}", path);
        URL resource = getClass().getResource(path);
        if (resource == null) {
            log.error("Image resource '{}' not found, default icon will be used", path);
            resource = getClass().getResource(GuiController.IMAGE_WEATHER_DEFAULT_ICON_PNG);
        }
        return new Image(String.valueOf(resource));
    }

    private BufferedImage readImage(String urlString) {
        try {
            return ImageIO.read(new URL(urlString));
        } catch (IOException e) {
            log.error("Error getting image from '{}'", urlString, e);
            return null;
        }
    }
}
