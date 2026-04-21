package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
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
        name = "detalle_alquiler"
)

public class DetalleAlquilerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "detalle_alquiler_id"
    )
    private int detalleAlquilerId;

    @ManyToOne
    @JoinColumn(name = "alquiler_id", nullable = false)
    @NotNull(message = "El alquiler no puede ser nulo")
    private AlquilerEntity alquiler;

    @ManyToOne
    @JoinColumn(name = "inventario_item_id", nullable = false)
    @NotNull(message = "El inventario no puede ser nulo")
    private InventarioItemEntity inventarioItem;

    @Column(
            name = "subtotal",
            nullable = false
    )
    @PositiveOrZero(message = "El subtotal no puede ser negativo")
    private double subtotal;

    public void calcularSubtotal() {
        if (alquiler == null || inventarioItem == null) {
            throw new IllegalStateException("Faltan datos para calcular el subtotal");
        }
        this.subtotal = inventarioItem.getPrecioDiario() * alquiler.calcularDiasAlquiler();
    }
}
