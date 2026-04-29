package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlquilerMapper {
    @Autowired
    DetalleAlquilerMapper detalleAlquilerMapper;
    @Autowired
    PersonaMapper personaMapper;


    public AlquilerEntity toEntity(AlquilerCreateOrReplaceRequest request, PersonaEntity persona) {
        return AlquilerEntity.builder()
                .persona(persona)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();
    }

    public AlquilerResponse toResponse(AlquilerEntity entity) {
        return AlquilerResponse.builder()
                .alquilerId(entity.getAlquilerId())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .personaResponse(personaMapper.convertirEntidadADTO(entity.getPersona()))
                .build();
    }

    public List<AlquilerResponse> toResponseList(List<AlquilerEntity> entities) {
        return entities.stream()
                .map(entity -> {
                    PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
                    return toResponse(entity);
                })
                .collect(Collectors.toList());
    }

}
