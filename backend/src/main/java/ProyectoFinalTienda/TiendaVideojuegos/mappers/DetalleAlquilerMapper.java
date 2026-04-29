package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioItemResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DetalleAlquilerMapper {

    @Autowired
    InventarioItemMapper inventarioItemMapper;
    @Autowired
    VideojuegoMapper videojuegoMapper;

    public DetalleAlquilerEntity toEntity(DetalleAlquilerRequest request, AlquilerEntity alquiler, InventarioItemEntity inventario) {
        return DetalleAlquilerEntity.builder()
                .alquiler(alquiler)
                .inventarioItem(inventario)
                .build();
    }

    public DetalleAlquilerResponse toResponse(DetalleAlquilerEntity entity) {
        InventarioItemResponse inventarioItemResponse = inventarioItemMapper.toResponse(
                entity.getInventarioItem(),
                videojuegoMapper.toResponse(entity.getInventarioItem().getVideojuego())
        );

        return DetalleAlquilerResponse.builder()
                .detalleAlquilerId(entity.getDetalleAlquilerId())
                .inventario(inventarioItemResponse)
                .subtotal(entity.getSubtotal())
                .build();
    }

    public List<DetalleAlquilerResponse> toResponseList(List<DetalleAlquilerEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
