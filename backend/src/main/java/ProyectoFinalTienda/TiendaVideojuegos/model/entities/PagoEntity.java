package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "pago"
)

public class PagoEntity {
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
    private BigDecimal descuento;
}
