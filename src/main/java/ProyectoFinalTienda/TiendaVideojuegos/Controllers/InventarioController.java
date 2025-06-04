package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioUpdateRequest;
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
    @PostMapping
    public ResponseEntity<InventarioEntity> crearInventario(@Valid @RequestBody InventarioCreateOrReplaceRequest request) {
        InventarioEntity creado = inventarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Eliminar inventario
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable int id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los inventarios
    @GetMapping("/listar")
    public ResponseEntity<List<InventarioEntity>> listarTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    // Obtener inventario por ID
    @GetMapping("/{id}")
    public ResponseEntity<InventarioEntity> obtenerPorId(@PathVariable int id) {
        InventarioEntity inventario = inventarioService.buscarPorId(id);
        return  ResponseEntity.ok(inventario);
    }

    // Buscar por plataforma
    @GetMapping("/plataforma")
    public ResponseEntity<List<InventarioEntity>> buscarPorPlataforma(@RequestParam Plataformas plataforma) {
        return ResponseEntity.ok(inventarioService.buscarPorPlataforma(plataforma));
    }

    // Buscar más baratos que cierto valor
    @GetMapping("/precio/menor-a")
    public ResponseEntity<List<InventarioEntity>> buscarMasBaratosQue(@RequestParam double valor) {
        return ResponseEntity.ok(inventarioService.buscarMasBaratosQue(valor));
    }

    // Buscar por plataforma y precio menor a cierto valor
    @GetMapping("/plataforma/precio/menor-a")
    public ResponseEntity<List<InventarioEntity>> buscarPorPlataformaMasBaratosQue(
            @RequestParam Plataformas plataforma,
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
    public ResponseEntity<InventarioEntity> actualizarInventarioCompleto(
            @PathVariable int id,
            @Valid @RequestBody InventarioCreateOrReplaceRequest request) {
        InventarioEntity actualizado = inventarioService.actualizarCompleto(id, request);
        return ResponseEntity.ok(actualizado);
    }

    // PATCH: Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<InventarioEntity> actualizarInventarioPorCampo(
            @PathVariable int id,
            @RequestBody InventarioUpdateRequest request) {
        InventarioEntity actualizado = inventarioService.actualizarPorCampo(id, request);
        return ResponseEntity.ok(actualizado);
    }
}
