package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetalleAlquilerMapper {

    @Autowired
    InventarioMapper inventarioMapper;
    @Autowired
    VideojuegoMapper videojuegoMapper;

    public DetalleAlquilerEntity toEntity(DetalleAlquilerCreateOrReplaceRequest request, AlquilerEntity alquiler, InventarioEntity inventario) {
        return DetalleAlquilerEntity.builder()
                .alquiler(alquiler)
                .inventario(inventario)
                .build();
    }

    public DetalleAlquilerResponse toResponse(DetalleAlquilerEntity entity) {
        InventarioResponse inventarioResponse = inventarioMapper.toResponse(
                entity.getInventario(),
                videojuegoMapper.toResponse(entity.getInventario().getVideojuego())
        );

        return DetalleAlquilerResponse.builder()
                .detalle_alquiler_id(entity.getDetalle_alquiler_id())
                .inventario(inventarioResponse)
                .subtotal(entity.getSubtotal())
                .build();
    }

    public DetalleAlquilerResponse toShallowResponse(DetalleAlquilerEntity entity) {
        return DetalleAlquilerResponse.builder()
               .detalle_alquiler_id(entity.getDetalle_alquiler_id())
                .subtotal(entity.getSubtotal())
                .build();
    }

}
