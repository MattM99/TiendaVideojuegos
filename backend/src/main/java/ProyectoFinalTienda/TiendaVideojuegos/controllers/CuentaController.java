package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CambiarContrasenaRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CambiarRolRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.CuentaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.RolInvalidoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
@Validated
@Tag(name = "Cuenta", description = "Operaciones relacionadas con la gestión de cuentas de usuario")
public class CuentaController {
    private final CuentaService cuentaService;

    @Operation(summary = "Listar cuentas", description = "Lista todas las cuentas registradas")
    @GetMapping("/listar")
    public ResponseEntity<Page<CuentaResponse>> listarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "nickname") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);


        return ResponseEntity.ok(
                cuentaService.listarTodos(paginacion)
        );
    }

    @Operation(summary = "Buscar cuenta por nickname", description = "Devuelve los datos de una cuenta específica usando su nickname")
    @GetMapping("/{nickname}")
    public ResponseEntity<CuentaResponse> buscarPorNickname(@PathVariable String nickname) throws UsuarioNoEncontradoException {
        CuentaEntity cuenta = cuentaService.buscarPorNickname(nickname);
        return ResponseEntity.ok(cuentaService.toCuentaResponse(cuenta));
    }

    @Operation(summary = "Buscar cuentas por rol", description = "Devuelve una lista de cuentas que tienen el rol especificado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<Page<CuentaResponse>> buscarPorRol(
            @PathVariable String rol,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "nickname") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) throws NoSuchElementException, UsuarioNoEncontradoException {

        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<CuentaResponse> responses =
                cuentaService.buscarPorRol(rol, paginacion);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar cuentas por estado", description = "Devuelve una lista de cuentas filtradas por estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<CuentaResponse>> buscarPorEstado(
            @PathVariable String estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "nickname") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) throws UsuarioNoEncontradoException, NoSuchElementException {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(
                pagina,
                tamano,
                sort
        );

        Page<CuentaResponse> responses =
                cuentaService.buscarPorEstado(estado, paginacion);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Cambiar contraseña de una cuenta (admin)", description = "Permite que un administrador cambie la contraseña de cualquier cuenta usando el nickname")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{nickname}/contrasena")
    public ResponseEntity<CuentaResponse> cambiarContrasena(
            @PathVariable String nickname,
            @RequestBody @Valid CambiarContrasenaRequest request
    ) throws UsuarioNoEncontradoException {

        CuentaEntity cuentaActualizada = cuentaService.cambiarContrasena(nickname, request.getNuevaContrasena());
        CuentaResponse response = cuentaService.toCuentaResponse(cuentaActualizada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar mi propia contraseña", description = "Permite al usuario autenticado cambiar su propia contraseña")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/mi-cuenta/contrasena")
    public ResponseEntity<CuentaResponse> cambiarMiContrasena(
            @RequestBody @Valid CambiarContrasenaRequest request,
            Authentication authentication
    ) throws UsuarioNoEncontradoException {

        String nickname = authentication.getName(); // Extrae el usuario autenticado
        CuentaEntity cuentaActualizada = cuentaService.cambiarContrasena(nickname, request.getNuevaContrasena());
        CuentaResponse response = cuentaService.toCuentaResponse(cuentaActualizada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar el rol de una cuenta", description = "Permite a un administrador cambiar el rol de una cuenta especificada")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{nickname}/rol")
    public ResponseEntity<CuentaResponse> cambiarRol(
            @PathVariable String nickname,
            @RequestBody @Valid CambiarRolRequest request
    ) throws UsuarioNoEncontradoException, RolInvalidoException {

        CuentaEntity cuentaActualizada = cuentaService.cambiarRol(nickname, request.getNuevoRol());
        CuentaResponse response = cuentaService.toCuentaResponse(cuentaActualizada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Dar de baja una cuenta", description = "Permite a un administrador desactivar una cuenta existente")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{nickname}/baja")
    public ResponseEntity<CuentaResponse> darDeBaja(
            @PathVariable String nickname)
            throws UsuarioNoEncontradoException {
        CuentaEntity cuentaActualizada = cuentaService.darDeBajaCuenta(nickname);
        CuentaResponse response = cuentaService.toCuentaResponse(cuentaActualizada);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reactivar una cuenta", description = "Permite a un administrador volver a activar una cuenta previamente dada de baja")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{nickname}/alta")
    public ResponseEntity<CuentaResponse> restablecerCuenta(@PathVariable String nickname) throws UsuarioNoEncontradoException {
        CuentaEntity cuentaActualizada = cuentaService.restablecerCuenta(nickname);
        CuentaResponse response = cuentaService.toCuentaResponse(cuentaActualizada);

        return ResponseEntity.ok(response);
    }

}
