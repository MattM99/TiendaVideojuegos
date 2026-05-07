package ProyectoFinalTienda.TiendaVideojuegos.model.entities;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.MetodosPago;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToOne
    @JoinColumn(name = "alquiler_id", nullable = false, unique = true)
    @NotNull(message = "El pago debe estar asociado a un alquiler")
    private AlquilerEntity alquiler;

    @Column(
            name = "metodo_pago",
            nullable = false
    )
    @NotNull(message = "El metodo de pago no puede ser nulo")
    @Enumerated(EnumType.STRING)
    private MetodosPago metodoPago;

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
    private BigDecimal penalizacionTotal; /// Pendiente: metodo de calcular penalizacion total

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoFinal;

    /// Constructor que usará internamente Lombok, privado para que no haya forma de instanciar con un costo total no calculado
    private PagoEntity(AlquilerEntity alquiler, MetodosPago metodoPago, BigDecimal descuento, BigDecimal penalizacionTotal, BigDecimal montoFinal) {
        this.alquiler = alquiler;
        this.metodoPago = metodoPago;
        this.descuento = descuento;
        this.penalizacionTotal = penalizacionTotal;
        this.montoFinal = montoFinal;
    }

    /// Factory Method
    public static PagoEntity crear(
            AlquilerEntity alquiler,
            MetodosPago metodoPago,
            BigDecimal descuento
    ) {
        BigDecimal precioBase = alquiler.calcularCostoFijo();
        BigDecimal descuentoAplicado = precioBase.multiply(descuento);
        BigDecimal penalizacionTotal = alquiler.getPenalizaciones().stream()
                .map(PenalizacionEntity::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = precioBase
                .subtract(descuentoAplicado)
                .add(penalizacionTotal)
                .setScale(2, RoundingMode.HALF_UP);

        PagoEntity pago = new PagoEntity(
                alquiler,
                metodoPago,
                descuento,
                penalizacionTotal,
                total
        );

        alquiler.asignarPago(pago);

        return pago;
    }

}
