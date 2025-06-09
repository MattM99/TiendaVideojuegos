package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlackListCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.BlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
@Validated
//@RequiredArgsConstructor
public class BlackListController {

    @Autowired
    private BlackListService blackListService;

    /**
     * Crear un nuevo registro en la lista negra.
     */
    @PostMapping
    public ResponseEntity<BlacklistEntity> crear(@RequestBody BlackListCreateOrReplaceRequest request) {
        BlacklistEntity creada = blackListService.crear(request);
        return ResponseEntity.ok(creada);
    }

    /**
     * Desbanear a una persona (cerrar su estado vigente en la blacklist).
     */
    @PutMapping("/desbanear/{personaId}")
    public ResponseEntity<BlacklistEntity> desbanear(@PathVariable int personaId) {
        BlacklistEntity actualizado = blackListService.desbanear(personaId);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Obtener el histórico completo de personas en la lista negra.
     */
    @GetMapping("/historico")
    public ResponseEntity<List<BlacklistEntity>> obtenerHistorico() {
        List<BlacklistEntity> lista = blackListService.obtenerHistorico();
        return ResponseEntity.ok(lista);
    }

    /**
     * Obtener personas actualmente en lista negra vigente.
     */
    @GetMapping("/vigentes")
    public ResponseEntity<List<BlacklistEntity>> obtenerVigentes() {
        List<BlacklistEntity> listaVigente = blackListService.obtenerPersonasEnListaNegraVigente();
        return ResponseEntity.ok(listaVigente);
    }

}
