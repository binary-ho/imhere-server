package gdsc.binaryho.imhere.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = getCorsConfiguration();
        source.registerCorsConfiguration("/api/**", configuration);
        return new CorsFilter(source);
    }

    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration =  new CorsConfiguration();
        configuration.setAllowCredentials(true);
        /* TODO: 도메인 생기면 업데이트 */
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("Authorization");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT"));

        return configuration;
    }
}
