package ProyectoFinalTienda.TiendaVideojuegos.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Aquí iría la lógica de autenticación JWT
        // Por ejemplo, verificar el token JWT en los encabezados de la solicitud


        final String token = getTokenFromRequest(request);

        if (token==null){
            filterChain.doFilter(request, response);
            return;
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);

    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return Optional.of(authorizationHeader.substring(7)); // Extrae el token sin el prefijo "Bearer "
    }
        return Optional.empty(); // Si no hay token, retorna null
    }
}
