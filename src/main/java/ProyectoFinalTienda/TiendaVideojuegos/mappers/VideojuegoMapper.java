package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideojuegoMapper {

    public VideojuegoEntity toEntityFromRequest(VideojuegoCreateOrReplaceRequest dto) {
        return VideojuegoEntity.builder()
                .titulo(dto.getTitulo())
                .desarrollador(dto.getDesarrollador())
                .genero(dto.getGenero())
                .lanzamiento(dto.getLanzamiento())
                .descripcion(dto.getDescripcion())
                .multijugador(dto.isMultijugador())
                .build();
    }

    public VideojuegoEntity toEntityFromResponse(VideojuegoResponse dto) {
        return VideojuegoEntity.builder()
                .titulo(dto.getTitulo())
                .desarrollador(dto.getDesarrollador())
                .genero(dto.getGenero())
                .lanzamiento(dto.getLanzamiento())
                .descripcion(dto.getDescripcion())
                .multijugador(dto.isMultijugador())
                .build();
    }

    public VideojuegoResponse toResponse(VideojuegoEntity entity) {
        return VideojuegoResponse.builder()
                .videojuegoID(entity.getVideojuegoID())
                .titulo(entity.getTitulo())
                .desarrollador(entity.getDesarrollador())
                .genero(entity.getGenero())
                .lanzamiento(entity.getLanzamiento())
                .descripcion(entity.getDescripcion())
                .multijugador(entity.isMultijugador())
                .build();
    }

    public List<VideojuegoResponse> toResponseList(List<VideojuegoEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void actualizarEntity(VideojuegoEntity entity, VideojuegoUpdateRequest dto) {
        if (dto.getTitulo() != null) entity.setTitulo(dto.getTitulo());
        if (dto.getDesarrollador() != null) entity.setDesarrollador(dto.getDesarrollador());
        if (dto.getGenero() != null) entity.setGenero(dto.getGenero());
        if (dto.getLanzamiento() != null) entity.setLanzamiento(dto.getLanzamiento());
        if (dto.getDescripcion() != null) entity.setDescripcion(dto.getDescripcion());
        if (dto.getMultijugador() != null) entity.setMultijugador(dto.getMultijugador());
    }
}
