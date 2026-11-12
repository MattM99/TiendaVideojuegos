package ProyectoFinalTienda.TiendaVideojuegos.config;

import ProyectoFinalTienda.TiendaVideojuegos.Jwt.JwtAuthenticationFilter;
import ProyectoFinalTienda.TiendaVideojuegos.auth.CustomAccessDeniedHandler;
import ProyectoFinalTienda.TiendaVideojuegos.auth.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF para simplificar la configuración (no lo vamos a usar ahora)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/cuenta/yo").authenticated()
                        .requestMatchers("/api/cuenta/mi-cuenta/**").authenticated()
                        .requestMatchers("/api/cuenta/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/reportes/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
        .sessionManagement(sessionManager ->
                sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la política de sesión como sin estado (stateless)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Añade el filtro JWT antes del filtro de autenticación por nombre de usuario y contraseña
        .build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
