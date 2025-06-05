package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackListCreateOrReplaceRequest {

    @NotNull(message = "El ID de la persona es obligatorio")
    private Integer personaID;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fecha_inicio;

    private LocalDate fecha_fin; // Puede ser null para indicar bloqueo indefinido

    @NotNull(message = "El motivo es obligatorio")
    private String motivo;

    public BlacklistEntity toEntity(PersonaEntity persona) {
        return BlacklistEntity.builder()
                .persona(persona)
                .fecha_inicio(this.fecha_inicio)
                .fecha_fin(this.fecha_fin)
                .motivo(this.motivo)
                .build();
    }

}
