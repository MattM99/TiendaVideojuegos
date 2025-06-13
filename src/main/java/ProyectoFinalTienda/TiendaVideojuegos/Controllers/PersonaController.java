package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.services.PersonaService;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@Validated
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @PostMapping("/crear")
    public ResponseEntity<PersonaResponse> crearPersona(@Valid @RequestBody PersonaCreateOrReplaceRequest dto) {
        PersonaResponse nueva = personaService.crearPersona(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }


    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<PersonaResponse>> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(personaService.buscarPorNombre(nombre));
    }

    @GetMapping("/apellido/{apellido}")
    public ResponseEntity<List<PersonaResponse>> buscarPorApellido(@PathVariable String apellido) {
        return ResponseEntity.ok(personaService.buscarPorApellido(apellido));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PersonaResponse> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(personaService.buscarPorDni(dni));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PersonaResponse> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(personaService.buscarPorEmail(email));
    }

    @PatchMapping("/{email}")
    public ResponseEntity<PersonaResponse> actualizar(
            @PathVariable String email,
            @Valid @RequestBody PersonaCreateOrReplaceRequest dto) {
        return ResponseEntity.ok(personaService.actualizar(email, dto));
    }


    @DeleteMapping("/eliminar/{dni}")
    public ResponseEntity<Void> eliminarPorDni(@PathVariable String dni) {
        personaService.eliminarPorDni(dni);
        return ResponseEntity.noContent().build();
    }

}
