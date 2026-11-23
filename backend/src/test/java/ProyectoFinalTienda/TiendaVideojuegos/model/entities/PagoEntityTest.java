package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.MetodoPago;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PagoEntityTest {

    @Test
    void crearSinDescuentoNiPenalizaciones() {
        AlquilerEntity alquiler = crearAlquilerConCostoFijoMil();
        BigDecimal descuento = BigDecimal.ZERO;

        PagoEntity pago = PagoEntity.crear(
                alquiler,
                MetodoPago.EFECTIVO,
                descuento
        );

        verificarPago(
                pago,
                alquiler,
                MetodoPago.EFECTIVO,
                descuento,
                BigDecimal.ZERO,
                new BigDecimal("1000.00")
        );
    }

    @Test
    void crearConDescuentoDelDiezPorCiento() {
        AlquilerEntity alquiler = crearAlquilerConCostoFijoMil();
        BigDecimal descuento = new BigDecimal("0.10");

        PagoEntity pago = PagoEntity.crear(
                alquiler,
                MetodoPago.DEBITO,
                descuento
        );

        verificarPago(
                pago,
                alquiler,
                MetodoPago.DEBITO,
                descuento,
                BigDecimal.ZERO,
                new BigDecimal("900.00")
        );
    }

    @Test
    void crearConDosPenalizaciones() {
        AlquilerEntity alquiler = crearAlquilerConCostoFijoMil();
        agregarPenalizacion(alquiler, "Penalización 1", "100");
        agregarPenalizacion(alquiler, "Penalización 2", "200");
        BigDecimal descuento = BigDecimal.ZERO;

        PagoEntity pago = PagoEntity.crear(
                alquiler,
                MetodoPago.CREDITO,
                descuento
        );

        verificarPago(
                pago,
                alquiler,
                MetodoPago.CREDITO,
                descuento,
                new BigDecimal("300"),
                new BigDecimal("1300.00")
        );
    }

    @Test
    void crearConDescuentoYPenalizacionAplicaDescuentoSoloAlCostoFijo() {
        AlquilerEntity alquiler = crearAlquilerConCostoFijoMil();
        agregarPenalizacion(alquiler, "Penalización", "200");
        BigDecimal descuento = new BigDecimal("0.10");

        PagoEntity pago = PagoEntity.crear(
                alquiler,
                MetodoPago.TRANSFERENCIA,
                descuento
        );

        verificarPago(
                pago,
                alquiler,
                MetodoPago.TRANSFERENCIA,
                descuento,
                new BigDecimal("200"),
                new BigDecimal("1100.00")
        );
    }

    private AlquilerEntity crearAlquilerConCostoFijoMil() {
        return AlquilerEntity.builder()
                .fechaInicio(LocalDate.of(2026, 11, 20))
                .fechaFin(LocalDate.of(2026, 11, 21))
                .montoDiarioAlquiler(new BigDecimal("500"))
                .build();
    }

    private void agregarPenalizacion(
            AlquilerEntity alquiler,
            String motivo,
            String monto
    ) {
        PenalizacionEntity penalizacion = PenalizacionEntity.builder()
                .motivo(motivo)
                .monto(new BigDecimal(monto))
                .build();

        alquiler.agregarPenalizacion(penalizacion);
    }

    private void verificarPago(
            PagoEntity pago,
            AlquilerEntity alquiler,
            MetodoPago metodoPago,
            BigDecimal descuento,
            BigDecimal penalizacionTotal,
            BigDecimal montoFinal
    ) {
        assertAll(
                () -> assertEquals(metodoPago, pago.getMetodoPago()),
                () -> assertEquals(descuento, pago.getDescuento()),
                () -> assertEquals(
                        penalizacionTotal,
                        pago.getPenalizacionTotal()
                ),
                () -> assertEquals(montoFinal, pago.getMontoFinal()),
                () -> assertSame(alquiler, pago.getAlquiler()),
                () -> assertSame(pago, alquiler.getPago())
        );
    }
}
