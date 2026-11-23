package ProyectoFinalTienda.TiendaVideojuegos.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void credencialesInvalidasDevuelven401() {
        ResponseEntity<String> r = handler.manejarAutenticacion(new BadCredentialsException("x"));

        assertEquals(401, r.getStatusCode().value());
        assertEquals("Usuario o contraseña incorrectos", r.getBody());
    }

    @Test
    void cuentaDeBajaDevuelve401() {
        ResponseEntity<String> r = handler.manejarCuentaDeshabilitada(new DisabledException("x"));

        assertEquals(401, r.getStatusCode().value());
        assertEquals("La cuenta está dada de baja", r.getBody());
    }
}