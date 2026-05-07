package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "penalizacion"
)
public class PenalizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "penalizacion_id")
    private int penalizacionId;

    @ManyToOne
    @JoinColumn(name = "alquiler_id", nullable = false)
    @NotNull(message = "El alquiler al que pertenece la penalizacion no puede ser nulo")
    private AlquilerEntity alquiler;

    @Column(nullable = false)
    @Positive(message = "El monto debe ser mayor a cero")
    private double monto;

    @Column(nullable = false)
    @NotBlank(message = "El motivo no puede estar vacío")
    private String motivo;
}
