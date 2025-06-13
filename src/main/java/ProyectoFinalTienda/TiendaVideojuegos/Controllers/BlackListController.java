package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlackListCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.BlackListResponse;
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
    @PostMapping("/crear")
    public ResponseEntity<BlackListResponse> crear(@RequestBody BlackListCreateOrReplaceRequest request) {
        BlackListResponse creada = blackListService.crear(request);
        return ResponseEntity.ok(creada);
    }
/*
    /**
     * Desbanear a una persona (cerrar su estado vigente en la blacklist).
     */
    @PutMapping("/desbanear/{personaId}")
    public ResponseEntity<BlackListResponse> desbanear(@PathVariable int personaId) {
        BlackListResponse actualizado = blackListService.desbanear(personaId);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Obtener el histórico completo de personas en la lista negra.
     */
    @GetMapping("/historico")
    public ResponseEntity<List<BlackListResponse>> obtenerHistorico() {
        List<BlackListResponse> lista = blackListService.obtenerHistorico();
        return ResponseEntity.ok(lista);
    }

    /**
     * Obtener personas actualmente en lista negra vigente.
     */
    @GetMapping("/vigentes")
    public ResponseEntity<List<BlackListResponse>> obtenerVigentes() {
        List<BlackListResponse> listaVigente = blackListService.obtenerPersonasEnListaNegraVigente();
        return ResponseEntity.ok(listaVigente);
    }
}
