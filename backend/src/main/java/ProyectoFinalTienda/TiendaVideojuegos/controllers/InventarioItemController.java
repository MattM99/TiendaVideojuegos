package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioItemResponse;
import ProyectoFinalTienda.TiendaVideojuegos.services.InventarioItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@Validated
@Tag(name = "Inventario", description = "Operaciones relacionadas con la gestión del inventario")
public class InventarioItemController {

    @Autowired
    private InventarioItemService inventarioItemService;

    @Operation(summary = "Crear un inventario", description = "Crea una nuevo inventario")
    @PostMapping("/crear")
    public ResponseEntity<InventarioItemResponse> crearInventario(@Valid @RequestBody InventarioItemCreateOrReplaceRequest request) {
        InventarioItemResponse response = inventarioItemService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar un inventario", description = "Elimina un inventario por su ID")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable int id) {
        inventarioItemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todos los inventarios", description = "Devuelve una lista de todos los inventarios")
    @GetMapping("/listar")
    public ResponseEntity<List<InventarioItemResponse>> listarTodos() {
        return ResponseEntity.ok(inventarioItemService.obtenerTodos());
    }

    @Operation(summary = "Obtener un inventario por ID", description = "Devuelve un inventario específico por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<InventarioItemResponse> obtenerPorId(@PathVariable int id) {
        InventarioItemResponse response = inventarioItemService.buscarPorId(id);
        return  ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar inventarios por videojuego", description = "Devuelve una lista de inventarios que contienen un videojuego específico")
    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<InventarioItemResponse>> buscarPorVideojuego(@PathVariable int videojuegoId) {
        return ResponseEntity.ok(inventarioItemService.buscarPorVideojuego(videojuegoId));
    }

    @Operation(summary = "Buscar inventarios por plataforma", description = "Devuelve una lista de inventarios que pertenecen a una plataforma específica")
    @GetMapping("/plataforma/{plataforma}")
    public ResponseEntity<List<InventarioItemResponse>> buscarPorPlataforma(@PathVariable String plataforma) {
        return ResponseEntity.ok(inventarioItemService.buscarPorPlataforma(plataforma));
    }

    @Operation(summary = "Buscar inventarios con precio menor a un valor", description = "Devuelve una lista de inventarios cuyo precio es menor al valor especificado")
    @GetMapping("/precio/menor-a")
    public ResponseEntity<List<InventarioItemResponse>> buscarMasBaratosQue(@RequestParam double valor) {
        return ResponseEntity.ok(inventarioItemService.buscarMasBaratosQue(valor));
    }

    @Operation(summary = "Buscar inventarios por plataforma y precio menor a un valor", description = "Devuelve una lista de inventarios que pertenecen a una plataforma específica y cuyo precio es menor al valor especificado")
    @GetMapping("/plataformaPrecio/menor-a")
    public ResponseEntity<List<InventarioItemResponse>> buscarPorPlataformaMasBaratosQue(
            @RequestParam String plataforma,
            @RequestParam double valor) {
        return ResponseEntity.ok(inventarioItemService.buscarPorPlataformaMasBaratosQue(plataforma, valor));
    }

    @Operation(summary = "Obtener stock total", description = "Devuelve el stock total de un inventario por ID")
    @GetMapping("/{id}/stock-total")
    public ResponseEntity<Integer> obtenerStockTotal(@PathVariable int id) {
        return ResponseEntity.ok(inventarioItemService.obtenerStockTotal(id));
    }

    @Operation(summary = "Obtener stock disponible", description = "Devuelve el stock disponible de un inventario por ID")
    @GetMapping("/{id}/stock-disponible")
    public ResponseEntity<Integer> obtenerStockDisponible(@PathVariable int id) {
        return ResponseEntity.ok(inventarioItemService.obtenerStockDisponible(id));
    }

//    @Operation(summary = "Obtener stock alquilado", description = "Devuelve el stock alquilado de un inventario por ID")
//    @GetMapping("/{id}/stock-alquilado")
//    public ResponseEntity<Integer> obtenerStockAlquilado(@PathVariable int id) {
//        return ResponseEntity.ok(inventarioItemService.obtenerStockAlquilado(id));
//    }

//    @Operation(summary = "Obtener stock descartado", description = "Devuelve el stock descartado de un inventario por ID")
//    @GetMapping("/{id}/stock-descartado")
//    public ResponseEntity<Integer> obtenerStockDescartado(@PathVariable int id) {
//        return ResponseEntity.ok(inventarioItemService.obtenerStockDescartado(id));
//    }

    @Operation(summary = "Actualizar inventario completo", description = "Actualiza todos los campos de un inventario por ID")
    @PutMapping("/{id}")
    public ResponseEntity<InventarioItemResponse> actualizarInventarioCompleto(
            @PathVariable int id,
            @Valid @RequestBody InventarioItemCreateOrReplaceRequest request) {
        InventarioItemResponse response = inventarioItemService.actualizarCompleto(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar inventario por campo", description = "Actualiza un campo específico de un inventario por ID")
    @PatchMapping("/{id}")
    public ResponseEntity<InventarioItemResponse> actualizarInventarioPorCampo(
            @PathVariable int id,
            @RequestBody InventarioItemUpdateRequest request) {
        InventarioItemResponse response = inventarioItemService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Agregar stock", description = "Incrementa el stock disponible y total de un inventario")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/agregar-stock")
    public ResponseEntity<InventarioResponse> agregarStock(
            @PathVariable int id,
            @RequestParam int cantidad) {

        InventarioResponse response = inventarioService.agregarStock(id, cantidad);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Dar de baja inventario", description = "Da de baja un inventario (stock en 0)")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PatchMapping("/{id}/baja")
    public ResponseEntity<InventarioResponse> darDeBaja(@PathVariable int id) {

        InventarioResponse response = inventarioService.darDeBaja(id);
        return ResponseEntity.ok(response);
    }

}