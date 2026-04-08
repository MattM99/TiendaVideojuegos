package ProyectoFinalTienda.TiendaVideojuegos.auth;

import ProyectoFinalTienda.TiendaVideojuegos.Jwt.JwtService;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PersonaRepository PersonaRepository;
    private final CuentaRepository CuentaRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (BadCredentialsException e){
            throw new RuntimeException("Usuario o contraseña incorrectos, so sad");
        }
        UserDetails user = CuentaRepository.findByNickname(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }
@Transactional
    public AuthResponse register(RegisterRequest request) {
        try{
            PersonaEntity persona = PersonaEntity.builder()
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .dni(request.getDni())
                    .telefono(request.getTelefono())
                    .email(request.getEmail())
                    .build();

            CuentaEntity cuenta = CuentaEntity.builder()
                    .nickname(request.getNickname())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .rol(Roles.valueOf(request.getRol().toUpperCase()))
                    .estado(Estado.ACTIVO)
                    .persona(persona)
                    .build();

             PersonaRepository.save(persona);
                CuentaRepository.save(cuenta);

        }catch (IllegalArgumentException e){
            throw new RuntimeException("Fijate que onda");

        }

        return AuthResponse.builder()
                .token("Cuenta registrada exitosamente. Por favor, inicia sesión para obtener tu token.")
                .build();
    }
}

