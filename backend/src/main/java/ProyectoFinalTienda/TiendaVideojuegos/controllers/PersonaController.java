package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaPatchRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.services.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/personas")
@Validated
@Tag(name = "Personas", description = "Gestión de datos personales de usuarios")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Operation(summary = "Crear una nueva persona", description = "Registra una nueva persona en el sistema")
    @PostMapping("/crear")
    public ResponseEntity<PersonaResponse> crearPersona(@Valid @RequestBody PersonaCreateOrReplaceRequest dto) {
        PersonaResponse nueva = personaService.crearPersona(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @Operation(summary = "Listar personas", description = "Lista a todas las personas registradas")
    @GetMapping("/listar")
    public ResponseEntity<Page<PersonaResponse>> listarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "apellido") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);


        return ResponseEntity.ok(
                personaService.listarTodos(paginacion)
        );
    }

    @Operation(summary = "Buscar personas por nombre", description = "Devuelve una lista de personas que coincidan con el nombre proporcionado")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Page<PersonaResponse>> buscarPorNombre(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "apellido") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<PersonaResponse> responses = personaService.buscarPorNombre(nombre, paginacion);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar personas por apellido", description = "Devuelve una lista de personas que coincidan con el apellido proporcionado")
    @GetMapping("/apellido/{apellido}")
    public ResponseEntity<Page<PersonaResponse>> buscarPorApellido(
            @PathVariable String apellido,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "apellido") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion
    ) {
        Sort sort = direccion.equalsIgnoreCase("desc")
                ? Sort.by(ordenarPor).descending()
                : Sort.by(ordenarPor).ascending();

        Pageable paginacion = PageRequest.of(pagina, tamano, sort);

        Page<PersonaResponse> responses = personaService.buscarPorApellido(apellido, paginacion);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Buscar persona por DNI", description = "Devuelve una persona que coincida con el número de DNI especificado")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<PersonaResponse> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(personaService.buscarPorDni(dni));
    }

    @Operation(summary = "Buscar persona por email", description = "Devuelve una persona que coincida con el correo electrónico especificado")
    @GetMapping("/email/{email}")
    public ResponseEntity<PersonaResponse> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(personaService.buscarPorEmail(email));
    }

    @Operation(summary = "Actualizar persona por email", description = "Permite actualizar parcialmente los datos de una persona usando su email")
    @PatchMapping("/{email}")
    public ResponseEntity<PersonaResponse> actualizar(
            @PathVariable String email,
            @RequestBody PersonaPatchRequest dto) {
        return ResponseEntity.ok(personaService.actualizar(email, dto));
    }

    @Operation(summary = "Eliminar persona por DNI", description = "Elimina a una persona del sistema usando su número de DNI")
    @DeleteMapping("/eliminar/{dni}")
    public ResponseEntity<Void> eliminarPorDni(@PathVariable String dni) {
        personaService.eliminarPorDni(dni);
        return ResponseEntity.noContent().build();
    }

}
