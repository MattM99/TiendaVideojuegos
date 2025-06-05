package ProyectoFinalTienda.TiendaVideojuegos.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación con @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarValidaciones(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    // Cuando no se encuentra un elemento
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> manejarElementoNoEncontrado(NoSuchElementException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //  Cuando se intenta insertar un dato que viola una restricción
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> manejarViolacionIntegridad(DataIntegrityViolationException ex) {
        String mensaje = "Violación de integridad de datos: probablemente un campo único ya existe.";
        return new ResponseEntity<>(mensaje, HttpStatus.CONFLICT); // 409 Conflict
    }

    //  Manejador general para cualquier excepción no controlada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGeneral(Exception ex) {
        String mensaje = "Error interno del servidor: " + ex.getMessage();
        return new ResponseEntity<>(mensaje, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<String> manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(VideojuegoNoEncontradoException.class)
    public ResponseEntity<String> manejarVideojuegoNoEncontrado(VideojuegoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InventarioNoEncontradoException.class)
    public ResponseEntity<String> manejarInventarioNoEncontrado(InventarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AlquilerNoEncontradoException.class)
    public ResponseEntity<String> manejarAlquilerNoEncontrado(AlquilerNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> manejarBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
