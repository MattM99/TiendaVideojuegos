package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CerrarAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/alquileres")
@Tag(name = "Alquileres", description = "Operaciones relacionadas con los alquileres de videojuegos")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    @Operation(summary = "Crear un nuevo alquiler", description = "Permite crear un nuevo alquiler de videojuego")
    @PostMapping
    public ResponseEntity<AlquilerResponse> crearAlquiler(@Valid @RequestBody AlquilerCreateOrReplaceRequest request) {
        AlquilerResponse response = alquilerService.crearAlquiler(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    @Operation(summary = "Obtener todos los alquileres", description = "Devuelve una lista de todos los alquileres registrados")
    @GetMapping("/listar")
    public ResponseEntity<Page<AlquilerResponse>> listarTodos(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "fechaInicio") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);


        return ResponseEntity.ok(
                alquilerService.listarTodos(fechaInicio, fechaFin, paginacion)
        );
    }

    @Operation(summary = "Obtener un alquiler por ID", description = "Devuelve un alquiler específico por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<AlquilerResponse> obtenerPorId(@PathVariable int id) {
        AlquilerResponse response = alquilerService.buscarPorId(id);
        return ResponseEntity.ok(response);
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
