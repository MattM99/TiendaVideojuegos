package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CerrarAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlquilerServiceTest {

    @Mock
    private AlquilerRepository alquilerRepository;

    @Mock
    private InventarioItemService inventarioItemService;

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

        when(alquilerRepository.findById(alquilerId))
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
                .devolverVideojuego(any(), anyInt());
    }

    private CerrarAlquilerRequest crearSolicitudDeCierreValida() {
        CerrarAlquilerRequest request = new CerrarAlquilerRequest();
        request.setMetodoPago(MetodoPago.EFECTIVO);
        request.setDescuento(BigDecimal.ZERO);
        return request;
    }
}
