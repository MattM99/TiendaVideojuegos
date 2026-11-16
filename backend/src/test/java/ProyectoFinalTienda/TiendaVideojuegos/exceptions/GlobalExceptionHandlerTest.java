package ProyectoFinalTienda.TiendaVideojuegos.exceptions;

import ProyectoFinalTienda.TiendaVideojuegos.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void error500NoDebeExponerDetallesInternos() {

        // Simulamos una excepción con información sensible
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Exception exception = new RuntimeException(
                "Error SQL: contraseña interna de prueba"
        );

        // Ejecutamos el manejador del error 500
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.manejarExcepcionGeneral(exception);

        // Verificamos el código HTTP
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verificamos que exista una respuesta
        assertNotNull(response.getBody());

        // Verificamos su contenido
        assertEquals(500, response.getBody().status());

        assertEquals(
                "Ocurrió un error interno en el servidor.",
                response.getBody().mensaje()
        );

        // Comprobamos que no se exponga información interna
        assertFalse(
                response.getBody().mensaje().toString().contains("contraseña interna")
        );
    }
}

