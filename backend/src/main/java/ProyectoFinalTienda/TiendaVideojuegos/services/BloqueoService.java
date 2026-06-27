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

        String dni = request.getPersonaDni();

        if (dni == null) {
            throw new BusinessException("DNI no puede ser null");
        }

        if (bloqueoRepository.findVigenteByPersona(dni).isPresent()) {
            throw new BusinessException("La persona ya está en la lista negra.");
        }

        PersonaEntity persona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "Persona no encontrada con DNI: " + dni));

        BloqueoEntity entity = bloqueoMapper.toEntity(request, persona);

        bloqueoRepository.save(entity);

        return bloqueoMapper.toResponse(entity,
                personaMapper.convertirEntidadADTO(persona));
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

    /// Update

    public BloqueoResponse actualizarParcialmente(int id, BloqueoUpdateRequest dto) {
        BloqueoEntity entity = bloqueoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró entrada en lista negra con ID: " + id));

        bloqueoMapper.actualizarEntity(entity, dto);
        bloqueoRepository.save(entity);

        PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
        return bloqueoMapper.toResponse(entity, personaResponse);
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

    /// Delete

    public BloqueoResponse desbanear(String dni) {
        Optional<BloqueoEntity> blackList = bloqueoRepository.findVigenteByPersona(dni);

        if (blackList.isPresent()) {
            BloqueoEntity entity = blackList.get();
            entity.setFechaFin(LocalDate.now());

            PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
            BloqueoEntity updatedEntity = bloqueoRepository.save(entity);

            return bloqueoMapper.toResponse(updatedEntity, personaResponse);
        } else {
            throw new NoSuchElementException("La persona no está en lista negra.");
        }
    }

    /**
     * Verifica si una persona está en la lista negra actualmente.
     * Lanza BusinessException si está en lista negra.
     */
    public void verificarNoEstaEnListaNegra(String dni) {
        Optional<BloqueoEntity> blacklist = bloqueoRepository.findVigenteByPersona(dni);

        if (blacklist.isPresent()) {
            throw new BusinessException("La persona está en lista negra. Motivo: "
                    + blacklist.get().getMotivo());
        }
    }

}
