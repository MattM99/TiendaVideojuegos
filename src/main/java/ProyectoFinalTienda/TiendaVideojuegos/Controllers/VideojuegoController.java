package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Videojuegos", description = "Operaciones relacionadas con la gestión de videojuegos")
public class VideojuegoController {

    @Autowired
    private VideojuegoService videojuegoService;

    @Operation(summary = "Crear un nuevo videojuego", description = "Permite crear un nuevo videojuego en la base de datos")
    @PostMapping("/crear")
    public ResponseEntity<VideojuegoResponse> crearVideojuego(
            @Valid @RequestBody VideojuegoCreateOrReplaceRequest request) {

        // 1. Guardamos la entidad
        VideojuegoResponse saved = videojuegoService.guardar(request);

        // 2. La devolvemos con 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Eliminar un videojuego", description = "Permite eliminar un videojuego por su ID")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable int id){
        videojuegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos los videojuegos", description = "Devuelve una lista de todos los videojuegos registrados")
    @GetMapping("/listar")
    public ResponseEntity<List<VideojuegoResponse>> listarTodos(){
        return ResponseEntity.ok(videojuegoService.obtenerTodos());
    }

    @Operation(summary = "Buscar videojuego por ID", description = "Devuelve un videojuego específico por su ID")
    @GetMapping("/id/{id}")
    public ResponseEntity<VideojuegoResponse> buscarPorId(@PathVariable int id){
        VideojuegoResponse response = videojuegoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar videojuegos por título", description = "Devuelve una lista de videojuegos que coincidan con el título proporcionado")
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorTitulo(@PathVariable String titulo){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar videojuegos por desarrollador", description = "Devuelve una lista de videojuegos desarrollados por el desarrollador especificado")
    @GetMapping("/desarrollador/{desarrollador}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorDesarrollador(@PathVariable String desarrollador){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorDesarrollador(desarrollador);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar videojuegos por género", description = "Devuelve una lista de videojuegos que pertenecen al género especificado")
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorGenero(@PathVariable String genero){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorGenero(genero.trim().toUpperCase());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar videojuegos multijugador", description = "Devuelve una lista de videojuegos que son multijugador")
    @GetMapping("/multijugador")
    public ResponseEntity<List<VideojuegoResponse>> buscarMultijugadores(){
        List<VideojuegoResponse> responses = videojuegoService.buscarMultijugadores();
        return ResponseEntity.ok(responses);
    }

    // Búsqueda por año de lanzamiento
    @Operation(summary = "Buscar videojuegos por lanzamiento", description = "Devuelve una lista de videojuegos lanzados en un año específico")
    @GetMapping("/lanzamiento/{lanzamiento}")
    public ResponseEntity<List<VideojuegoResponse>> buscarPorLanzamiento(@PathVariable Year lanzamiento){
        List<VideojuegoResponse> responses = videojuegoService.buscarPorLanzamiento(lanzamiento);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Actualizar videojuego completo", description = "Permite actualizar todos los campos de un videojuego usando su ID")
    @PutMapping("/{id}")
    public ResponseEntity<VideojuegoResponse> actualizarVideojuegoCompleto(
            @PathVariable int id,
            @Valid @RequestBody VideojuegoCreateOrReplaceRequest request) {
        VideojuegoResponse response = videojuegoService.actualizarCompleto(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar videojuego por campo", description = "Permite actualizar parcialmente los campos de un videojuego usando su ID")
    @PatchMapping("/{id}")
    public ResponseEntity<VideojuegoResponse> actualizarVideojuegoPorCampo(
            @PathVariable int id,
            @RequestBody VideojuegoUpdateRequest request) {
        VideojuegoResponse response = videojuegoService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(response);
    }

}
