package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CerrarAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.MetodoPago;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlquilerServiceTest {

    @Mock
    private AlquilerRepository alquilerRepository;

    @Mock
    private InventarioItemService inventarioItemService;

    @Mock
    private AlquilerMapper alquilerMapper;

    @InjectMocks
    private AlquilerService alquilerService;

    @Test
    void cerrarAlquilerFinalizadoDebeFallarSinModificarlo() {
        int alquilerId = 1;
        AlquilerEntity alquiler = AlquilerEntity.builder()
                .alquilerId(alquilerId)
                .activo(true)
                .estadoAlquiler(EstadoAlquiler.FINALIZADO)
                .build();
        CerrarAlquilerRequest request = crearSolicitudDeCierreValida();

        when(alquilerRepository.buscarPorIdParaActualizar(alquilerId))
                .thenReturn(Optional.of(alquiler));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> alquilerService.cerrarAlquiler(alquilerId, request)
        );

        assertAll(
                () -> assertEquals(
                        "Solo se pueden cerrar alquileres en curso.",
                        exception.getMessage()
                ),
                () -> assertEquals(
                        EstadoAlquiler.FINALIZADO,
                        alquiler.getEstadoAlquiler()
                ),
                () -> assertNull(alquiler.getFechaDevolucion()),
                () -> assertNull(alquiler.getPago()),
                () -> assertEquals(0, alquiler.getPenalizaciones().size())
        );

        verify(alquilerRepository, never()).save(any(AlquilerEntity.class));
        verify(inventarioItemService, never())
                .devolverVideojuego(anyInt(), anyInt());
    }

    @Test
    void cerrarAlquilerBloqueaElAlquilerAntesDeDevolverLasUnidades() {
        int alquilerId = 1;
        LocalDate hoy = LocalDate.now();
        AlquilerEntity alquiler = AlquilerEntity.builder()
                .alquilerId(alquilerId)
                .estadoAlquiler(EstadoAlquiler.EN_CURSO)
                .fechaInicio(hoy.minusDays(1))
                .fechaFin(hoy.plusDays(1))
                .montoDiarioAlquiler(BigDecimal.TEN)
                .build();
        var primerDetalle = mock(AlquilerRepository.UnidadesParaDevolver.class);
        var segundoDetalle = mock(AlquilerRepository.UnidadesParaDevolver.class);
        when(primerDetalle.getInventarioItemId()).thenReturn(3);
        when(primerDetalle.getCantidad()).thenReturn(2);
        when(segundoDetalle.getInventarioItemId()).thenReturn(8);
        when(segundoDetalle.getCantidad()).thenReturn(1);
        when(alquilerRepository.buscarPorIdParaActualizar(alquilerId)).thenReturn(Optional.of(alquiler));
        when(alquilerRepository.buscarUnidadesParaDevolver(alquilerId)).thenReturn(List.of(primerDetalle, segundoDetalle));
        when(alquilerRepository.save(alquiler)).thenReturn(alquiler);

        alquilerService.cerrarAlquiler(alquilerId, crearSolicitudDeCierreValida());

        var orden = inOrder(alquilerRepository, inventarioItemService);
        orden.verify(alquilerRepository).buscarPorIdParaActualizar(alquilerId);
        orden.verify(alquilerRepository).buscarUnidadesParaDevolver(alquilerId);
        orden.verify(inventarioItemService).devolverVideojuego(3, 2);
        orden.verify(inventarioItemService).devolverVideojuego(8, 1);
        orden.verify(alquilerRepository).save(alquiler);
        assertEquals(EstadoAlquiler.FINALIZADO, alquiler.getEstadoAlquiler());
    }

    private CerrarAlquilerRequest crearSolicitudDeCierreValida() {
        CerrarAlquilerRequest request = new CerrarAlquilerRequest();
        request.setMetodoPago(MetodoPago.EFECTIVO);
        request.setDescuento(BigDecimal.ZERO);
        return request;
    }
}
