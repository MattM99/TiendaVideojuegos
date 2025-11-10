package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventarioMapper {

    @Autowired
    private VideojuegoMapper videojuegoMapper;

    public InventarioEntity toEntity(InventarioCreateOrReplaceRequest dto, VideojuegoEntity videojuego) {
        return InventarioEntity.builder()
                .videojuego(videojuego)
                .plataforma(dto.getPlataforma())
                .precioUnitarioDiario(dto.getPrecioUnitarioDiario())
                .stockTotal(dto.getStockTotal())
                .stockDisponible(dto.getStockDisponible())
                .stockAlquilado(dto.getStockAlquilado())
                .stockDescartado(dto.getStockDescartado())
                .build();
    }

    public InventarioResponse toResponse(InventarioEntity entity, VideojuegoResponse videojuegoResponse) {
        return InventarioResponse.builder()
                .inventarioId(entity.getInventario_id())
                .videojuego(videojuegoResponse)
                .plataforma(entity.getPlataforma())
                .precioUnitarioDiario(entity.getPrecioUnitarioDiario())
                .stockTotal(entity.getStockTotal())
                .stockDisponible(entity.getStockDisponible())
                .stockAlquilado(entity.getStockAlquilado())
                .stockDescartado(entity.getStockDescartado())
                .build();
    }


    public List<InventarioResponse> toResponseList(List<InventarioEntity> entities) {
        return entities.stream()
                .map(entity -> {
                    VideojuegoResponse videojuegoResponse = videojuegoMapper.toResponse(entity.getVideojuego());
                    return toResponse(entity, videojuegoResponse);
                })
                .collect(Collectors.toList());
    }

    public void actualizarEntity(InventarioEntity entity, InventarioUpdateRequest dto) {
        if (dto.getPrecioUnitarioDiario() != null && dto.getPrecioUnitarioDiario() > 0) entity.setPrecioUnitarioDiario(dto.getPrecioUnitarioDiario());
        if (dto.getStockTotal() != null && dto.getStockTotal() >= 0) entity.setStockTotal(dto.getStockTotal());
        if (dto.getStockDisponible() != null && dto.getStockDisponible() >= 0) entity.setStockDisponible(dto.getStockDisponible());
        if (dto.getStockAlquilado() != null && dto.getStockAlquilado() >= 0) entity.setStockAlquilado(dto.getStockAlquilado());
        if (dto.getStockDescartado() != null && dto.getStockDescartado() >= 0) entity.setStockDescartado(dto.getStockDescartado());
    }
}

