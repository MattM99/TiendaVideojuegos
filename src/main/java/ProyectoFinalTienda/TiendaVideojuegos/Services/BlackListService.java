package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlackListCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.BlacklistRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BlackListService {

    @Autowired
    private BlacklistRepository blacklistRepository;
    @Autowired
    private PersonaService personaService;
    @Autowired
    private PersonaRepository personaRepository;

    public BlacklistEntity crear(BlackListCreateOrReplaceRequest request) {
        // Buscar la persona relacionada al ID
        PersonaEntity persona = personaRepository.findById(request.getPersonaID()).orElseThrow();

        // Convertir el DTO a entidad
        BlacklistEntity entity = request.toEntity(persona);

        // Guardar en base de datos
        return blacklistRepository.save(entity);
    }

    public BlacklistEntity desbanear(int personaId) {
        Optional<BlacklistEntity> blackList = blacklistRepository.findVigenteByPersona(personaId);

        if (blackList.isPresent()) {
            BlacklistEntity entity = blackList.get();
            entity.setFecha_fin(LocalDate.now());
            return blacklistRepository.save(entity);
        } else {
            throw new BusinessException("La persona no está en lista negra.");
        }
    }

    public List<BlacklistEntity> obtenerHistorico(){
        List<BlacklistEntity> listaNegra = blacklistRepository.findAll();
        if (listaNegra.isEmpty()) {
            throw new BusinessException("No se encontró ningún registro en la lista negra.");
        }
        return listaNegra;
    }

    public List<BlacklistEntity> obtenerPersonasEnListaNegraVigente() {
        return blacklistRepository.findPersonasEnListaNegraVigente();
    }

    /**
     * Verifica si una persona está en la lista negra actualmente.
     * Lanza BusinessException si está en lista negra.
     */
    public void verificarNoEstaEnListaNegra(int personaId) {
        Optional<BlacklistEntity> blackList = blacklistRepository.findVigenteByPersona(personaId);

        if (blackList.isPresent()) {
            throw new BusinessException("La persona no puede alquilar porque está en lista negra. Motivo: "
                    + blackList.get().getMotivo());
        }
    }

}
