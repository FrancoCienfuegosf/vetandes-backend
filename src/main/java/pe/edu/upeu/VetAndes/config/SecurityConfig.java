package pe.edu.upeu.VetAndes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import pe.edu.upeu.VetAndes.security.RestAccessDeniedHandler;
import pe.edu.upeu.VetAndes.security.RestAuthenticationEntryPoint;

import static pe.edu.upeu.VetAndes.security.Permiso.*;

/*
 * Cadena de seguridad de la API.
 *
 * - STATELESS: no hay sesion en el servidor; cada peticion trae su JWT.
 * - CSRF desactivado: no hay cookies de sesion; el token viaja en un
 *   encabezado que el navegador no envia solo.
 * - Permisos centralizados con hasAuthority(PERMISO): ningun controlador
 *   compara nombres de rol. El orden importa: gana la primera regla que
 *   coincide.
 * - @EnableMethodSecurity habilita @PreAuthorize (ReporteController).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] RUTAS_PUBLICAS = {
            "/api/v1/auth/login",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtService jwtService,
                                           UserDetailsService userDetailsService,
                                           CorsConfigurationSource corsConfigurationSource,
                                           RestAuthenticationEntryPoint authenticationEntryPoint,
                                           RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publicas: login, Swagger y preflight CORS
                        .requestMatchers(RUTAS_PUBLICAS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Propietarios (solo lectura)
                        .requestMatchers(HttpMethod.GET, "/api/v1/propietarios/**")
                        .hasAuthority(MASCOTA_LEER.name())

                        // Prestaciones: GET lee; POST, PUT, DELETE y PATCH escriben
                        .requestMatchers(HttpMethod.GET, "/api/v1/prestaciones/**")
                        .hasAuthority(PRESTACION_LEER.name())
                        .requestMatchers("/api/v1/prestaciones/**")
                        .hasAuthority(PRESTACION_ESCRIBIR.name())

                        // Mascotas
                        .requestMatchers(HttpMethod.GET, "/api/v1/mascotas/**")
                        .hasAuthority(MASCOTA_LEER.name())
                        .requestMatchers("/api/v1/mascotas/**")
                        .hasAuthority(MASCOTA_ESCRIBIR.name())

                        // Atenciones
                        .requestMatchers(HttpMethod.POST, "/api/v1/atenciones")
                        .hasAuthority(ATENCION_REGISTRAR.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/atenciones/*/anular")
                        .hasAuthority(ATENCION_ANULAR.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/atenciones/**")
                        .hasAuthority(ATENCION_LEER.name())

                        // Reportes (ademas @PreAuthorize en ReporteController)
                        .requestMatchers("/api/v1/reportes/**")
                        .hasAuthority(REPORTE_VER.name())

                        // Todo lo demas (por ejemplo /auth/me) exige token valido
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint)  // 401
                        .accessDeniedHandler(accessDeniedHandler))           // 403
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * BCrypt: hash lento y con sal aleatoria. La misma contrasena produce
     * un hash distinto cada vez y no se puede revertir.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
