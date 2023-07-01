package das.tools.weather;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DasWeatherApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DasWeatherApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



}
