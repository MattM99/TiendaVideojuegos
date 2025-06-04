package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
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
    public ResponseEntity<VideojuegoEntity> crearVideojuego(@Valid @RequestBody VideojuegoCreateOrReplaceRequest request) {
        VideojuegoEntity nuevo = videojuegoService.guardar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Eliminación de un videojuego por ID
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable int id){
        videojuegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Listar todos los videojuegos
    @GetMapping("/listar")
    public ResponseEntity<List<VideojuegoEntity>> listarTodos(){
        return ResponseEntity.ok(videojuegoService.obtenerTodos());
    }

    // Búsqueda por id
    @GetMapping("/id/{id}")
    public ResponseEntity<VideojuegoEntity> buscarPorId(@PathVariable int id){
        VideojuegoEntity videojuego = videojuegoService.buscarPorId(id);
        return  ResponseEntity.ok(videojuego);
    }

    // Búsquedas por titulo
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorTitulo(@PathVariable String titulo){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(videojuegos);
    }

    // Búsquedas por desarrollador
    @GetMapping("/desarrollador/{desarrollador}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorDesarrollador(@PathVariable String desarrollador){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorDesarrollador(desarrollador);
        return ResponseEntity.ok(videojuegos);
    }

    // Búsquedas por genero
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorGenero(@PathVariable Generos genero){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorGenero(genero);
        return ResponseEntity.ok(videojuegos);
    }

    // Búsqueda de videojuegos multijugador
    @GetMapping("/multijugador")
    public ResponseEntity<List<VideojuegoEntity>> buscarMultijugadores(){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarMultijugadores();
        return ResponseEntity.ok(videojuegos);
    }

    // Búsqueda por año de lanzamiento
    @GetMapping("/lanzamiento/{lanzamiento}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorLanzamiento(@PathVariable Year lanzamiento){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorLanzamiento(lanzamiento);
        return ResponseEntity.ok(videojuegos);
    }

    // Actualización completa
    // No importa que comparta path con el de abajo; spring ya los diferencia por ser put y patch.
    @PutMapping("/{id}")
    public ResponseEntity<VideojuegoEntity> actualizarVideojuegoCompleto(
            @PathVariable int id,
            @Valid @RequestBody VideojuegoCreateOrReplaceRequest request) {
        VideojuegoEntity actualizado = videojuegoService.actualizarCompleto(id, request);
        return ResponseEntity.ok(actualizado);
    }

    // Actualización por campo
    @PatchMapping("/{id}")
    public ResponseEntity<VideojuegoEntity> actualizarVideojuegoPorCampo(
            @PathVariable int id,
            @RequestBody VideojuegoUpdateRequest request) {
        VideojuegoEntity actualizado = videojuegoService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(actualizado);
    }



}
