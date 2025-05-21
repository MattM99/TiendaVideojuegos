package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
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
    private FacturaEntity factura;

    @Column(
            name = "fecha_emision_penalizacion",
            nullable = false
    )
    private LocalDateTime fechaEmisionAdicional;

    @Column(
            name = "monto_penalizacion_diaria",
            nullable = false
    )
    private double montoPenalizacionDiaria;

    @Column(
            name = "dias_retraso",
            nullable = false
    )
    private int diasRetraso;

    @Column(
            name = "monto_total_penalizacion",
            nullable = false
    )
    private double montoTotalPenalizacion;

}
