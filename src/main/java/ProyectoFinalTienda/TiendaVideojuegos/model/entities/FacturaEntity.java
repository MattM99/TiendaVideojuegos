package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "factura"
)

public class FacturaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "factura_id"
    )
    private int factura_id;

    @ManyToOne
    @JoinColumn(name = "alquiler_id", nullable = false)
    private AlquilerEntity alquiler;

    @OneToOne(
            mappedBy = "factura",
            cascade = CascadeType.ALL
    )
    private PenalizacionEntity penalizacion;


    @Column(
            name = "fecha_emision",
            nullable = false
    )
    private String fecha_emision;

    @Column(
            name = "monto_alquileres",
            nullable = false
    )
    private double monto_alquileres;

    @Column(
            name = "descuento",
            nullable = false
    )
    private double descuento;

    @Column(
            name = "total",
            nullable = false
    )
    private double total;
}
