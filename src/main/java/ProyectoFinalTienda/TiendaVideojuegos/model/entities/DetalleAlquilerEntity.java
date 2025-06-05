package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "detalle_alquiler"
)

public class DetalleAlquilerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "detalle_alquiler_id"
    )
    private int detalle_alquiler_id;

    @ManyToOne
    @JoinColumn(name = "alquiler_id", nullable = false)
    private AlquilerEntity alquiler;

    @ManyToOne
    @JoinColumn(name = "inventario_id", nullable = false)
    private InventarioEntity inventario;

    @Column(
            name = "subtotal",
            nullable = false
    )
    private double subtotal;

    public void calcularSubtotal() {
        if (alquiler == null || inventario == null) {
            throw new IllegalStateException("Faltan datos para calcular el subtotal");
        }
        this.subtotal = inventario.getPrecioUnitarioDiario() * alquiler.calcularDiasAlquiler();
    }
}
