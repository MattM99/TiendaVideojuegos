package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.DetalleAlquilerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles_alquileres")
@Tag(name = "Detalle Alquiler", description = "Operaciones relacionadas con la gestión de los detalles de alquileres de videojuegos")
public class DetalleAlquilerController {

    @Autowired
    private DetalleAlquilerService detalleAlquilerService;

    @Operation(summary = "Crear un detalle de alquiler", description = "Crea una nueva entrada de detalle de alquiler")
    @PostMapping("/crear")
    public ResponseEntity<DetalleAlquilerResponse> crearDetalle(@Valid @RequestBody DetalleAlquilerCreateOrReplaceRequest request) {
        DetalleAlquilerResponse response = detalleAlquilerService.crearDetalle(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un detalle de alquiler", description = "Elimina una nueva entrada de detalle de alquiler")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable int id) {
        detalleAlquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
