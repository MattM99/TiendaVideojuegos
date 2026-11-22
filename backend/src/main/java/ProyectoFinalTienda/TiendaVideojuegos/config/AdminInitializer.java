package ProyectoFinalTienda.TiendaVideojuegos.config;

import ProyectoFinalTienda.TiendaVideojuegos.auth.AuthService;
import ProyectoFinalTienda.TiendaVideojuegos.auth.RegisterRequest;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {
    @Value("${founder.password}") String founderPassword;

    @Bean
    public CommandLineRunner initAdmin(AuthService authService, CuentaRepository cuentaRepository) {
        return args -> {
            if (cuentaRepository.findByNickname("FOUNDER").isEmpty()) {
                RegisterRequest founderRequest = RegisterRequest.builder()
                        .nickname("FOUNDER")
                        .password(founderPassword)
                        .nombre("FOUNDER")
                        .apellido("FOUNDER")
                        .dni("0000000")
                        .email("admin@admin.com")
                        .telefono("000000000")
                        .rol("FOUNDER")
                        .build();

                authService.register(founderRequest);
                System.out.println(">>> Usuario founder creado.");
            }
        };
    }
}
