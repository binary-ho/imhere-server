package gdsc.binaryho.imhere.security.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${origin.domain}")
    private String origin;

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
        configuration.addAllowedOrigin(origin);
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(getHttpMethods());
        return configuration;
    }

    private List<String> getHttpMethods() {
        return List.of(
            HttpMethod.GET.name(), HttpMethod.POST.name(),
            HttpMethod.PUT.name(), HttpMethod.OPTIONS.name()
        );
    }
}
