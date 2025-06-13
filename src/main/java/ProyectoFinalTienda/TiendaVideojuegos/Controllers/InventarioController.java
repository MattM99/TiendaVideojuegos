package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.services.InventarioService;
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
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    // Creación de un nuevo inventario
    @PostMapping("/inventario/crear")
    public ResponseEntity<InventarioResponse> crearInventario(@Valid @RequestBody InventarioCreateOrReplaceRequest request) {
        InventarioResponse response = inventarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Eliminar inventario
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable int id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los inventarios
    @GetMapping("/listar")
    public ResponseEntity<List<InventarioResponse>> listarTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    // Obtener inventario por ID
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponse> obtenerPorId(@PathVariable int id) {
        InventarioResponse response = inventarioService.buscarPorId(id);
        return  ResponseEntity.ok(response);
    }

    // Buscar por videojuego
    @GetMapping("/videojuego/{videojuegoId}")
    public ResponseEntity<List<InventarioResponse>> buscarPorVideojuego(@PathVariable int videojuegoId) {
        return ResponseEntity.ok(inventarioService.buscarPorVideojuego(videojuegoId));
    }

    // Buscar por plataforma
    @GetMapping("/plataforma/{plataforma}")
    public ResponseEntity<List<InventarioResponse>> buscarPorPlataforma(@PathVariable String plataforma) {
        return ResponseEntity.ok(inventarioService.buscarPorPlataforma(plataforma));
    }

    // Buscar más baratos que cierto valor
    @GetMapping("/precio/menor-a")
    public ResponseEntity<List<InventarioResponse>> buscarMasBaratosQue(@RequestParam double valor) {
        return ResponseEntity.ok(inventarioService.buscarMasBaratosQue(valor));
    }

    // Buscar por plataforma y precio menor a cierto valor
    @GetMapping("/plataformaPrecio/menor-a")
    public ResponseEntity<List<InventarioResponse>> buscarPorPlataformaMasBaratosQue(
            @RequestParam String plataforma,
            @RequestParam double valor) {
        return ResponseEntity.ok(inventarioService.buscarPorPlataformaMasBaratosQue(plataforma, valor));
    }

    // Obtener stock total
    @GetMapping("/{id}/stock-total")
    public ResponseEntity<Integer> obtenerStockTotal(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockTotal(id));
    }

    // Obtener stock disponible
    @GetMapping("/{id}/stock-disponible")
    public ResponseEntity<Integer> obtenerStockDisponible(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockDisponible(id));
    }

    // Obtener stock alquilado
    @GetMapping("/{id}/stock-alquilado")
    public ResponseEntity<Integer> obtenerStockAlquilado(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockAlquilado(id));
    }

    // Obtener stock descartado
    @GetMapping("/{id}/stock-descartado")
    public ResponseEntity<Integer> obtenerStockDescartado(@PathVariable int id) {
        return ResponseEntity.ok(inventarioService.obtenerStockDescartado(id));
    }

    // PUT: Actualización completa
    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizarInventarioCompleto(
            @PathVariable int id,
            @Valid @RequestBody InventarioCreateOrReplaceRequest request) {
        InventarioResponse response = inventarioService.actualizarCompleto(id, request);
        return ResponseEntity.ok(response);
    }

    // PATCH: Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizarInventarioPorCampo(
            @PathVariable int id,
            @RequestBody InventarioUpdateRequest request) {
        InventarioResponse response = inventarioService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(response);
    }
}
