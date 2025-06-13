package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.services.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@Validated
@Tag(name = "Inventario", description = "Operaciones relacionadas con la gestión del inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Crear un inventario", description = "Crea una nuevo inventario")
    @PostMapping("/crear")
    public ResponseEntity<InventarioResponse> crearInventario(@Valid @RequestBody InventarioCreateOrReplaceRequest request) {
        InventarioResponse response = inventarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar un inventario", description = "Elimina un inventario por su ID")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable int id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos los inventarios", description = "Devuelve una lista de todos los inventarios")
    @GetMapping("/listar")
    public ResponseEntity<List<InventarioResponse>> listarTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @Operation(summary = "Obtener un inventario por ID", description = "Devuelve un inventario específico por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> obtenerPorId(@PathVariable int id) {
        InventarioResponse response = inventarioService.buscarPorId(id);
        return  ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar inventarios por videojuego", description = "Devuelve una lista de inventarios que contienen un videojuego específico")
    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<InventarioResponse>> buscarPorVideojuego(@PathVariable int videojuegoId) {
        return ResponseEntity.ok(inventarioService.buscarPorVideojuego(videojuegoId));
    }

    @Operation(summary = "Buscar inventarios por plataforma", description = "Devuelve una lista de inventarios que pertenecen a una plataforma específica")
    @GetMapping("/plataforma/{plataforma}")
    public ResponseEntity<List<InventarioResponse>> buscarPorPlataforma(@PathVariable String plataforma) {
        return ResponseEntity.ok(inventarioService.buscarPorPlataforma(plataforma));
    }

    @Operation(summary = "Buscar inventarios con precio menor a un valor", description = "Devuelve una lista de inventarios cuyo precio es menor al valor especificado")
    @GetMapping("/precio/menor-a")
    public ResponseEntity<List<InventarioResponse>> buscarMasBaratosQue(@RequestParam double valor) {
        return ResponseEntity.ok(inventarioService.buscarMasBaratosQue(valor));
    }

    @Operation(summary = "Buscar inventarios por plataforma y precio menor a un valor", description = "Devuelve una lista de inventarios que pertenecen a una plataforma específica y cuyo precio es menor al valor especificado")
    @GetMapping("/plataformaPrecio/menor-a")
    public ResponseEntity<List<InventarioResponse>> buscarPorPlataformaMasBaratosQue(
            @RequestParam String plataforma,
            @RequestParam double valor) {
        return ResponseEntity.ok(inventarioService.buscarPorPlataformaMasBaratosQue(plataforma, valor));
    }

    @Operation(summary = "Obtener stock total", description = "Devuelve el stock total de un inventario por ID")
    @GetMapping("/{id}/stock-total")
    public ResponseEntity<Integer> obtenerStockTotal(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockTotal(id));
    }

    @Operation(summary = "Obtener stock disponible", description = "Devuelve el stock disponible de un inventario por ID")
    @GetMapping("/{id}/stock-disponible")
    public ResponseEntity<Integer> obtenerStockDisponible(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockDisponible(id));
    }

    @Operation(summary = "Obtener stock alquilado", description = "Devuelve el stock alquilado de un inventario por ID")
    @GetMapping("/{id}/stock-alquilado")
    public ResponseEntity<Integer> obtenerStockAlquilado(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockAlquilado(id));
    }

    @Operation(summary = "Obtener stock descartado", description = "Devuelve el stock descartado de un inventario por ID")
    @GetMapping("/{id}/stock-descartado")
    public ResponseEntity<Integer> obtenerStockDescartado(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockDescartado(id));
    }

    @Operation(summary = "Actualizar inventario completo", description = "Actualiza todos los campos de un inventario por ID")
    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizarInventarioCompleto(
            @PathVariable int id,
            @Valid @RequestBody InventarioCreateOrReplaceRequest request) {
        InventarioResponse response = inventarioService.actualizarCompleto(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar inventario por campo", description = "Actualiza un campo específico de un inventario por ID")
    @PatchMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizarInventarioPorCampo(
            @PathVariable int id,
            @RequestBody InventarioUpdateRequest request) {
        InventarioResponse response = inventarioService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(response);
    }
}
