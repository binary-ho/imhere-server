package gdsc.binaryho.imhere.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${origin.domain}")
    private String productionOrigin;
    @Value("${origin.test}")
    private String testOrigin;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = getCorsConfiguration();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration =  new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(productionOrigin, testOrigin));
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS"));

        return configuration;
    }
}
