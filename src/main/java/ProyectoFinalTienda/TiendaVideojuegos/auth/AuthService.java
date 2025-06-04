package ProyectoFinalTienda.TiendaVideojuegos.auth;

import ProyectoFinalTienda.TiendaVideojuegos.Jwt.JwtService;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public AuthResponse login(LoginRequest request) {

        return null;
    }
    private final PersonaRepository PersonaRepository;
    private final CuentaRepository CuentaRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) { //A modo de prueba, posiblemente este metodo no se use
        PersonaEntity persona = PersonaEntity.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .build();

        PersonaRepository.save(persona);

        CuentaEntity cuenta = CuentaEntity.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Roles.EMPLEADO)
                .estado(Estado.ACTIVO)
                .persona(persona)
                .build();

        CuentaRepository.save(cuenta);

        return AuthResponse.builder()
                .token(jwtService.getToken(cuenta))
                .build();
    }
}

