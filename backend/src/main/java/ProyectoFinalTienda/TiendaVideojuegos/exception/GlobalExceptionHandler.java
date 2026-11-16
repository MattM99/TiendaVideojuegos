package ProyectoFinalTienda.TiendaVideojuegos.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            int status,
            String error,
            Object mensaje
    ) {}

    // ===============================
    // VALIDACIONES (400)
    // ===============================


    // Errores de validación con @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(MethodArgumentNotValidException ex) {

        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(HttpStatus.BAD_REQUEST, errores);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> manejarConstraintViolation(ConstraintViolationException ex) {

        List<String> errores = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Error en el formato de los datos enviados. Por favor, verifique fechas y tipos."
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleEstadoInvalido(EstadoInvalidoException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(RolInvalidoException.class)
    public ResponseEntity<ErrorResponse> manejarRolInvalido(RolInvalidoException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(StockNoValidoException.class)
    public ResponseEntity<ErrorResponse> manejarStockNoValido(StockNoValidoException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> manejarBusinessException(BusinessException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // ===============================
    // NOT FOUND (404)
    // ===============================


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarNotFound(NotFoundException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler({
            UsuarioNoEncontradoException.class,
            VideojuegoNoEncontradoException.class,
            InventarioItemNoEncontradoException.class,
            AlquilerNoEncontradoException.class,
            PersonaNoEncontradaException.class,
            DetalleAlquilerNoEncontradoException.class,
            BloqueoNoEncontradoException.class
    })
    public ResponseEntity<ErrorResponse> manejarEntidadesNoEncontradas(Exception ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> manejarElementoNoEncontrado(NoSuchElementException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }


    // ===============================
    // CONFLICTOS (409)
    // ===============================

    //  Cuando se intenta insertar un dato que viola una restricción
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarViolacionIntegridad(DataIntegrityViolationException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Violación de integridad de datos: probablemente un campo único ya existe."
        );
    }

    // ===============================
    // FORBIDDEN (403)
    // ===============================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> manejarAccesoDenegado(AccessDeniedException ex) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "No tenés permisos para realizar esta operación."
        );
    }



    // ===============================
    // GENERAL (500)
    // ===============================

    //  Manejador general para cualquier excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarExcepcionGeneral(Exception ex) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor."
        );
    }

    // ===============================
    // HELPER
    // ===============================

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Object mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje));
    }

}
