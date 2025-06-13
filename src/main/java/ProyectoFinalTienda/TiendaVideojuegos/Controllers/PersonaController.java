package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaCreateOrReplaceRequest;
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

    @PostMapping
    public ResponseEntity<PersonaEntity> crearPersona(@Valid @RequestBody PersonaCreateOrReplaceRequest dto) {
        PersonaEntity persona = convertirDTOaEntidad(dto);
        PersonaEntity nueva = personaService.crearPersona(persona);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<PersonaEntity>> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(personaService.buscarPorNombre(nombre));
    }

    @GetMapping("/apellido/{apellido}")
    public ResponseEntity<List<PersonaEntity>> buscarPorApellido(@PathVariable String apellido) {
        return ResponseEntity.ok(personaService.buscarPorApellido(apellido));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PersonaEntity> buscarPorDni(@PathVariable String dni) {
        PersonaEntity persona = personaService.buscarPorDni(dni);
        return ResponseEntity.ok(persona);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PersonaEntity> buscarPorEmail(@PathVariable String email) {
        PersonaEntity persona = personaService.buscarPorEmail(email);
        return ResponseEntity.ok(persona);
    }

    @PatchMapping("/{email}")
    public ResponseEntity<PersonaEntity> actualizar(
            @PathVariable String email,
            @RequestBody PersonaEntity persona) {
        return ResponseEntity.ok(personaService.actualizar(email, persona));
    }


    @DeleteMapping("/eliminar/{dni}")
    public ResponseEntity<Void> eliminarPorDni(@PathVariable String dni) {
        personaService.eliminarPorDni(dni);
        return ResponseEntity.noContent().build();
    }

    private PersonaEntity convertirDTOaEntidad(PersonaCreateOrReplaceRequest dto) {
        return PersonaEntity.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .build();
    }

}
