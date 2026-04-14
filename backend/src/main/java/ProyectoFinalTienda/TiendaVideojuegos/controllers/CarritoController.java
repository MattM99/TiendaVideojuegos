package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CarritoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.CarritoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.services.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detalles_alquileres")
@Tag(name = "Detalle Alquiler", description = "Operaciones relacionadas con la gestión de los detalles de alquileres de videojuegos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Crear un detalle de alquiler", description = "Crea una nueva entrada de detalle de alquiler")
    @PostMapping("/crear")
    public ResponseEntity<CarritoResponse> crearDetalle(@Valid @RequestBody CarritoCreateOrReplaceRequest request) {
        CarritoResponse response = carritoService.crearDetalle(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un detalle de alquiler", description = "Elimina una nueva entrada de detalle de alquiler")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable int id) {
        carritoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
