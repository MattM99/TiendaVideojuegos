package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {

    public PersonaEntity convertirDTOaEntidad(PersonaCreateOrReplaceRequest dto) {
        return PersonaEntity.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .build();
    }
    public PersonaEntity toEntity(PersonaResponse dto) {
        return PersonaEntity.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .build();
    }


    public PersonaResponse convertirEntidadADTO(PersonaEntity entity) {
        return PersonaResponse.builder()
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .dni(entity.getDni())
                .email(entity.getEmail())
                .telefono(entity.getTelefono())
                .build();
    }



}
