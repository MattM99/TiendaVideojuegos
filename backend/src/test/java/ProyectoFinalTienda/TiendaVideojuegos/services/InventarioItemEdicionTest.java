package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.controllers.InventarioItemController;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.exception.GlobalExceptionHandler;
import ProyectoFinalTienda.TiendaVideojuegos.exception.StockNoValidoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.InventarioItemMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InventarioItemEdicionTest {

    private static final int INVENTARIO_ID = 3;

    @Mock
    private InventarioItemRepository inventarioItemRepository;
    @Mock
    private VideojuegoMapper videojuegoMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Spy
    private InventarioItemMapper inventarioItemMapper;
    @InjectMocks
    private InventarioItemService inventarioItemService;

    private final InventarioItemEntity inventario = new InventarioItemEntity();

    @BeforeEach
    void prepararInventario() {
        inventario.setInventarioItemId(INVENTARIO_ID);
        inventario.setVideojuego(VideojuegoEntity.builder().videojuegoId(7).build());
        inventario.setPlataforma(Plataformas.PS3);
        inventario.setPrecioDiario(BigDecimal.TEN);
        inventario.setStockTotal(4);
        inventario.setStockDisponible(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"precioDiario\":150}",
            "{\"precioDiario\":150,\"stockTotal\":5,\"stockDisponible\":3,\"videojuegoId\":9,\"plataforma\":\"PS4\"}"
    })
    void editarElPrecioMantieneElStockBloqueadoAunqueElClienteEnvieValoresAnteriores(String body) throws Exception {
        prepararBloqueo();
        prepararGuardado();
        InventarioItemController controller = new InventarioItemController();
        ReflectionTestUtils.setField(controller, "inventarioItemService", inventarioItemService);

        MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build()
                .perform(put("/api/inventario/{id}", INVENTARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioDiario").value(150))
                .andExpect(jsonPath("$.stockTotal").value(4))
                .andExpect(jsonPath("$.stockDisponible").value(1))
                .andExpect(jsonPath("$.plataforma").value("PS3"));

        verify(inventarioItemRepository).buscarPorIdParaActualizar(INVENTARIO_ID);
        assertEquals(7, inventario.getVideojuego().getVideojuegoId());
        verifyNoInteractions(eventPublisher);
    }

    @ParameterizedTest
    @CsvSource({"3, false", "2, true"})
    void rechazaAjustarStockDisponiblePorEncimaDelTotalMenosLosCompromisos(long alquiladas, boolean reservaNotificada) {
        prepararBloqueo();
        if (reservaNotificada) agregarReserva(EstadoReserva.NOTIFICADA);
        when(inventarioItemRepository.contarUnidadesAlquiladas(INVENTARIO_ID, EstadoAlquiler.EN_CURSO))
                .thenReturn(alquiladas);
        var request = InventarioItemUpdateRequest.builder().stockDisponible(2).build();

        assertThrows(StockNoValidoException.class,
                () -> inventarioItemService.actualizarPorCampo(INVENTARIO_ID, request));

        assertEquals(1, inventario.getStockDisponible());
        verify(inventarioItemRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void unAumentoValidoPublicaUnEventoYConservaLasUnidadesComprometidas() {
        prepararBloqueo();
        prepararGuardado();
        agregarReserva(EstadoReserva.NOTIFICADA);
        agregarReserva(EstadoReserva.PENDIENTE);
        when(inventarioItemRepository.contarUnidadesAlquiladas(INVENTARIO_ID, EstadoAlquiler.EN_CURSO))
                .thenReturn(2L);
        var request = InventarioItemUpdateRequest.builder().stockTotal(5).stockDisponible(2).build();

        var respuesta = inventarioItemService.actualizarPorCampo(INVENTARIO_ID, request);

        assertEquals(5, respuesta.getStockTotal());
        assertEquals(2, respuesta.getStockDisponible());
        var evento = ArgumentCaptor.forClass(StockDisponibleEvent.class);
        var orden = inOrder(inventarioItemRepository, eventPublisher);
        orden.verify(inventarioItemRepository).buscarPorIdParaActualizar(INVENTARIO_ID);
        orden.verify(inventarioItemRepository).save(inventario);
        orden.verify(eventPublisher).publishEvent(evento.capture());
        orden.verifyNoMoreInteractions();
        assertEquals(INVENTARIO_ID, evento.getValue().getInventarioItemId());
    }

    @ParameterizedTest
    @CsvSource({"4, 0", "4, 1", "5, 1"})
    void elAjusteNoPublicaEventoSiElStockDisponibleNoAumenta(int total, int disponible) {
        prepararBloqueo();
        prepararGuardado();
        var request = InventarioItemUpdateRequest.builder().stockTotal(total).stockDisponible(disponible).build();

        var respuesta = inventarioItemService.actualizarPorCampo(INVENTARIO_ID, request);

        assertEquals(total, respuesta.getStockTotal());
        assertEquals(disponible, respuesta.getStockDisponible());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void rechazaAjustarDisponibilidadPorEncimaDelStockTotal() {
        prepararBloqueo();
        var request = InventarioItemUpdateRequest.builder().stockDisponible(5).build();

        assertThrows(IllegalArgumentException.class,
                () -> inventarioItemService.actualizarPorCampo(INVENTARIO_ID, request));

        assertEquals(1, inventario.getStockDisponible());
        verify(inventarioItemRepository, never()).save(any());
    }

    @Test
    void agregarStockAumentaAmbosContadoresYPublicaElEventoExistente() {
        prepararBloqueo();
        prepararGuardado();

        var respuesta = inventarioItemService.agregarStock(INVENTARIO_ID, 2);

        assertEquals(6, respuesta.getStockTotal());
        assertEquals(3, respuesta.getStockDisponible());
        var evento = ArgumentCaptor.forClass(StockDisponibleEvent.class);
        verify(eventPublisher).publishEvent(evento.capture());
        assertEquals(INVENTARIO_ID, evento.getValue().getInventarioItemId());
    }

    @Test
    void agregarStockSigueRechazandoUnaCantidadNegativa() {
        prepararBloqueo();

        assertThrows(IllegalArgumentException.class, () -> inventarioItemService.agregarStock(INVENTARIO_ID, -1));

        assertEquals(4, inventario.getStockTotal());
        assertEquals(1, inventario.getStockDisponible());
        verify(inventarioItemRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    private void prepararBloqueo() {
        when(inventarioItemRepository.buscarPorIdParaActualizar(INVENTARIO_ID)).thenReturn(Optional.of(inventario));
    }

    private void prepararGuardado() {
        when(inventarioItemRepository.save(inventario)).thenReturn(inventario);
    }

    private void agregarReserva(EstadoReserva estado) {
        inventario.agregarReserva(ReservaEntity.builder().estadoReserva(estado).build());
    }
}
