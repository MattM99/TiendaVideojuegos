package ProyectoFinalTienda.TiendaVideojuegos.services;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class CuentaService {
    private final PasswordEncoder passwordEncoder;

    public CuentaService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public UserDetailsService crearCuentas() {
        // Crear una cuenta de administrador
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("12345"))
                .roles("ADMINISTRADOR")
                .build();

        // Crear una cuenta de vendedor
    UserDetails vendedor1 = User.withUsername("vendedor1")
            .password(passwordEncoder.encode("12345"))
            .roles("EMPLEADO")
            .build();

    return new InMemoryUserDetailsManager(admin,vendedor1);

}
}
