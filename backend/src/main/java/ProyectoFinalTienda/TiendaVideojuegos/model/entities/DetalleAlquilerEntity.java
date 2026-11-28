package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.OrigenDetalleAlquiler;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_entrega")
    @Setter(AccessLevel.NONE)
    private OrigenDetalleAlquiler origenEntrega;

    @Column(name = "reserva_consumida_id")
    @Setter(AccessLevel.NONE)
    private Integer reservaConsumidaId;

    @Column(
            name = "subtotal",
            nullable = false
    )
    @PositiveOrZero(message = "El subtotal no puede ser negativo")
    private BigDecimal subtotal;

    public OrigenDetalleAlquiler getOrigenEntrega() {
        return origenEntrega == null ? OrigenDetalleAlquiler.DESCONOCIDO : origenEntrega;
    }

    public void registrarOrigenEntrega(Integer idReservaConsumida) {
        if (origenEntrega != null) {
            throw new IllegalStateException("El origen de la entrega ya está registrado.");
        }
        if (idReservaConsumida != null && idReservaConsumida <= 0) {
            throw new IllegalStateException("La reserva consumida no tiene un identificador válido.");
        }

        this.reservaConsumidaId = idReservaConsumida;
        this.origenEntrega = idReservaConsumida == null
                ? OrigenDetalleAlquiler.STOCK_LIBRE
                : OrigenDetalleAlquiler.RESERVA;
    }

    public void calcularSubtotal() {
        if (alquiler == null || inventarioItem == null) {
            throw new IllegalStateException("Faltan datos para calcular el subtotal");
        }
        this.subtotal = inventarioItem.getPrecioDiario().multiply(BigDecimal.valueOf(this.getCantidad()));
    }
}
