package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "bloqueo"
)

public class BloqueoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "bloqueo_id"
    )
    private int bloqueoId;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    @NotNull(message = "Debe especificarse la persona afectada")
    private PersonaEntity persona;

    @Column(
            name = "fecha_inicio",
            nullable = false
    )
    @NotNull(message = "La fecha de inicio no puede ser nula")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    private LocalDate fechaInicio;

    @Column(
            name = "fecha_fin"
    )
    @FutureOrPresent(message = "La fecha de fin debe ser actual o futura")
    private LocalDate fechaFin;

    @Column(
            name = "motivo",
            nullable = false
    )
    @NotBlank(message = "Debe especificarse el motivo de la inclusión en la lista negra")
    private String motivo;
}
