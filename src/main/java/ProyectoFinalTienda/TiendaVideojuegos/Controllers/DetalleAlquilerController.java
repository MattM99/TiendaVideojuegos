package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.DetalleAlquilerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalles_alquileres")
public class DetalleAlquilerController {

    @Autowired
    private DetalleAlquilerService detalleAlquilerService;

    @PostMapping
    public ResponseEntity<DetalleAlquilerResponse> crearDetalle(@Valid @RequestBody DetalleAlquilerCreateOrReplaceRequest request) {
        DetalleAlquilerResponse response = detalleAlquilerService.crearDetalle(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable int id) {
        detalleAlquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
