package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListaDeEsperaWorkflowServiceTest {

    @Mock
    private InventarioItemRepository inventarioItemRepository;

    @InjectMocks
    private ListaDeEsperaWorkflowService workflow;

    @Test
    void recibirDosEventosConUnaUnidadNotificaSoloLaPrimeraReserva() {
        InventarioItemEntity inventario = inventario(2, 1);
        LocalDateTime ahora = LocalDateTime.now();
        ReservaEntity primera = reserva(inventario, EstadoReserva.PENDIENTE, ahora.minusMinutes(2), null);
        ReservaEntity segunda = reserva(inventario, EstadoReserva.PENDIENTE, ahora.minusMinutes(1), null);
        when(inventarioItemRepository.buscarPorIdParaActualizar(2)).thenReturn(Optional.of(inventario));

        StockDisponibleEvent evento = new StockDisponibleEvent(2);
        workflow.onStockDisponible(evento);
        workflow.onStockDisponible(evento);

        verify(inventarioItemRepository, times(2)).buscarPorIdParaActualizar(2);
        assertEquals(0, inventario.getStockDisponible());
        assertEquals(EstadoReserva.NOTIFICADA, primera.getEstadoReserva());
        assertEquals(EstadoReserva.PENDIENTE, segunda.getEstadoReserva());
        assertNull(segunda.getFechaNotificacion());
    }

    @Test
    void vencimientoBloqueaLosCandidatosYReasignaLaUnidadLiberada() {
        InventarioItemEntity primero = inventario(2, 0);
        InventarioItemEntity segundo = inventario(5, 0);
        LocalDateTime ahora = LocalDateTime.now();
        ReservaEntity vencida = reserva(primero, EstadoReserva.NOTIFICADA, ahora.minusMinutes(10), ahora.minusMinutes(5));
        ReservaEntity siguiente = reserva(primero, EstadoReserva.PENDIENTE, ahora.minusMinutes(6), null);
        ReservaEntity vigente = reserva(segundo, EstadoReserva.NOTIFICADA, ahora.minusMinutes(2), ahora.minusMinutes(1));
        when(inventarioItemRepository.buscarIdsConReservasEnEstado(EstadoReserva.NOTIFICADA)).thenReturn(List.of(2, 5));
        when(inventarioItemRepository.buscarPorIdParaActualizar(2)).thenReturn(Optional.of(primero));
        when(inventarioItemRepository.buscarPorIdParaActualizar(5)).thenReturn(Optional.of(segundo));

        workflow.revisarReservasExpiradas();

        var orden = inOrder(inventarioItemRepository);
        orden.verify(inventarioItemRepository).buscarIdsConReservasEnEstado(EstadoReserva.NOTIFICADA);
        orden.verify(inventarioItemRepository).buscarPorIdParaActualizar(2);
        orden.verify(inventarioItemRepository).buscarPorIdParaActualizar(5);
        assertEquals(EstadoReserva.EXPIRADA, vencida.getEstadoReserva());
        assertEquals(EstadoReserva.NOTIFICADA, siguiente.getEstadoReserva());
        assertEquals(EstadoReserva.NOTIFICADA, vigente.getEstadoReserva());
        assertEquals(0, primero.getStockDisponible());
        assertEquals(0, segundo.getStockDisponible());
    }

    private InventarioItemEntity inventario(int id, int stockDisponible) {
        InventarioItemEntity inventario = new InventarioItemEntity();
        inventario.setInventarioItemId(id);
        inventario.setVideojuego(VideojuegoEntity.builder().titulo("Juego de prueba").build());
        inventario.setStockTotal(1);
        inventario.setStockDisponible(stockDisponible);
        return inventario;
    }

    private ReservaEntity reserva(InventarioItemEntity inventario, EstadoReserva estado, LocalDateTime fechaReserva,
                                  LocalDateTime fechaNotificacion) {
        ReservaEntity reserva = ReservaEntity.builder()
                .persona(PersonaEntity.builder().nombre("Cliente").email("cliente@example.test").build())
                .estadoReserva(estado)
                .fechaReserva(fechaReserva)
                .fechaNotificacion(fechaNotificacion)
                .build();
        inventario.agregarReserva(reserva);
        return reserva;
    }
}
