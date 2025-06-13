package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                .personaResponse(personaMapper.toResponse(entity.getPersona()))
                .build();
    }



}
