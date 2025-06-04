package ProyectoFinalTienda.TiendaVideojuegos.config;

import ProyectoFinalTienda.TiendaVideojuegos.Jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class    SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF para simplificar la configuración (no lo vamos a usar ahora)
                .authorizeHttpRequests(authRequest -> authRequest
                        .requestMatchers("/api/auth/**").permitAll() // Se permite acceso a la autenticación (es de acceso público)
                        .anyRequest().authenticated() // El resto de las peticiones requieren autenticación
                )
        .sessionManagement(sessionManager ->
                sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la política de sesión como sin estado (stateless)
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Añade el filtro JWT antes del filtro de autenticación por nombre de usuario y contraseña
        .build();

    }

}
