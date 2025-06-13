package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
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
                .fecha_retiro(request.getFecha_retiro())
                .fecha_devolucion(request.getFecha_devolucion())
                .build();
    }

    public AlquilerResponse toResponse(AlquilerEntity entity) {
        return AlquilerResponse.builder()
                .alquiler_id(entity.getAlquiler_id())
                .fecha_retiro(entity.getFecha_retiro())
                .fecha_devolucion(entity.getFecha_devolucion())
                .personaResponse(personaMapper.convertirEntidadADTO(entity.getPersona()))
                .detalles(detalleAlquilerMapper.toResponseList(entity.getDetalles()))
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
