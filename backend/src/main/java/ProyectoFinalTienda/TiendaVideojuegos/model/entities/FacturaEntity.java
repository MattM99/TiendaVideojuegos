package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    @NotNull(message = "El alquiler no puede ser nulo")
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
    @NotBlank(message = "La fecha de emisión es obligatoria")
    private String fecha_emision;

    @Column(
            name = "monto_alquileres",
            nullable = false
    )
    @PositiveOrZero(message = "El monto de alquileres no puede ser negativo")
    private double monto_alquileres;

    @Column(
            name = "descuento",
            nullable = false
    )
    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private double descuento;

    @Column(
            name = "total",
            nullable = false
    )
    @PositiveOrZero(message = "El total no puede ser negativo")
    private double total;
}
