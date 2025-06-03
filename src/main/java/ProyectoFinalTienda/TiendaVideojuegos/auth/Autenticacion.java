package ProyectoFinalTienda.TiendaVideojuegos.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Ruta base para autenticación
@RequiredArgsConstructor
public class Autenticacion {

    private final AuthService authService;

    @PostMapping("/login") // Ruta especifica para iniciar sesión
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Aquí iría la lógica de autenticación
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")  // Ruta especifica para registrar un nuevo usuario
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }
}
