package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.controllers.AlquilerController;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCorreccionRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.DetalleAlquilerNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.GlobalExceptionHandler;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.OrigenDetalleAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlquilerCorreccionTest {

    private static final int ALQUILER_ID = 30;
    private static final int DETALLE_ID = 40;
    private static final int ORIGEN_ID = 8;
    private static final int DESTINO_ID = 3;
    private static final LocalDate INICIO = LocalDate.of(2026, 11, 20);
    private static final LocalDate FIN = LocalDate.of(2026, 11, 25);

    @Mock
    private AlquilerRepository alquilerRepository;
    @Mock
    private InventarioItemRepository inventarioItemRepository;
    @Mock
    private AlquilerMapper alquilerMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Spy
    private InventarioItemService inventarioItemService = new InventarioItemService();
    @InjectMocks
    private AlquilerService alquilerService;

    private final PersonaEntity cliente = PersonaEntity.builder().personaId(7).build();
    private final VideojuegoEntity juegoOriginal = VideojuegoEntity.builder().titulo("God of War").build();
    private final InventarioItemEntity origen = inventario(ORIGEN_ID, juegoOriginal, Plataformas.PS3, 100);
    private final AlquilerEntity alquiler = AlquilerEntity.builder()
            .alquilerId(ALQUILER_ID)
            .persona(cliente)
            .fechaInicio(INICIO)
            .fechaFin(FIN)
            .estadoAlquiler(EstadoAlquiler.EN_CURSO)
            .build();
    private DetalleAlquilerEntity detalle;

    @BeforeEach
    void prepararAlquiler() {
        ReflectionTestUtils.setField(inventarioItemService, "inventarioItemRepository", inventarioItemRepository);
        ReflectionTestUtils.setField(inventarioItemService, "eventPublisher", eventPublisher);
        origen.setStockDisponible(3);
        detalle = agregarDetalle(DETALLE_ID, origen, OrigenDetalleAlquiler.STOCK_LIBRE);
        alquiler.calcularMontoDiario();
    }

    @ParameterizedTest
    @CsvSource({"false, PS4", "true, PS3"})
    void reemplazaLaPlataformaOElJuego(boolean otroJuego, Plataformas plataforma) {
        VideojuegoEntity juego = otroJuego ? VideojuegoEntity.builder().titulo("Terraria").build() : juegoOriginal;
        InventarioItemEntity destino = inventario(DESTINO_ID, juego, plataforma, 150);
        DetalleAlquilerEntity otroDetalle = agregarDetalle(41,
                inventario(9, juegoOriginal, Plataformas.PS5, 200), OrigenDetalleAlquiler.STOCK_LIBRE);
        alquiler.calcularMontoDiario();
        prepararDetalleExistente();
        bloquear(origen);
        bloquear(destino);
        AlquilerResponse respuesta = prepararRespuesta();

        assertSame(respuesta, corregir(DESTINO_ID, 1));

        var orden = inOrder(alquilerRepository, inventarioItemRepository, inventarioItemService);
        orden.verify(alquilerRepository).buscarPorIdParaActualizar(ALQUILER_ID);
        orden.verify(alquilerRepository).buscarInventarioDelDetalle(ALQUILER_ID, DETALLE_ID);
        orden.verify(inventarioItemRepository).buscarPorIdParaActualizar(DESTINO_ID);
        orden.verify(inventarioItemRepository).buscarPorIdParaActualizar(ORIGEN_ID);
        orden.verify(inventarioItemService).devolverVideojuego(ORIGEN_ID, 1);
        orden.verify(alquilerRepository).save(alquiler);
        assertSame(destino, detalle.getInventarioItem());
        assertEquals(DETALLE_ID, detalle.getDetalleAlquilerId());
        assertEquals(1, detalle.getCantidad());
        assertEquals(OrigenDetalleAlquiler.STOCK_LIBRE, detalle.getOrigenEntrega());
        assertEquals(BigDecimal.valueOf(150), detalle.getSubtotal());
        assertEquals(4, origen.getStockDisponible());
        assertEquals(2, destino.getStockDisponible());
        assertEquals(BigDecimal.valueOf(350), alquiler.getMontoDiarioAlquiler());
        assertSame(otroDetalle, alquiler.getItems().get(1));
        assertEquals(BigDecimal.valueOf(200), otroDetalle.getSubtotal());
        assertEquals(ALQUILER_ID, alquiler.getAlquilerId());
        assertEquals(INICIO, alquiler.getFechaInicio());
        assertEquals(FIN, alquiler.getFechaFin());
        assertEquals(EstadoAlquiler.EN_CURSO, alquiler.getEstadoAlquiler());
        assertSame(cliente, alquiler.getPersona());
        verificarEventoDeDevolucion();
    }

    @Test
    void aumentaSoloLaDiferenciaDesdeStockLibreAunqueElClienteTengaUnaReserva() {
        ReservaEntity reserva = notificarReserva(origen);
        prepararDetalleExistente();
        bloquear(origen);
        prepararRespuesta();

        corregir(ORIGEN_ID, 3);

        verify(inventarioItemRepository).buscarPorIdParaActualizar(ORIGEN_ID);
        assertEquals(1, origen.getStockDisponible());
        assertEquals(3, detalle.getCantidad());
        assertEquals(BigDecimal.valueOf(300), alquiler.getMontoDiarioAlquiler());
        assertEquals(EstadoReserva.NOTIFICADA, reserva.getEstadoReserva());
        assertNull(detalle.getReservaConsumidaId());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void devuelveSoloLaDiferenciaAlReducirLaCantidad() {
        detalle.setCantidad(3);
        detalle.calcularSubtotal();
        alquiler.calcularMontoDiario();
        origen.setStockDisponible(1);
        prepararDetalleExistente();
        bloquear(origen);
        prepararRespuesta();

        corregir(ORIGEN_ID, 1);

        verify(inventarioItemService).devolverVideojuego(ORIGEN_ID, 2);
        assertEquals(3, origen.getStockDisponible());
        assertEquals(1, detalle.getCantidad());
        assertEquals(BigDecimal.valueOf(100), alquiler.getMontoDiarioAlquiler());
        verificarEventoDeDevolucion();
    }

    @Test
    void stockLibreInsuficienteRechazaLaCorreccionAunqueHayaReservaPropia() {
        InventarioItemEntity destino = inventario(DESTINO_ID, juegoOriginal, Plataformas.PS4, 150);
        destino.setStockDisponible(0);
        ReservaEntity reserva = notificarReserva(destino);
        prepararDetalleExistente();
        bloquear(origen);
        bloquear(destino);

        var error = assertThrows(IllegalArgumentException.class, () -> corregir(DESTINO_ID, 1));

        assertEquals("El item: 3 no tiene suficiente stock disponible para disminuir. Copias disponibles: 0",
                error.getMessage());
        assertEquals(0, destino.getStockDisponible());
        assertEquals(EstadoReserva.NOTIFICADA, reserva.getEstadoReserva());
        verificarAlquilerSinCambios();
    }

    @ParameterizedTest
    @CsvSource({"true, FINALIZADO", "false, EN_CURSO"})
    void rechazaUnAlquilerFinalizadoOInactivo(boolean activo, EstadoAlquiler estado) {
        alquiler.setActivo(activo);
        alquiler.setEstadoAlquiler(estado);
        prepararAlquilerExistente();

        assertThrows(BusinessException.class, () -> corregir(ORIGEN_ID, 2));

        verifyNoInteractions(inventarioItemRepository);
        verificarAlquilerSinCambios();
    }

    @ParameterizedTest
    @EnumSource(value = OrigenDetalleAlquiler.class, names = {"RESERVA", "DESCONOCIDO"})
    void rechazaSiOtroDetalleNoProvieneDeStockLibre(OrigenDetalleAlquiler origenDelOtroDetalle) {
        agregarDetalle(41, inventario(9, juegoOriginal, Plataformas.PS5, 200), origenDelOtroDetalle);
        prepararDetalleExistente();
        bloquear(origen);

        assertThrows(BusinessException.class, () -> corregir(ORIGEN_ID, 2));

        verificarAlquilerSinCambios();
    }

    @Test
    void rechazaSiElDestinoYaEstaEnOtroDetalle() {
        InventarioItemEntity destino = inventario(DESTINO_ID, juegoOriginal, Plataformas.PS4, 150);
        agregarDetalle(41, destino, OrigenDetalleAlquiler.STOCK_LIBRE);
        prepararDetalleExistente();
        bloquear(origen);
        bloquear(destino);

        assertThrows(BusinessException.class, () -> corregir(DESTINO_ID, 1));

        assertEquals(3, destino.getStockDisponible());
        verificarAlquilerSinCambios();
    }

    @Test
    void rechazaSiElDetalleNoPerteneceAlAlquiler() {
        prepararAlquilerExistente();

        assertThrows(DetalleAlquilerNoEncontradoException.class, () -> corregir(ORIGEN_ID, 2));

        verifyNoInteractions(inventarioItemRepository);
        verificarAlquilerSinCambios();
    }

    @ParameterizedTest
    @CsvSource({"false, true", "true, false"})
    void rechazaInventarioOVideojuegoDeDestinoInactivo(boolean inventarioActivo, boolean videojuegoActivo) {
        VideojuegoEntity juego = VideojuegoEntity.builder().titulo("Terraria").activo(videojuegoActivo).build();
        InventarioItemEntity destino = inventario(DESTINO_ID, juego, Plataformas.PS3, 150);
        destino.setActivo(inventarioActivo);
        prepararDetalleExistente();
        bloquear(origen);
        bloquear(destino);

        assertThrows(BusinessException.class, () -> corregir(DESTINO_ID, 1));

        assertEquals(3, destino.getStockDisponible());
        verificarAlquilerSinCambios();
    }

    @Test
    void devuelveElAlquilerSinGuardarNiEmitirEventosCuandoNoHayCambios() {
        prepararDetalleExistente();
        bloquear(origen);
        AlquilerResponse respuesta = new AlquilerResponse();
        when(alquilerMapper.toResponse(alquiler)).thenReturn(respuesta);

        assertSame(respuesta, corregir(ORIGEN_ID, 1));

        verificarAlquilerSinCambios();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"inventarioItemId\":8,\"cantidad\":0}",
            "{\"inventarioItemId\":-1,\"cantidad\":1}"})
    void validaElJsonDelPatchAntesDeBloquear(String body) throws Exception {
        AlquilerController controller = new AlquilerController();
        ReflectionTestUtils.setField(controller, "alquilerService", alquilerService);

        MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build()
                .perform(patch("/api/alquileres/{id}/detalles/{detalleId}", ALQUILER_ID, DETALLE_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").isString());

        verifyNoInteractions(alquilerRepository, inventarioItemRepository, eventPublisher);
    }

    private void prepararAlquilerExistente() {
        when(alquilerRepository.buscarPorIdParaActualizar(ALQUILER_ID)).thenReturn(Optional.of(alquiler));
    }

    private void prepararDetalleExistente() {
        prepararAlquilerExistente();
        when(alquilerRepository.buscarInventarioDelDetalle(ALQUILER_ID, DETALLE_ID)).thenReturn(Optional.of(ORIGEN_ID));
    }

    private void bloquear(InventarioItemEntity inventario) {
        when(inventarioItemRepository.buscarPorIdParaActualizar(inventario.getInventarioItemId()))
                .thenReturn(Optional.of(inventario));
    }

    private AlquilerResponse prepararRespuesta() {
        AlquilerResponse respuesta = new AlquilerResponse();
        when(alquilerRepository.save(alquiler)).thenReturn(alquiler);
        when(alquilerMapper.toResponse(alquiler)).thenReturn(respuesta);
        return respuesta;
    }

    private AlquilerResponse corregir(int inventarioId, int cantidad) {
        return alquilerService.corregirDetalle(ALQUILER_ID, DETALLE_ID,
                new DetalleAlquilerCorreccionRequest(inventarioId, cantidad));
    }

    private void verificarAlquilerSinCambios() {
        assertSame(origen, detalle.getInventarioItem());
        assertEquals(1, detalle.getCantidad());
        assertEquals(3, origen.getStockDisponible());
        assertEquals(BigDecimal.valueOf(100), alquiler.getMontoDiarioAlquiler());
        verify(alquilerRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    private void verificarEventoDeDevolucion() {
        var evento = ArgumentCaptor.forClass(StockDisponibleEvent.class);
        verify(eventPublisher).publishEvent(evento.capture());
        assertEquals(ORIGEN_ID, evento.getValue().getInventarioItemId());
    }

    private DetalleAlquilerEntity agregarDetalle(int id, InventarioItemEntity inventario, OrigenDetalleAlquiler origen) {
        DetalleAlquilerEntity nuevoDetalle = DetalleAlquilerEntity.builder()
                .detalleAlquilerId(id).inventarioItem(inventario).cantidad(1).build();
        if (origen != OrigenDetalleAlquiler.DESCONOCIDO) {
            nuevoDetalle.registrarOrigenEntrega(origen == OrigenDetalleAlquiler.RESERVA ? 77 : null);
        }
        alquiler.agregarDetalle(nuevoDetalle);
        nuevoDetalle.calcularSubtotal();
        return nuevoDetalle;
    }

    private ReservaEntity notificarReserva(InventarioItemEntity inventario) {
        ReservaEntity reserva = ReservaEntity.builder().reservaId(77).persona(cliente)
                .estadoReserva(EstadoReserva.NOTIFICADA).fechaNotificacion(LocalDateTime.now()).build();
        inventario.agregarReserva(reserva);
        return reserva;
    }

    private static InventarioItemEntity inventario(int id, VideojuegoEntity juego, Plataformas plataforma, int precio) {
        InventarioItemEntity inventario = new InventarioItemEntity();
        inventario.setInventarioItemId(id);
        inventario.setVideojuego(juego);
        inventario.setPlataforma(plataforma);
        inventario.setStockTotal(4);
        inventario.setStockDisponible(3);
        inventario.setPrecioDiario(BigDecimal.valueOf(precio));
        return inventario;
    }
}
