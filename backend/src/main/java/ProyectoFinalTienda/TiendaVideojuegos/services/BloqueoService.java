package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BloqueoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlacklistUpdateRequest;
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
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Service
public class BloqueoService {

    @Autowired
    private BloqueoRepository bloqueoRepository;
    @Autowired
    private BloqueoMapper bloqueoMapper;
    @Autowired
    private PersonaService personaService;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private PersonaMapper personaMapper;

    public BloqueoResponse crear(BloqueoCreateOrReplaceRequest request) {
        if (bloqueoRepository.findVigenteByPersona(request.getPersonaID()).isPresent()) {
            throw new BusinessException("La persona ya está en la lista negra.");
        }
        // Buscar la persona relacionada al ID
        PersonaEntity persona = personaRepository.findById(request.getPersonaID())
                        .orElseThrow(() -> new PersonaNoEncontradaException("Persona no encontrada con ID: " + request.getPersonaID()));

        // Convertir el DTO a entidad
        BloqueoEntity entity = request.toEntity(persona);
        bloqueoRepository.save(entity);

        // Guardar en base de datos
        return bloqueoMapper.toResponse(entity, personaMapper.convertirEntidadADTO(persona));
    }

    public BloqueoResponse desbanear(int personaId) {
        Optional<BloqueoEntity> blackList = bloqueoRepository.findVigenteByPersona(personaId);

        if (blackList.isPresent()) {
            BloqueoEntity entity = blackList.get();
            entity.setFecha_fin(LocalDate.now());

            PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
            BloqueoEntity updatedEntity = bloqueoRepository.save(entity);

            return bloqueoMapper.toResponse(updatedEntity, personaResponse);
        } else {
            throw new NoSuchElementException("La persona no está en lista negra.");
        }
    }

    public List<BloqueoResponse> obtenerHistorico(){
        List<BloqueoEntity> listaNegra = bloqueoRepository.findAll();
        if (listaNegra.isEmpty()) {
            throw new BusinessException("No se encontró ningún registro en la lista negra.");
        }
        return bloqueoMapper.toResponseList(listaNegra);
    }

    public List<BloqueoResponse> obtenerPersonasEnListaNegraVigente() {
        List<BloqueoEntity> lista = bloqueoRepository.findPersonasEnListaNegraVigente();
        if (lista.isEmpty()) {
            throw new BusinessException("No se encontró ningún registro en la lista negra.");
        }
        return bloqueoMapper.toResponseList(lista);
    }

    /**
     * Verifica si una persona está en la lista negra actualmente.
     * Lanza BusinessException si está en lista negra.
     */
    public void verificarNoEstaEnListaNegra(int personaId) {
        Optional<BloqueoEntity> blacklist = bloqueoRepository.findVigenteByPersona(personaId);

        if (blacklist.isPresent()) {
            throw new BusinessException("La persona no puede alquilar porque está en lista negra. Motivo: "
                    + blacklist.get().getMotivo());
        }
    }

    public BloqueoResponse actualizarParcialmente(int id, BlacklistUpdateRequest dto) {
        BloqueoEntity entity = bloqueoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró entrada en lista negra con ID: " + id));

        bloqueoMapper.actualizarEntity(entity, dto);
        bloqueoRepository.save(entity);

        PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
        return bloqueoMapper.toResponse(entity, personaResponse);
    }


}
