package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.exception.EstadoInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.NotFoundException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.RolInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.List;
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

    public CuentaEntity buscarPorNickname(String nombre) throws UsuarioNoEncontradoException {
        Optional<CuentaEntity> cuenta = cuentaRepository.findByNickname(nombre);
        if (cuenta.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se ha encontrado al usuario con el nombre: " + nombre);
        }
        return cuenta.get();
    }

    public List<CuentaEntity> buscarPorRol(String stringRol) throws RolInvalidoException, NotFoundException {
        if (!esRolValido(stringRol)){
            throw new RolInvalidoException("No existe el rol: " + stringRol);
        }

        Roles rol = Roles.valueOf(stringRol.toUpperCase());

        List<CuentaEntity> cuentas = cuentaRepository.findByRol(rol);

        if (cuentas.isEmpty()) {
            throw new NotFoundException("No se encontraron usuarios con el rol: " + rol);
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

    public List<CuentaEntity> buscarPorEstado(String stringEstado) throws EstadoInvalidoException, NotFoundException {
        if (!esEstadoValido(stringEstado)){
            throw new RolInvalidoException("No existe el estado: " + stringEstado);
        }

        Estado estado = Estado.valueOf(stringEstado.toUpperCase());

        List<CuentaEntity> cuentas = cuentaRepository.findByEstado(estado);

        if (cuentas.isEmpty()) {
            throw new NotFoundException("No se encontraron usuarios con el estado: " + estado);
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


}
