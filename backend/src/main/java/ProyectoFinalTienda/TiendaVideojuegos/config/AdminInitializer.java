package ProyectoFinalTienda.TiendaVideojuegos.config;

import ProyectoFinalTienda.TiendaVideojuegos.auth.AuthService;
import ProyectoFinalTienda.TiendaVideojuegos.auth.RegisterRequest;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(AuthService authService, CuentaRepository cuentaRepository) {
        return args -> {
            if (cuentaRepository.findByNickname("admin").isEmpty()) {
                RegisterRequest adminRequest = RegisterRequest.builder()
                        .nickname("admin")
                        .password("admin123")
                        .nombre("Admin")
                        .apellido("Root")
                        .dni("0000000")
                        .email("admin@admin.com")
                        .telefono("000000000")
                        .rol("ADMINISTRADOR")
                        .build();

                authService.register(adminRequest);
                System.out.println(">>> Usuario admin creado. Nickname: " + adminRequest.getNickname() + ", Rol: " + adminRequest.getRol());
            }
        };
    }
}
