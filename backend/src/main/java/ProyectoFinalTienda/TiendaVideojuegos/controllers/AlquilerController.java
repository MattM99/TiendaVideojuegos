package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CerrarAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ValidarDisponibilidadResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.services.AlquilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/alquileres")
@Tag(name = "Alquileres", description = "Operaciones relacionadas con los alquileres de videojuegos")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    // Se consume esto antes que crearAlquiler, para ver disponibilidad antes de crear el alquiler y la posibilidad de crear nueva reserva
    @Operation(summary = "Validar disponibilidad", description = "Verifica si existe stock suficiente para los videojuegos solicitados")
    @PostMapping("/validar")
    public ResponseEntity<ValidarDisponibilidadResponse>
    validarDisponibilidad(
            @RequestBody @Valid AlquilerCreateOrReplaceRequest request) {

        return ResponseEntity.ok(
                alquilerService.validarDisponibilidad(request)
        );
    }

    @Operation(summary = "Crear un nuevo alquiler", description = "Permite crear un nuevo alquiler de videojuego")
    @PostMapping
    public ResponseEntity<AlquilerResponse> crearAlquiler(@Valid @RequestBody AlquilerCreateOrReplaceRequest request) {
        AlquilerResponse response = alquilerService.crearAlquiler(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Actualizar fecha de finalización de un alquiler",
            description = "Permite modificar únicamente fechaFin de un alquiler activo y EN_CURSO"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<AlquilerResponse> actualizar(
            @PathVariable int id,
            @Valid @RequestBody AlquilerUpdateRequest request
    ) {
        return ResponseEntity.ok(
                alquilerService.actualizar(id, request)
        );
    }

    @PostMapping("/{alquilerId}/finalizar")
    public ResponseEntity<AlquilerResponse> cerrarAlquiler(
            @PathVariable Integer alquilerId,
            @Valid @RequestBody CerrarAlquilerRequest request
    ) {

        AlquilerResponse response =
                alquilerService.cerrarAlquiler(alquilerId, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un alquiler", description = "Permite eliminar un alquiler de videojuego por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlquiler(@PathVariable int id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar alquileres activos",
            description = "Permite filtrar por estado, rango de fecha de inicio, nombre o apellido del cliente")
    @GetMapping("/listaractivos")
    public ResponseEntity<Page<AlquilerResponse>> listarActivos(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin,
            @RequestParam(required = false) EstadoAlquiler estado,
            @RequestParam(required = false) String persona,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "fechaInicio") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Pageable paginacion = paginarListado(pagina, tamano, ordenarPor, direccion);

        return ResponseEntity.ok(
                alquilerService.listarActivos(fechaInicio, fechaFin, estado, persona, paginacion)
        );
    }

    @Operation(
            summary = "Listar todos los alquileres para administración",
            description = "Devuelve alquileres activos e inactivos con filtro opcional por estado"
    )
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/administracion/obtenertodos")
    public ResponseEntity<Page<AlquilerResponse>> obtenerTodos(
            @RequestParam(required = false) EstadoAlquiler estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "alquilerId") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Pageable paginacion = paginarListado(pagina, tamano, ordenarPor, direccion);

        return ResponseEntity.ok(
                alquilerService.obtenerTodos(estado, paginacion)
        );
    }

    private Pageable paginarListado(int pagina, int tamano, String ordenarPor, String direccion) {
        Sort.Direction sentido = direccion.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = ordenarPor.equals("alquilerId")
                ? Sort.by(sentido, ordenarPor)
                : Sort.by(sentido, ordenarPor, "alquilerId");
        return PageRequest.of(pagina, tamano, sort);
    }

    @Operation(summary = "Obtener un alquiler por ID", description = "Devuelve un alquiler específico por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<AlquilerResponse> obtenerPorId(@PathVariable int id) {
        AlquilerResponse response = alquilerService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar alquiler por ID para administración",
            description = "Permite consultar alquileres activos e inactivos"
    )
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/administracion/{id}")
    public ResponseEntity<AlquilerResponse> buscarPorIdAdministracion(
            @PathVariable int id
    ) {
        return ResponseEntity.ok(
                alquilerService.buscarPorIdAdministracion(id)
        );
    }

    @Operation(summary = "Obtener alquileres por usuario", description = "Devuelve una lista de alquileres asociados a un usuario específico")
    @GetMapping("/usuario/{personaId}")
    public ResponseEntity<Page<AlquilerResponse>> obtenerPorUsuario(
            @PathVariable int personaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "fechaInicio") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {

        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(
                pagina,
                tamano,
                sort
        );

        Page<AlquilerResponse> responses =
                alquilerService.buscarPorUsuario(
                        personaId,
                        paginacion
                );
        return ResponseEntity.ok(responses);
    }
}
