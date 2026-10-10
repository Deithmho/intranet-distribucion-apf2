package com.dwi.intranetdistribucion.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration
public class CorsConfig {
    @Bean public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origins}") String origins) {
        var config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
