package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@Table(
        name = "pago"
)

public class PagoEntity {

    private PagoEntity(int pagoId, EstadoPago estadoPago, BigDecimal descuento, BigDecimal penalizacionTotal, BigDecimal costoTotal) {
        this.pagoId = pagoId;
        this.estadoPago = estadoPago;
        this.descuento = descuento;
        this.penalizacionTotal = penalizacionTotal;
        this.costoTotal = costoTotal;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "pago_id",
            nullable = false
    )
    private int pagoId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado_pago",
            nullable = false
    )
    @NotNull(message = "El estado actual del pago es obligatorio")
    private EstadoPago estadoPago;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    @Digits(integer = 1, fraction = 4)
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal descuento;

    @NotNull
    @DecimalMin("0.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal penalizacionTotal;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    @Setter(AccessLevel.NONE)
    private BigDecimal costoTotal;

    public static PagoEntity crear(
            EstadoPago estadoPago,
            BigDecimal descuento,
            BigDecimal penalizacionTotal,
            BigDecimal precioBase
    ) {
        BigDecimal descuentoAplicado = precioBase.multiply(descuento);

        BigDecimal total = precioBase
                .subtract(descuentoAplicado)
                .add(penalizacionTotal)
                .setScale(2, RoundingMode.HALF_UP);

        return PagoEntity.builder()
                .estadoPago(estadoPago)
                .descuento(descuento)
                .penalizacionTotal(penalizacionTotal)
                .costoTotal(total)
                .build();
    }

}
