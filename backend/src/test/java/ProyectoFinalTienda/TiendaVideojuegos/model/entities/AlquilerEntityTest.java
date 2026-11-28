package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.OrigenDetalleAlquiler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class AlquilerEntityTest {

    @Test
    void interpretaUnDetalleSinOrigenRegistradoComoDesconocido() {
        DetalleAlquilerEntity detalleHistorico = DetalleAlquilerEntity.builder()
                .cantidad(2)
                .build();

        assertEquals(OrigenDetalleAlquiler.DESCONOCIDO, detalleHistorico.getOrigenEntrega());
        assertNull(detalleHistorico.getReservaConsumidaId());
    }

    @Test
    void generarPenalizacionPorRetrasoConDosDiasDeAtraso() {
        AlquilerEntity alquiler = AlquilerEntity.builder()
                .montoDiarioAlquiler(new BigDecimal("100"))
                .fechaFin(LocalDate.of(2026, 1, 10))
                .fechaDevolucion(LocalDate.of(2026, 1, 12))
                .build();

        alquiler.generarPenalizacionPorRetraso();

        assertEquals(1, alquiler.getPenalizaciones().size());

        PenalizacionEntity penalizacion = alquiler.getPenalizaciones().getFirst();

        assertAll(
                () -> assertEquals("Retraso en devolución", penalizacion.getMotivo()),
                () -> assertEquals(new BigDecimal("280.00"), penalizacion.getMonto()),
                () -> assertSame(alquiler, penalizacion.getAlquiler()),
                () -> assertEquals(1, alquiler.getPenalizaciones().size())
        );
    }

    @Test
    void calcularMontoDiarioSumaLosSubtotalesDeLosDetalles() {
        AlquilerEntity alquiler = AlquilerEntity.builder().build();

        DetalleAlquilerEntity primerDetalle = DetalleAlquilerEntity.builder()
                .subtotal(new BigDecimal("100"))
                .build();
        DetalleAlquilerEntity segundoDetalle = DetalleAlquilerEntity.builder()
                .subtotal(new BigDecimal("200"))
                .build();

        alquiler.agregarDetalle(primerDetalle);
        alquiler.agregarDetalle(segundoDetalle);

        alquiler.calcularMontoDiario();

        assertEquals(
                new BigDecimal("300"),
                alquiler.getMontoDiarioAlquiler()
        );
    }

    @Test
    void calcularCostoFijoIncluyeFechaDeInicioYFechaDeFin() {
        AlquilerEntity alquiler = AlquilerEntity.builder()
                .fechaInicio(LocalDate.of(2026, 11, 20))
                .fechaFin(LocalDate.of(2026, 11, 22))
                .montoDiarioAlquiler(new BigDecimal("300"))
                .build();

        BigDecimal costoFijo = alquiler.calcularCostoFijo();

        assertEquals(new BigDecimal("900"), costoFijo);
    }
}
