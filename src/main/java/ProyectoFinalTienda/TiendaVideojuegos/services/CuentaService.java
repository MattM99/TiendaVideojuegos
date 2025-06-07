package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.CuentaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.RolInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CuentaService {
    private final PasswordEncoder passwordEncoder;
    private final CuentaRepository cuentaRepository;

    public CuentaService(PasswordEncoder passwordEncoder, CuentaRepository cuentaRepository) {
        this.passwordEncoder = passwordEncoder;
        this.cuentaRepository = cuentaRepository;
    }

    //No hay metodo de crear cuenta, el metodo de registro esta en AuthService

    public CuentaEntity buscarPorNickname(String nickname) throws UsuarioNoEncontradoException {
        Optional<CuentaEntity> cuenta = cuentaRepository.findByNickname(nickname);
        if (cuenta.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se ha encontrado al usuario con el nombre: " + nickname);
        }
        return cuenta.get();
    }

    public List<CuentaEntity> buscarPorRol(String stringRol) throws NoSuchElementException, UsuarioNoEncontradoException {
        if (!esRolValido(stringRol)){
            throw new NoSuchElementException("No existe el rol: " + stringRol);
        }

        Roles rol = Roles.valueOf(stringRol.toUpperCase());

        List<CuentaEntity> cuentas = cuentaRepository.findByRol(rol);

        if (cuentas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el rol: " + rol);
        }
        return cuentas;
    }

    public boolean esRolValido(String rolStr) {
        try {
            Roles.valueOf(rolStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<CuentaEntity> buscarPorEstado(String stringEstado) throws NoSuchElementException, UsuarioNoEncontradoException {
        if (!esEstadoValido(stringEstado)){
            throw new NoSuchElementException("No existe el estado: " + stringEstado);
        }

        Estado estado = Estado.valueOf(stringEstado.toUpperCase());

        List<CuentaEntity> cuentas = cuentaRepository.findByEstado(estado);

        if (cuentas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el estado: " + estado);
        }
        return cuentas;
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
        CuentaEntity cuenta = buscarPorNickname(nickname);
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        cuenta.setPassword(passwordEncoder.encode(password));

        return cuentaRepository.save(cuenta);
    }

    public CuentaEntity cambiarRol(String nickname, String rol) throws UsuarioNoEncontradoException, RolInvalidoException {
        if (!esRolValido(rol)) {
            throw new RolInvalidoException("El rol proporcionado no es válido: " + rol);
        }

        CuentaEntity cuenta = buscarPorNickname(nickname);

        cuenta.setRol(Roles.valueOf(rol.toUpperCase()));

        return cuentaRepository.save(cuenta);
    }

    public CuentaEntity darDeBajaCuenta(String nickname) throws UsuarioNoEncontradoException {
        CuentaEntity cuenta = buscarPorNickname(nickname);
        cuenta.setEstado(Estado.BAJA);
        return cuentaRepository.save(cuenta);
    }

    public CuentaEntity restablecerCuenta(String nickname) throws UsuarioNoEncontradoException {
        CuentaEntity cuenta = buscarPorNickname(nickname);
        cuenta.setEstado(Estado.ACTIVO);
        return cuentaRepository.save(cuenta);
    }

    public CuentaResponse toCuentaResponse(CuentaEntity cuenta) {
        return new CuentaResponse(
                cuenta.getNickname(),
                cuenta.getRol().name(),
                cuenta.getEstado().name()
        );
    }

}
