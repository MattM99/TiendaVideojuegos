package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.CuentaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.RolInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PersonaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CuentaService {
    private final PasswordEncoder passwordEncoder;
    private final CuentaRepository cuentaRepository;
    private final PersonaMapper personaMapper;

    public CuentaService(PasswordEncoder passwordEncoder, CuentaRepository cuentaRepository, PersonaMapper personaMapper) {
        this.passwordEncoder = passwordEncoder;
        this.cuentaRepository = cuentaRepository;
        this.personaMapper = personaMapper;
    }


    //No hay metodo de crear cuenta, el metodo de registro está en AuthService


    public Page<CuentaResponse> listarTodos(Pageable paginacion) {
        return cuentaRepository.findAll(paginacion)
                .map(this::toCuentaResponse);
    }

    public CuentaEntity buscarPorNickname(String nickname) throws UsuarioNoEncontradoException {
        Optional<CuentaEntity> cuenta = cuentaRepository.findByNickname(nickname);
        if (cuenta.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se ha encontrado al usuario con el nombre: " + nickname);
        }
        return cuenta.get();
    }

    public Page<CuentaResponse> buscarPorRol(String stringRol, Pageable paginacion) throws NoSuchElementException, UsuarioNoEncontradoException {
        if (!esRolValido(stringRol)) {
            throw new NoSuchElementException("No existe el rol: " + stringRol);
        }

        Roles rol = Roles.valueOf(stringRol.toUpperCase());

        Page<CuentaEntity> cuentas = cuentaRepository.findByRol(rol, paginacion);
        if (cuentas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el rol: " + rol);
        }
        return cuentas
                .map(this::toCuentaResponse);
    }

    public boolean esRolValido(String rolStr) {
        try {
            Roles.valueOf(rolStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Page<CuentaResponse> buscarPorEstado(String stringEstado, Pageable paginacion) throws NoSuchElementException, UsuarioNoEncontradoException {
        if (!esEstadoValido(stringEstado)) {
            throw new NoSuchElementException("No existe el estado: " + stringEstado);
        }

        Estado estado = Estado.valueOf(stringEstado.toUpperCase());

        Page<CuentaEntity> cuentas = cuentaRepository.findByEstado(estado, paginacion);

        if (cuentas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el estado: " + estado);
        }
        return cuentas
                .map(this::toCuentaResponse);
    }

    public boolean esEstadoValido(String stringEstado) {
        try {
            Estado.valueOf(stringEstado.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public CuentaEntity cambiarContrasena(String nickname, String password) throws UsuarioNoEncontradoException {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser nula o vacía");
        }

        CuentaEntity cuenta = buscarPorNickname(nickname);
        validarPermisoSobre(cuenta);

        boolean esPropiaCuenta = nickname.equalsIgnoreCase(usuarioActivo());
        if (cuenta.getRol() == Roles.FOUNDER && !esPropiaCuenta) {
            throw new AccessDeniedException("Solo el FOUNDER puede cambiar su propia contraseña.");
        }

        cuenta.setPassword(passwordEncoder.encode(password));
        return cuentaRepository.save(cuenta);
    }

    public CuentaEntity cambiarRol(String nickname, String rol) throws UsuarioNoEncontradoException, RolInvalidoException {
        if (nickname.equalsIgnoreCase(usuarioActivo())) {
            throw new IllegalArgumentException("No es posible modificar el rol propio.");
        }

        if (!esRolValido(rol)) {
            throw new RolInvalidoException("El rol proporcionado no es válido: " + rol);
        }

        Roles nuevoRol = Roles.valueOf(rol.trim().toUpperCase());

        if (nuevoRol == Roles.FOUNDER) {
            throw new BusinessException("El rol FOUNDER no se puede asignar: lo crea el sistema al iniciar.");
        }

        CuentaEntity cuenta = buscarPorNickname(nickname);
        validarPermisoSobre(cuenta);

        if (cuenta.getRol() == Roles.FOUNDER) {
            throw new AccessDeniedException("No se permite modificar el rol de una cuenta FOUNDER.");
        }

        cuenta.setRol(nuevoRol);
        return cuentaRepository.save(cuenta);
    }


    public CuentaEntity darDeBajaCuenta(String nickname) throws UsuarioNoEncontradoException {
        if (nickname.equalsIgnoreCase(usuarioActivo())) {
            throw new IllegalArgumentException("No es posible dar de baja la propia cuenta.");
        }

        CuentaEntity cuenta = buscarPorNickname(nickname);
        validarPermisoSobre(cuenta);

        if (cuenta.getRol() == Roles.FOUNDER) {
            throw new AccessDeniedException("No se puede dar de baja la cuenta FOUNDER.");
        }

        cuenta.setEstado(Estado.BAJA);
        return cuentaRepository.save(cuenta);
    }

    public CuentaEntity restablecerCuenta(String nickname) throws UsuarioNoEncontradoException {
        CuentaEntity cuenta = buscarPorNickname(nickname);
        validarPermisoSobre(cuenta);
        cuenta.setEstado(Estado.ACTIVO);
        return cuentaRepository.save(cuenta);
    }

    public CuentaResponse toCuentaResponse(CuentaEntity cuenta) {

        return new CuentaResponse(
                cuenta.getNickname(),
                cuenta.getRol().name(),
                cuenta.getEstado().name(),
                cuenta.getPersona() != null ? personaMapper.convertirEntidadADTO(cuenta.getPersona()) : null

        );
    }

    private String usuarioActivo() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void validarPermisoSobre(CuentaEntity objetivo) {
        boolean objetivoEsAdmin = objetivo.getRol() == Roles.ADMINISTRADOR;
        boolean esPropiaCuenta = objetivo.getNickname().equalsIgnoreCase(usuarioActivo());

        if (objetivoEsAdmin && !esPropiaCuenta && !usuarioActivoEsFounder()) {
            throw new AccessDeniedException("Un ADMINISTRADOR no puede modificar a otro ADMINISTRADOR.");
        }
    }

    private boolean usuarioActivoEsFounder() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FOUNDER"));
    }

}
