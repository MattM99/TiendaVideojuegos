package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/videojuegos")
@Validated
public class VideojuegoController {

    @Autowired
    private VideojuegoService videojuegoService;

    // Creación de un nuevo videojuego

    @PostMapping
    public ResponseEntity<VideojuegoResponse> crearVideojuego(
            @Valid @RequestBody VideojuegoCreateOrReplaceRequest request) {

        // 1. Guardamos la entidad
        VideojuegoResponse saved = videojuegoService.guardar(request);

        // 2. La devolvemos con 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Eliminación de un videojuego por ID
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable int id){
        videojuegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Listar todos los videojuegos
    @GetMapping("/listar")
    public ResponseEntity<List<VideojuegoResponse>> listarTodos(){
        return ResponseEntity.ok(videojuegoService.obtenerTodos());
    }

    // Búsqueda por id
    @GetMapping("/id/{id}")
    public ResponseEntity<VideojuegoResponse> buscarPorId(@PathVariable int id){
        VideojuegoResponse response = videojuegoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    // Búsquedas por titulo
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorTitulo(@PathVariable String titulo){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(responses);
    }

    // Búsquedas por desarrollador
    @GetMapping("/desarrollador/{desarrollador}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorDesarrollador(@PathVariable String desarrollador){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorDesarrollador(desarrollador);
        return ResponseEntity.ok(responses);
    }

    // Búsquedas por genero
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorGenero(@PathVariable String genero){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorGenero(genero.trim().toUpperCase());
        return ResponseEntity.ok(responses);
    }

    // Búsqueda de videojuegos multijugador
    @GetMapping("/multijugador")
    public ResponseEntity<List<VideojuegoResponse>> buscarMultijugadores(){
        List<VideojuegoResponse> responses = videojuegoService.buscarMultijugadores();
        return ResponseEntity.ok(responses);
    }

    // Búsqueda por año de lanzamiento
    @GetMapping("/lanzamiento/{lanzamiento}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorLanzamiento(@PathVariable Year lanzamiento){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorLanzamiento(lanzamiento);
        return ResponseEntity.ok(responses);
    }

    // Actualización completa
    // No importa que comparta path con el de abajo; spring ya los diferencia por ser put y patch.
    @PutMapping("/{id}")
    public ResponseEntity<VideojuegoResponse> actualizarVideojuegoCompleto(
            @PathVariable int id,
            @Valid @RequestBody VideojuegoCreateOrReplaceRequest request) {
        VideojuegoResponse response = videojuegoService.actualizarCompleto(id, request);
        return ResponseEntity.ok(response);
    }

    // Actualización por campo
    @PatchMapping("/{id}")
    public ResponseEntity<VideojuegoResponse> actualizarVideojuegoPorCampo(
            @PathVariable int id,
            @RequestBody VideojuegoUpdateRequest request) {
        VideojuegoResponse response = videojuegoService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(response);
    }

}
