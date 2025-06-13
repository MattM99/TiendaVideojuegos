package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.AlquilerService;
import ProyectoFinalTienda.TiendaVideojuegos.services.DetalleAlquilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
@Tag(name = "Gestión alquileres", description = "Operaciones relacionadas con los alquileres de videojuegos")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private DetalleAlquilerController detalleAlquilerController;


    @Operation(summary = "Crear un nuevo alquiler", description = "Permite crear un nuevo alquiler de videojuego")
    @PostMapping
    public ResponseEntity<AlquilerResponse> crearAlquiler(@Valid @RequestBody AlquilerCreateOrReplaceRequest request) {
        AlquilerResponse response = alquilerService.guardar(request);
        DetalleAlquilerCreateOrReplaceRequest detalleRequest = DetalleAlquilerCreateOrReplaceRequest.builder()
                .alquiler_id(response.getAlquiler_id())
                .inventario_id(request.getIdJuego())
                .build();
        DetalleAlquilerResponse detalleResponse = detalleAlquilerController.crearDetalle(detalleRequest).getBody();
        response.setDetalles(detalleResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar un alquiler", description = "Permite eliminar un alquiler de videojuego por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlquiler(@PathVariable int id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener todos los alquileres", description = "Devuelve una lista de todos los alquileres registrados")
    @GetMapping("/listar")
    public ResponseEntity<List<AlquilerResponse>> obtenerTodos() {
        return ResponseEntity.ok(alquilerService.obtenerTodos());
    }

    // Obtener un alquiler por su ID
    @GetMapping("/{id}")
    public ResponseEntity<AlquilerResponse> obtenerPorId(@PathVariable int id) {
        AlquilerResponse response = alquilerService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    // Obtener alquileres por ID de persona (usuario)
    @GetMapping("/usuario/{personaId}")
    public ResponseEntity<List<AlquilerResponse>> obtenerPorUsuario(@PathVariable int personaId) {
        List<AlquilerResponse> responses = alquilerService.buscarPorUsuario(personaId);
        return ResponseEntity.ok(responses);
    }
}
