package ProyectoFinalTienda.TiendaVideojuegos.auth;

import ProyectoFinalTienda.TiendaVideojuegos.Jwt.JwtService;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.RolInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private PersonaRepository personaRepository;
    @Mock private CuentaRepository cuentaRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks private AuthService authService;

    private LoginRequest pedidoDeLogin(String usuario, String clave) {
        return LoginRequest.builder().username(usuario).password(clave).build();
    }

    private RegisterRequest pedidoDeRegistro(String rol) {
        return RegisterRequest.builder()
                .nickname("empleado01").password("clave123")
                .nombre("Ana").apellido("Lopez")
                .dni("30111222").email("ana@test.com").telefono("1155550000")
                .rol(rol).build();
    }

    // ---------- login ----------

    @Test
    void loginCorrectoDevuelveElToken() {
        UserDetails usuario = User.withUsername("juan").password("hash").roles("EMPLEADO").build();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.getToken(usuario)).thenReturn("token-de-prueba");

        AuthResponse respuesta = authService.login(pedidoDeLogin("juan", "clave123"));

        assertEquals("token-de-prueba", respuesta.getToken());
    }

    @Test
    void loginConContrasenaIncorrectaLanzaBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(pedidoDeLogin("juan", "mala")));
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginConCuentaDeBajaLanzaDisabled() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("User is disabled"));

        assertThrows(DisabledException.class,
                () -> authService.login(pedidoDeLogin("juan", "clave123")));
        verifyNoInteractions(jwtService);
    }

    // ---------- register ----------

    @Test
    void registroCorrectoGuardaLaCuentaConLaClaveEncriptada() {
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.empty());
        when(personaRepository.getPersonaByEmail("ana@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("clave123")).thenReturn("hash-de-prueba");

        authService.register(pedidoDeRegistro("empleado"));

        ArgumentCaptor<CuentaEntity> captor = ArgumentCaptor.forClass(CuentaEntity.class);
        verify(cuentaRepository).save(captor.capture());
        assertEquals("empleado01", captor.getValue().getNickname());
        assertEquals(Roles.EMPLEADO, captor.getValue().getRol());
        assertEquals("hash-de-prueba", captor.getValue().getPassword());
    }

    @Test
    void registroConRolInvalidoLanzaExcepcion() {
        assertThrows(RolInvalidoException.class,
                () -> authService.register(pedidoDeRegistro("SUPERUSER")));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void registroSinRolLanzaExcepcion() {
        assertThrows(RolInvalidoException.class,
                () -> authService.register(pedidoDeRegistro(null)));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void registroConNicknameRepetidoLanzaExcepcion() {
        when(cuentaRepository.existsByNickname("empleado01")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> authService.register(pedidoDeRegistro("EMPLEADO")));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void registroDePersonaQueYaTieneCuentaLanzaExcepcion() {
        PersonaEntity persona = mock(PersonaEntity.class);
        when(persona.getCuenta()).thenReturn(new CuentaEntity());
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.of(persona));

        assertThrows(BusinessException.class,
                () -> authService.register(pedidoDeRegistro("EMPLEADO")));
        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void registroConEmailYaRegistradoLanzaExcepcion() {
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.empty());
        when(personaRepository.getPersonaByEmail("ana@test.com"))
                .thenReturn(Optional.of(mock(PersonaEntity.class)));

        assertThrows(BusinessException.class,
                () -> authService.register(pedidoDeRegistro("EMPLEADO")));
        verify(cuentaRepository, never()).save(any());
    }
}