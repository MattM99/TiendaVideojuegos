package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @NotNull(message = "El alquiler al que pertenece no puede ser nulo")
    private AlquilerEntity alquiler;

    @ManyToOne
    @JoinColumn(name = "inventario_item_id", nullable = false)
    @NotNull(message = "El item contenido no puede ser nulo")
    private InventarioItemEntity inventarioItem;

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private int cantidad;

    @Column(
            name = "subtotal",
            nullable = false
    )
    @PositiveOrZero(message = "El subtotal no puede ser negativo")
    private BigDecimal subtotal;

    public void calcularSubtotal() {
        if (alquiler == null || inventarioItem == null) {
            throw new IllegalStateException("Faltan datos para calcular el subtotal");
        }
        this.subtotal = inventarioItem.getPrecioDiario().multiply(BigDecimal.valueOf(this.getCantidad()));
    }
}
