package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.AlquilerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    // Crear un nuevo alquiler
    @PostMapping
    public ResponseEntity<AlquilerResponse> crearAlquiler(@Valid @RequestBody AlquilerCreateOrReplaceRequest request) {
        AlquilerResponse response = alquilerService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Eliminar un alquiler por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAlquiler(@PathVariable int id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los alquileres
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
