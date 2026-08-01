package beleg.rouletteservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
// kümmert sich um die configuration des Http Client / RestClient wäre als fluent API Alternative denkbar
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}