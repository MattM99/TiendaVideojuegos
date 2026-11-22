package ProyectoFinalTienda.TiendaVideojuegos.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Ruta base para autenticación
@RequiredArgsConstructor
public class  AuthController {

    private final AuthService authService;

    @PostMapping("/login") // Ruta especifica para iniciar sesión
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/register")  // Ruta especifica para registrar un nuevo usuario
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request,
                                                 Authentication authentication) {
        boolean esFounder = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FOUNDER"));
        boolean pideFounder = request.getRol() != null
                && request.getRol().trim().equalsIgnoreCase("FOUNDER");

        if (pideFounder && !esFounder) {
            throw new AccessDeniedException("Solo un FOUNDER puede crear cuentas FOUNDER.");
        }

        authService.register(request);
        return ResponseEntity.ok().build();
    }


}
