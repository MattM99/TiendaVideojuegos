package ProyectoFinalTienda.TiendaVideojuegos.Jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    // Claves de prueba (base64). No son las de la aplicación.
    private static final String CLAVE_DE_PRUEBA =
            "Y2xhdmUtZGUtcHJ1ZWJhLXNvbG8tcGFyYS10ZXN0cy0xMjM0NTY3OA==";
    private static final String OTRA_CLAVE =
            "b3RyYS1jbGF2ZS1kaXN0aW50YS1wYXJhLXNpbXVsYXItZm9yamFkby0xMjM=";

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filtro;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain cadena;

    @BeforeEach
    void preparar() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", CLAVE_DE_PRUEBA);

        userDetailsService = mock(UserDetailsService.class);
        filtro = new JwtAuthenticationFilter(jwtService, userDetailsService);

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/alquileres/listar");
        response = new MockHttpServletResponse();
        cadena = new MockFilterChain();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private UserDetails usuario(boolean habilitado) {
        return User.withUsername("juan").password("hash").roles("EMPLEADO")
                .disabled(!habilitado).build();
    }

    private String tokenConClave(String claveBase64, long vencimientoEnMs) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(claveBase64));
        return Jwts.builder()
                .setSubject("juan")
                .setIssuedAt(new Date(System.currentTimeMillis() - 60_000))
                .setExpiration(new Date(System.currentTimeMillis() + vencimientoEnMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private void conToken(String token) {
        request.addHeader("Authorization", "Bearer " + token);
    }

    @Test
    void tokenValidoAutenticaAlUsuario() throws Exception {
        UserDetails usuario = usuario(true);
        when(userDetailsService.loadUserByUsername("juan")).thenReturn(usuario);
        conToken(jwtService.getToken(usuario));

        filtro.doFilter(request, response, cadena);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("juan", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO")));
        assertNotNull(cadena.getRequest()); // la cadena continuó
    }

    @Test
    void tokenVencidoNoAutenticaYNoLanzaExcepcion() {
        conToken(tokenConClave(CLAVE_DE_PRUEBA, -1_000));

        assertDoesNotThrow(() -> filtro.doFilter(request, response, cadena));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void tokenMalFormadoNoAutenticaYNoLanzaExcepcion() {
        conToken("esto-no-es-un-jwt");

        assertDoesNotThrow(() -> filtro.doFilter(request, response, cadena));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void tokenFirmadoConOtraClaveEsRechazado() {
        conToken(tokenConClave(OTRA_CLAVE, 60_000));

        assertDoesNotThrow(() -> filtro.doFilter(request, response, cadena));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void cuentaDadaDeBajaNoSeAutenticaAunConTokenValido() throws Exception {
        UserDetails dadoDeBaja = usuario(false);
        when(userDetailsService.loadUserByUsername("juan")).thenReturn(dadoDeBaja);
        conToken(jwtService.getToken(dadoDeBaja));

        filtro.doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
    }

    @Test
    void sinHeaderNoAutenticaYContinuaLaCadena() throws Exception {
        filtro.doFilter(request, response, cadena);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(cadena.getRequest());
        verifyNoInteractions(userDetailsService);
    }
}