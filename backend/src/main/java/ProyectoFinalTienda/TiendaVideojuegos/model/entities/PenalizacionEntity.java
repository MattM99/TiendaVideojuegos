package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    @Column
    private int idPenalizacion;

    @OneToOne
    @JoinColumn(name = "factura_id", nullable = false, unique = true)
    @NotNull(message = "La factura asociada no puede ser nula")
    private FacturaEntity factura;

    @Column(
            name = "fecha_emision_penalizacion",
            nullable = false
    )
    @NotNull(message = "La fecha de emisión de la penalización no puede ser nula")
    private LocalDateTime fechaEmisionAdicional;

    @Column(
            name = "monto_penalizacion_diaria",
            nullable = false
    )
    @Positive(message = "El monto diario de penalización debe ser mayor a cero")
    private double montoPenalizacionDiaria;

    @Column(
            name = "dias_retraso",
            nullable = false
    )
    @Min(value = 0, message = "Los días de retraso no pueden ser negativos")
    private int diasRetraso;

    @Column(
            name = "monto_total_penalizacion",
            nullable = false
    )
    @PositiveOrZero(message = "El monto total de penalización no puede ser negativo")
    private double montoTotalPenalizacion;

}
