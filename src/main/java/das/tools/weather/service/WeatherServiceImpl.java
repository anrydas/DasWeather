package das.tools.weather.service;

import das.tools.weather.entity.CurrenWeatherResponse;
import das.tools.weather.entity.ForecastWeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    @Value("${app.api-key}")
    String apiKey;
    @Value("${app.weather.forecast.url}")
    String baseUrl;
    @Value("${app.weather.location}")
    String location;

    private final RestTemplate restTemplate;

    public WeatherServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public CurrenWeatherResponse getCurrentWeather() {
        return null;
    }

    @Override
    public ForecastWeatherResponse getForecastWeather() {
        ForecastWeatherResponse response = null;

        String url = ServletUriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("key", apiKey)
                .queryParam("q", location)
                .queryParam("aqi", "yes")
                .queryParam("lang", "uk")
                .queryParam("days", "3")
                .toUriString();
        if(log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: got url={}", url);

        try {
            response = restTemplate.getForObject(url, ForecastWeatherResponse.class);
            if(log.isDebugEnabled()) log.debug("[WeatherService].getForecastWeather: response={}", response);
            log.info("Weather was updated");
        } catch (HttpClientErrorException e) {
            log.error("Couldn't get response from server: ", e);
        }

        return response;
    }

    @Override
    public Image getRemoteImage(String imageUrl) {
        String url = "https:" + imageUrl;
        Image image = null;
        try {
            image = ImageIO.read(new URL(url));
        } catch (IOException e) {
            log.error("Couldn't get image from '{}'", url);
        }
        String msg = image != null ? "[WeatherService].getRemoteImage: got weather image from {}" : "[WeatherService].getRemoteImage: Couldn't get weather image from {}" ;
        if(log.isDebugEnabled()) log.debug(msg, url);
        return image;
    }
}
