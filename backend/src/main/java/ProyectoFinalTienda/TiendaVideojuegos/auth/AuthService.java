package ProyectoFinalTienda.TiendaVideojuegos.auth;

import ProyectoFinalTienda.TiendaVideojuegos.Jwt.JwtService;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.RolInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PersonaRepository personaRepository;
    private final CuentaRepository cuentaRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserDetails user = (UserDetails) auth.getPrincipal();

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        Roles rol = parsearRol(request.getRol());

        if (cuentaRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException("El nombre de usuario '" + request.getNickname() + "' ya está en uso.");
        }

        PersonaEntity persona = personaRepository.findByDni(request.getDni()).orElse(null);

        if (persona != null) {
            // La persona ya existe (por ejemplo, un cliente que pasa a ser empleado): se reutiliza.
            if (persona.getCuenta() != null) {
                throw new BusinessException("La persona con DNI " + request.getDni() + " ya tiene una cuenta.");
            }
        } else {
            if (personaRepository.getPersonaByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("El email " + request.getEmail() + " ya está registrado.");
            }
            persona = PersonaEntity.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .dni(request.getDni())
                    .email(request.getEmail())
                    .telefono(request.getTelefono())
                    .build();
        }

        CuentaEntity cuenta = CuentaEntity.builder()
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .estado(Estado.ACTIVO)
                .persona(persona)
                .build();

        personaRepository.save(persona);
        cuentaRepository.save(cuenta);
    }

    private Roles parsearRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new RolInvalidoException("El rol es obligatorio.");
        }
        try {
            return Roles.valueOf(rol.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RolInvalidoException("El rol proporcionado no es válido: " + rol);
        }
    }
}