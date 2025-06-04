package ProyectoFinalTienda.TiendaVideojuegos.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter permite hacer filtros personalizados que se aplican una vez por solicitud
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String token = getTokenFromRequest(request);
/*
        if (token==null){
            filterChain.doFilter(request, response);
            return; // Si no hay token, continúa con la cadena de filtros sin hacer nada más (Porque si no hay token, es que no se ha iniciado sesión)
        }
*/
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION); // Obtiene el encabezado de autorización de la solicitud HTTP

        if(StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extrae el token sin el prefijo "Bearer "
    }
        return null; // Si no hay token, retorna null
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/"); // No aplicar el filtro a rutas públicas
    }
}
