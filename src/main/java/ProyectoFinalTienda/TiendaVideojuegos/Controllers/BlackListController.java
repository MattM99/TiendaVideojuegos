package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlackListCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.BlackListResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.BlackListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
@Validated
@Tag(name = "BlackList", description = "Control de lista negra de usuarios")
public class BlackListController {

    @Autowired
    private BlackListService blackListService;

    @Operation(summary = "Crear un nuevo registro en la lista negra", description = "Permite crear una nueva entrada en la lista negra")
    @PostMapping("/crear")
    public ResponseEntity<BlackListResponse> crear(@RequestBody BlackListCreateOrReplaceRequest request) {
        BlackListResponse creada = blackListService.crear(request);
        return ResponseEntity.ok(creada);
    }

    @Operation(summary = "Desbanear a una persona", description = "Permite desbanear a una persona de la lista negra")
    @PutMapping("/desbanear/{personaId}")
    public ResponseEntity<BlackListResponse> desbanear(@PathVariable int personaId) {
        BlackListResponse actualizado = blackListService.desbanear(personaId);
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Obtener histórico de lista negra", description = "Devuelve el histórico completo de personas en la lista negra")
    @GetMapping("/historico")
    public ResponseEntity<List<BlackListResponse>> obtenerHistorico() {
        List<BlackListResponse> lista = blackListService.obtenerHistorico();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener personas en lista negra vigente", description = "Devuelve las personas actualmente en la lista negra")
    @GetMapping("/vigentes")
    public ResponseEntity<List<BlackListResponse>> obtenerVigentes() {
        List<BlackListResponse> listaVigente = blackListService.obtenerPersonasEnListaNegraVigente();
        return ResponseEntity.ok(listaVigente);
    }
}
