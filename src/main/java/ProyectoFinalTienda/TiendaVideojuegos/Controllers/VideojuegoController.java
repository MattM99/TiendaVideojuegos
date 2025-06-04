package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateRequest;
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

    @PostMapping
    public ResponseEntity<VideojuegoEntity> crearVideojuego(@Valid @RequestBody VideojuegoCreateRequest request) {
        VideojuegoEntity nuevo = videojuegoService.guardar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable int id){
        videojuegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar")
    public ResponseEntity<List<VideojuegoEntity>> listarTodos(){
        return ResponseEntity.ok(videojuegoService.obtenerTodos());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<VideojuegoEntity> buscarPorId(@PathVariable int id){
        VideojuegoEntity videojuego = videojuegoService.buscarPorId(id);
        return  ResponseEntity.ok(videojuego);
    }

    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorTitulo(@PathVariable String titulo){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(videojuegos);
    }

    @GetMapping("/desarrollador/{desarrollador}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorDesarrollador(@PathVariable String desarrollador){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorDesarrollador(desarrollador);
        return ResponseEntity.ok(videojuegos);
    }

    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<VideojuegoEntity>> buscarPorGenero(@PathVariable Generos genero){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarPorGenero(genero);
        return ResponseEntity.ok(videojuegos);
    }

    @GetMapping("/multijugador")
    public ResponseEntity<List<VideojuegoEntity>> buscarMultijugadores(){
        List<VideojuegoEntity> videojuegos = videojuegoService.buscarMultijugadores();
        return ResponseEntity.ok(videojuegos);
    }

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
            @Valid @RequestBody VideojuegoCreateRequest request) {
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
