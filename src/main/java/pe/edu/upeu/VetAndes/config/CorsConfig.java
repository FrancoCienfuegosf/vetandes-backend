package pe.edu.upeu.VetAndes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
 * CORS para la SPA (http://localhost:4200).
 *
 * Con Spring Security activo, CORS se aplica en la cadena de seguridad
 * (SecurityConfig usa este bean): asi el preflight OPTIONS del navegador
 * se responde antes de exigir el token.
 */
@Configuration
public class CorsConfig {

    private final List<String> origenesPermitidos;

    public CorsConfig(@Value("${app.cors.allowed-origins:http://localhost:4200}") String[] origenes) {
        this.origenesPermitidos = List.of(origenes);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenesPermitidos);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Location"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
