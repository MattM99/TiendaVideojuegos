package ProyectoFinalTienda.TiendaVideojuegos.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Ruta base para autenticación

public class Autenticacion {

    @PostMapping("/login") // Ruta especifica para iniciar sesión
    public String login() {
        // Aquí iría la lógica de autenticación
        return "Login correcto";
    }

    @PostMapping("/register")  // Ruta especifica para registrar un nuevo usuario
    public String register() {
        // Aquí iría la lógica de registro de usuario
        return "Registracion exitosa";
    }
}
