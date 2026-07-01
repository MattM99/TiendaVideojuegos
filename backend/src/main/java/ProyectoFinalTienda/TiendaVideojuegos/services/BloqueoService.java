package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BloqueoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BloqueoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.BloqueoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.PersonaNoEncontradaException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.BloqueoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PersonaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BloqueoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.BloqueoRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BloqueoService {

    @Autowired
    private BloqueoRepository bloqueoRepository;
    @Autowired
    private BloqueoMapper bloqueoMapper;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private PersonaMapper personaMapper;

    /// Create

    public BloqueoResponse crear(BloqueoCreateOrReplaceRequest request) {

        PersonaEntity persona =
                obtenerPersonaParaBloqueo(request.getPersonaDni());

        BloqueoEntity entity =
                bloqueoMapper.toEntity(request, persona);

        bloqueoRepository.save(entity);

        return bloqueoMapper.toResponse(
                entity,
                personaMapper.convertirEntidadADTO(persona)
        );

    }

    /// Read

    public Page<BloqueoResponse> obtenerHistorico(Pageable pageable) {
        return bloqueoRepository
                .findAll(pageable)
                .map(entity -> {
                    PersonaResponse persona =
                            personaMapper.convertirEntidadADTO(entity.getPersona());
                    return bloqueoMapper.toResponse(entity, persona);
                });
    }

    public Page<BloqueoResponse> obtenerPersonasEnListaNegraVigente(Pageable pageable) {
        return bloqueoRepository
                .findPersonasEnListaNegraVigente(pageable)
                .map(entity -> {
                    PersonaResponse persona =
                            personaMapper.convertirEntidadADTO(entity.getPersona());
                    return bloqueoMapper.toResponse(entity, persona);
                });
    }

    public List<PersonaResponse> obtenerPersonasDisponiblesParaBloqueo() {

        List<PersonaEntity> personas = personaRepository.findAll();

        List<BloqueoEntity> bloqueosVigentes =
                bloqueoRepository.findPersonasEnListaNegraVigente();

        Set<String> dnisBloqueados = bloqueosVigentes.stream()
                .map(b -> b.getPersona().getDni())
                .collect(Collectors.toSet());

        return personas.stream()
                .filter(p -> !dnisBloqueados.contains(p.getDni()))
                .map(personaMapper::convertirEntidadADTO)
                .toList();
    }

    /// Update

    public BloqueoResponse actualizarParcialmente(int id, BloqueoUpdateRequest dto) {
        BloqueoEntity entity = bloqueoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró entrada en lista negra con ID: " + id));

        bloqueoMapper.actualizarEntity(entity, dto);
        bloqueoRepository.save(entity);

        PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
        return bloqueoMapper.toResponse(entity, personaResponse);
    }

    /// Delete

    public BloqueoResponse desbanear(String dni) {

        BloqueoEntity entity = buscarBloqueoVigente(dni)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "La persona no está en lista negra."));

        entity.setFechaFin(LocalDate.now());

        bloqueoRepository.save(entity);

        return bloqueoMapper.toResponse(
                entity,
                personaMapper.convertirEntidadADTO(entity.getPersona())
        );
    }

    /// Validaciones

    public PersonaResponse validarPersonaParaBloqueo(String dni) {

        return personaMapper.convertirEntidadADTO(
                obtenerPersonaParaBloqueo(dni)
        );

    }

    private PersonaEntity obtenerPersonaParaBloqueo(String dni) {

        PersonaEntity persona = personaRepository.findByDni(dni)
                .orElseThrow(() ->
                        new PersonaNoEncontradaException(
                                "Persona no encontrada."
                        ));

        verificarNoEstaEnListaNegra(dni);

        return persona;
    }

    public void verificarNoEstaEnListaNegra(String dni) {

        Optional<BloqueoEntity> bloqueo = buscarBloqueoVigente(dni);

        if (bloqueo.isPresent()) {
            throw new BusinessException(
                    "La persona está en lista negra. Motivo: "
                            + bloqueo.get().getMotivo());
        }
    }

    private Optional<BloqueoEntity> buscarBloqueoVigente(String dni) {
        return bloqueoRepository.findVigenteByPersona(dni);
    }

}
