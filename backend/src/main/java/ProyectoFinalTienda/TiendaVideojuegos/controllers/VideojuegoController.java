package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

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
    public ResponseEntity<Page<VideojuegoResponse>> listarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);


        return ResponseEntity.ok(
                videojuegoService.listarTodos(paginacion)
        );
    }

    @Operation(summary = "Buscar videojuego por ID", description = "Devuelve un videojuego específico por su ID")
    @GetMapping("/id/{id}")
    public ResponseEntity<VideojuegoResponse> buscarPorId(@PathVariable int id){
        VideojuegoResponse response = videojuegoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar videojuegos por título", description = "Devuelve una lista de videojuegos que coincidan con el título proporcionado")
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<Page<VideojuegoResponse>> buscarPorTitulo(
            @PathVariable String titulo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "nickname") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion

    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<VideojuegoResponse> responses = videojuegoService.buscarPorTitulo(titulo, paginacion);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar videojuegos por desarrollador", description = "Devuelve una lista de videojuegos desarrollados por el desarrollador especificado")
    @GetMapping("/desarrollador/{desarrollador}")
    public ResponseEntity<Page<VideojuegoResponse>> buscarPorDesarrollador(
            @PathVariable String desarrollador,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<VideojuegoResponse> responses = videojuegoService.buscarPorDesarrollador(desarrollador, paginacion);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/genero/{genero}")
    public ResponseEntity<Page<VideojuegoResponse>> buscarPorGenero(
            @PathVariable Generos genero,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<VideojuegoResponse> responses =
                videojuegoService.buscarPorGenero(genero, paginacion);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar videojuegos multijugador", description = "Devuelve una lista de videojuegos que son multijugador")
    @GetMapping("/multijugador")
    public ResponseEntity<Page<VideojuegoResponse>> buscarMultijugadores(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<VideojuegoResponse> responses = videojuegoService.buscarMultijugadores(paginacion);

        return ResponseEntity.ok(responses);
    }

    // Búsqueda por año de lanzamiento
    @Operation(summary = "Buscar videojuegos por lanzamiento", description = "Devuelve una lista de videojuegos lanzados en un año específico")
    @GetMapping("/lanzamiento/{lanzamiento}")
    public ResponseEntity<Page<VideojuegoResponse>> buscarPorLanzamiento(
            @PathVariable Year lanzamiento,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<VideojuegoResponse> responses = videojuegoService.buscarPorLanzamiento(lanzamiento, paginacion);

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
