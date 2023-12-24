package das.tools.weather.service;

import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;
    private final GuiConfigService guiConfig;

    public WeatherServiceImpl(RestTemplate restTemplate, GuiConfigService guiConfig) {
        this.restTemplate = restTemplate;
        this.guiConfig = guiConfig;
    }

    @Override
    public CurrenWeatherResponse getCurrentWeather() {
        return null;
    }

    @Override
    public ForecastWeatherResponse getForecastWeather() {
        Properties props = guiConfig.getCurrentConfig();
        ForecastWeatherResponse response = null;
        String url = ServletUriComponentsBuilder.fromHttpUrl(props.getProperty("app.weather.forecast.url", ""))
                .queryParam("key", props.getProperty("app.api-key", ""))
                .queryParam("q", props.getProperty("app.weather.location", "Kyiv"))
                .queryParam("aqi", "yes")
                //.queryParam("lang", "uk")
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

    private ForecastWeatherResponse getResponseAsync(String url) {
        CompletableFuture<ForecastWeatherResponse> completableFuture =
                CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, ForecastWeatherResponse.class));
        ForecastWeatherResponse res = null;
        try {
            res = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error getting response from server: ", e);
            throw new RuntimeException(e);
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
