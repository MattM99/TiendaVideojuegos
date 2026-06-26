package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BloqueoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.BloqueoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.services.BloqueoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
@Validated
@Tag(name = "BlackList", description = "Control de lista negra de usuarios")
public class BloqueoController {

    @Autowired
    private BloqueoService bloqueoService;

    @Operation(summary = "Crear un nuevo registro en la lista negra", description = "Permite crear una nueva entrada en la lista negra")
    @PostMapping("/crear")
    public ResponseEntity<BloqueoResponse> crear(@RequestBody BloqueoCreateOrReplaceRequest request) {
        BloqueoResponse creada = bloqueoService.crear(request);
        return ResponseEntity.ok(creada);
    }

    @Operation(summary = "Desbanear a una persona", description = "Permite desbanear a una persona de la lista negra")
    @PutMapping("/desbanear/{dni}")
    public ResponseEntity<BloqueoResponse> desbanear(@PathVariable String dni) {
        BloqueoResponse actualizado = bloqueoService.desbanear(dni);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/historico")
    public ResponseEntity<Page<BloqueoResponse>> obtenerHistorico(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "5") int tamano,
            @RequestParam(defaultValue = "fechaInicio") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion
    ) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(
                        Sort.Direction.fromString(direccion),
                        ordenarPor
                )
        );
        return ResponseEntity.ok(
                bloqueoService.obtenerHistorico(pageable)
        );
    }

    @GetMapping("/vigentes")
    public ResponseEntity<Page<BloqueoResponse>> obtenerVigentes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "5") int tamano,
            @RequestParam(defaultValue = "fechaInicio") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion
    ) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamano,
                Sort.by(
                        Sort.Direction.fromString(direccion),
                        ordenarPor
                )
        );
        return ResponseEntity.ok(
                bloqueoService.obtenerPersonasEnListaNegraVigente(pageable)
        );
    }
}
