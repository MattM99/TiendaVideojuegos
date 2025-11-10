package ProyectoFinalTienda.TiendaVideojuegos.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //OncePerRequestFilter permite hacer filtros personalizados que se aplican una vez por solicitud

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String token = getTokenFromRequest(request);
        final String username;

        if(token == null) {
            filterChain.doFilter(request, response); // Si no hay token, continúa con el filtro
            return;
        }
        username = jwtService.getUsernameFromToken(token); // Obtiene el nombre de usuario del token
        System.out.println("JWT contiene nickname: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Carga los detalles del usuario
            if (jwtService.isTokenValid(token, userDetails)) { // Verifica si el token es válido
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // Crea un objeto de autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Establece los detalles de autenticación
                SecurityContextHolder.getContext().setAuthentication(authToken); // Establece la autenticación en el contexto de seguridad
            }
        }


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
        return path.startsWith("/api/auth/login"); // No aplicar el filtro a rutas públicas
    }
}
