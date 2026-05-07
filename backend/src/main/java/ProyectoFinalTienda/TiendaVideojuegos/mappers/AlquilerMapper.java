package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
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
    @Autowired
    private PagoMapper pagoMapper;
    @Autowired
    private PenalizacionMapper penalizacionMapper;


    public AlquilerEntity toEntity(AlquilerCreateOrReplaceRequest request, PersonaEntity persona) {
        return AlquilerEntity.builder()
                .persona(persona)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .build();
    }

    public AlquilerResponse toResponse(AlquilerEntity entity) {

        List<DetalleAlquilerResponse> detalles = entity.getItems()
                .stream()
                .map(detalleAlquilerMapper::toResponse)
                .toList();

        return AlquilerResponse.builder()
                .alquilerId(entity.getAlquilerId())

                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .fechaDevolucion(entity.getFechaDevolucion())

                .estadoAlquiler(entity.getEstadoAlquiler())

                .montoDiarioAlquiler(entity.getMontoDiarioAlquiler())

                .personaResponse(
                        personaMapper.convertirEntidadADTO(entity.getPersona())
                )

                .carrito(detalles)

                .penalizaciones(
                        penalizacionMapper.toResponseList(
                                entity.getPenalizaciones()
                        )
                )

                .pago(
                        pagoMapper.toResponse(entity.getPago())
                )

                .build();
    }

    public List<AlquilerResponse> toResponseList(
            List<AlquilerEntity> entities
    ) {

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

}
