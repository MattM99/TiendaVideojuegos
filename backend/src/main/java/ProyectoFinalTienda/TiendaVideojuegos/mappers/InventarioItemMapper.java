package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioItemResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventarioItemMapper {

    @Autowired
    private VideojuegoMapper videojuegoMapper;

    public InventarioItemEntity toEntity(InventarioItemCreateOrReplaceRequest dto, VideojuegoEntity videojuego) {
        return InventarioItemEntity.builder()
                .videojuego(videojuego)
                .plataforma(dto.getPlataforma())
                .precioDiario(dto.getPrecioDiario())
                .stockTotal(dto.getStockTotal())
                .stockDisponible(dto.getStockDisponible())
                .build();
    }

    public InventarioItemResponse toResponse(InventarioItemEntity entity, VideojuegoResponse videojuegoResponse) {
        return InventarioItemResponse.builder()
                .inventarioId(entity.getInventarioItemId())
                .videojuego(videojuegoResponse)
                .plataforma(entity.getPlataforma())
                .precioDiario(entity.getPrecioDiario())
                .stockTotal(entity.getStockTotal())
                .stockDisponible(entity.getStockDisponible())
                .build();
    }


    public List<InventarioItemResponse> toResponseList(List<InventarioItemEntity> entities) {
        return entities.stream()
                .map(entity -> {
                    VideojuegoResponse videojuegoResponse = videojuegoMapper.toResponse(entity.getVideojuego());
                    return toResponse(entity, videojuegoResponse);
                })
                .collect(Collectors.toList());
    }

    public void actualizarEntity(InventarioItemEntity entity, InventarioItemUpdateRequest dto) {
        if (dto.getPrecioDiario() != null && dto.getPrecioDiario() > 0) entity.setPrecioDiario(dto.getPrecioDiario());
        if (dto.getStockTotal() != null && dto.getStockTotal() >= 0) entity.setStockTotal(dto.getStockTotal());
        if (dto.getStockDisponible() != null && dto.getStockDisponible() >= 0) entity.setStockDisponible(dto.getStockDisponible());
    }
}

