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
    public ResponseEntity<String> manejarValidaciones(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> manejarConstraintViolation(ConstraintViolationException ex) {

        List<String> errores = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> manejarJsonInvalido(HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest()
                .body("Error en el formato de los datos enviados. Por favor, verifique fechas y tipos.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EstadoInvalidoException.class)
    public ResponseEntity<String> handleEstadoInvalido(EstadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RolInvalidoException.class)
    public ResponseEntity<String> manejarRolInvalido(RolInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(StockNoValidoException.class)
    public ResponseEntity<String> manejarStockNoValido(StockNoValidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> manejarBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // ===============================
    // NOT FOUND (404)
    // ===============================


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> manejarNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
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
    public ResponseEntity<String> manejarElementoNoEncontrado(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    // ===============================
    // CONFLICTOS (409)
    // ===============================

    //  Cuando se intenta insertar un dato que viola una restricción
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        String mensaje = "Violación de integridad de datos: probablemente un campo único ya existe.";
        return new ResponseEntity<>(mensaje, HttpStatus.CONFLICT); // 409 Conflict
    }

    // ===============================
    // FORBIDDEN (403)
    // ===============================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }



    // ===============================
    // GENERAL (500)
    // ===============================

    //  Manejador general para cualquier excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGeneral(Exception ex) {
        String mensaje = "Error interno del servidor: " + ex.getMessage();
        return new ResponseEntity<>(mensaje, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    // ===============================
    // HELPER
    // ===============================

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, Object mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje));
    }

}
