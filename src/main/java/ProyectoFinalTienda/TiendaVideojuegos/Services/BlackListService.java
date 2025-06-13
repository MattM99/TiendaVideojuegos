package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlackListCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlacklistUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.BlackListResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.PersonaNoEncontradaException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.BlacklistMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PersonaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.BlacklistRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Service
public class BlackListService {

    @Autowired
    private BlacklistRepository blacklistRepository;
    @Autowired
    private BlacklistMapper blacklistMapper;
    @Autowired
    private PersonaService personaService;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private PersonaMapper personaMapper;

    public BlackListResponse crear(BlackListCreateOrReplaceRequest request) {
        if (blacklistRepository.findVigenteByPersona(request.getPersonaID()).isPresent()) {
            throw new BusinessException("La persona ya está en la lista negra.");
        }
        // Buscar la persona relacionada al ID
        PersonaEntity persona = personaRepository.findById(request.getPersonaID())
                        .orElseThrow(() -> new PersonaNoEncontradaException("Persona no encontrada con ID: " + request.getPersonaID()));

        // Convertir el DTO a entidad
        BlacklistEntity entity = request.toEntity(persona);
        blacklistRepository.save(entity);

        // Guardar en base de datos
        return blacklistMapper.toResponse(entity, personaMapper.convertirEntidadADTO(persona));
    }

    public BlackListResponse desbanear(int personaId) {
        Optional<BlacklistEntity> blackList = blacklistRepository.findVigenteByPersona(personaId);

        if (blackList.isPresent()) {
            BlacklistEntity entity = blackList.get();
            entity.setFecha_fin(LocalDate.now());

            PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
            BlacklistEntity updatedEntity = blacklistRepository.save(entity);

            return blacklistMapper.toResponse(updatedEntity, personaResponse);
        } else {
            throw new NoSuchElementException("La persona no está en lista negra.");
        }
    }

    public List<BlackListResponse> obtenerHistorico(){
        List<BlacklistEntity> listaNegra = blacklistRepository.findAll();
        if (listaNegra.isEmpty()) {
            throw new BusinessException("No se encontró ningún registro en la lista negra.");
        }
        return blacklistMapper.toResponseList(listaNegra);
    }

    public List<BlackListResponse> obtenerPersonasEnListaNegraVigente() {
        List<BlacklistEntity> lista = blacklistRepository.findPersonasEnListaNegraVigente();
        if (lista.isEmpty()) {
            throw new BusinessException("No se encontró ningún registro en la lista negra.");
        }
        return blacklistMapper.toResponseList(lista);
    }

    /**
     * Verifica si una persona está en la lista negra actualmente.
     * Lanza BusinessException si está en lista negra.
     */
    public void verificarNoEstaEnListaNegra(int personaId) {
        Optional<BlacklistEntity> blacklist = blacklistRepository.findVigenteByPersona(personaId);

        if (blacklist.isPresent()) {
            throw new BusinessException("La persona no puede alquilar porque está en lista negra. Motivo: "
                    + blacklist.get().getMotivo());
        }
    }

    public BlackListResponse actualizarParcialmente(int id, BlacklistUpdateRequest dto) {
        BlacklistEntity entity = blacklistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró entrada en lista negra con ID: " + id));

        blacklistMapper.actualizarEntity(entity, dto);
        blacklistRepository.save(entity);

        PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
        return blacklistMapper.toResponse(entity, personaResponse);
    }


}
