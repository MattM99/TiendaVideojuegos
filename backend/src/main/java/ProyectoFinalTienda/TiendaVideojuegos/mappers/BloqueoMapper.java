package ProyectoFinalTienda.TiendaVideojuegos.mappers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BloqueoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.BlacklistUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.BloqueoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BloqueoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.services.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BloqueoMapper {
        @Autowired
        private PersonaMapper personaMapper;
        @Autowired
        private PersonaService personaService;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        public BloqueoEntity toEntity(BloqueoCreateOrReplaceRequest dto) {
            return BloqueoEntity.builder()
                    .persona(personaMapper.toEntity(personaService.buscarPorId(dto.getPersonaID())))
                    .fecha_inicio(dto.getFecha_inicio())
                    .fecha_fin(dto.getFecha_fin())
                    .motivo(dto.getMotivo())
                    .build();
        }

        public BloqueoResponse toResponse(BloqueoEntity entity, PersonaResponse personaResponse) {


            return BloqueoResponse.builder()
                    .blacklist_id(entity.getBlacklist_id())
                    .personaResponse(personaResponse)
                    .fecha_inicio(entity.getFecha_inicio().format(formatter))
                    .fecha_fin(fechaFinString(entity.getFecha_fin()))
                    .motivo(entity.getMotivo())
                    .build();
        }

        public String fechaFinString(LocalDate fecha) {
            return (fecha != null)
                    ? fecha.format(formatter)
                    : "Indefinido";
        }


        public List<BloqueoResponse> toResponseList(List<BloqueoEntity> entities) {
            return entities.stream()
                    .map(entity -> {
                        PersonaResponse personaResponse = personaMapper.convertirEntidadADTO(entity.getPersona());
                        return toResponse(entity, personaResponse);
                    })
                    .collect(Collectors.toList());
        }

        public void actualizarEntity(BloqueoEntity entity, BlacklistUpdateRequest dto) {
            if (dto.getPersona() != null) entity.setPersona(personaMapper.toEntity(dto.getPersona()));

            if (dto.getFecha_inicio() != null) {
                LocalDate inicio = LocalDate.parse(dto.getFecha_inicio(), formatter);
                if (inicio.isBefore(LocalDate.now())) {
                    entity.setFecha_inicio(inicio);
                }
            }
            if (dto.getFecha_fin() != null) {
                LocalDate fin = LocalDate.parse(dto.getFecha_fin(), formatter);
                if (fin.isAfter(LocalDate.now())) {
                    entity.setFecha_fin(fin);
                }
            }

            if (dto.getMotivo() != null) entity.setMotivo(dto.getMotivo());
        }
    }
