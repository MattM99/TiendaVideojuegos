package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CarritoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.CarritoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CarritoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarritoMapper {

    @Autowired
    InventarioMapper inventarioMapper;
    @Autowired
    VideojuegoMapper videojuegoMapper;

    public CarritoEntity toEntity(CarritoCreateOrReplaceRequest request, AlquilerEntity alquiler, InventarioEntity inventario) {
        return CarritoEntity.builder()
                .alquiler(alquiler)
                .inventario(inventario)
                .build();
    }

    public CarritoResponse toResponse(CarritoEntity entity) {
        InventarioResponse inventarioResponse = inventarioMapper.toResponse(
                entity.getInventario(),
                videojuegoMapper.toResponse(entity.getInventario().getVideojuego())
        );

        return CarritoResponse.builder()
                .detalle_alquiler_id(entity.getDetalle_alquiler_id())
                .inventario(inventarioResponse)
                .subtotal(entity.getSubtotal())
                .build();
    }

    public List<CarritoResponse> toResponseList(List<CarritoEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
