package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.DetalleAlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlquilerDisponibilidadTest {

    private static final String DNI = "12345678";
    private static final int INVENTARIO_ID = 12;

    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private InventarioItemRepository inventarioItemRepository;
    @Mock
    private AlquilerRepository alquilerRepository;
    @Mock
    private BloqueoService bloqueoService;
    @Spy
    private AlquilerMapper alquilerMapper;
    @Spy
    private DetalleAlquilerMapper detalleAlquilerMapper;
    @InjectMocks
    private AlquilerService alquilerService;

    private final PersonaEntity persona = PersonaEntity.builder().personaId(1).dni(DNI).build();
    private final InventarioItemEntity inventario = new InventarioItemEntity();

    @BeforeEach
    void prepararPersonaEInventario() {
        when(personaRepository.findByDni(DNI)).thenReturn(Optional.of(persona));
        inventario.setInventarioItemId(INVENTARIO_ID);
        inventario.setVideojuego(VideojuegoEntity.builder().titulo("Juego de prueba").build());
        inventario.setPrecioDiario(BigDecimal.TEN);
        inventario.setStockTotal(1);
    }

    @Test
    void validaYEntregaUnaReservaPropiaVigenteSinDescontarStock() {
        prepararInventarioDisponibleParaConsulta();
        ReservaEntity reserva = agregarReserva(persona, EstadoReserva.NOTIFICADA, LocalDateTime.now());

        var resultado = alquilerService.validarDisponibilidad(solicitud(detalle(1)));
        var disponibilidad = resultado.getDisponibilidad().getFirst();

        assertAll(
                () -> assertTrue(resultado.isPuedeCrearAlquiler()),
                () -> assertTrue(resultado.getFaltantes().isEmpty()),
                () -> assertTrue(disponibilidad.reservaPropiaVigente()),
                () -> assertEquals(1, disponibilidad.cantidadDisponibleParaCliente()),
                () -> assertEquals(0, disponibilidad.stockLibre()),
                () -> assertEquals(0, inventario.getStockDisponible()),
                () -> assertEquals(EstadoReserva.NOTIFICADA, reserva.getEstadoReserva())
        );
        verifyNoInteractions(alquilerRepository);

        AlquilerResponse respuesta = new AlquilerResponse();
        when(alquilerRepository.save(any(AlquilerEntity.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        doReturn(respuesta).when(alquilerMapper).toResponse(any(AlquilerEntity.class));

        assertSame(respuesta, alquilerService.crearAlquiler(solicitud(detalle(1))));
        assertEquals(EstadoReserva.CONCLUIDA, reserva.getEstadoReserva());
        assertEquals(0, inventario.getStockDisponible());
    }

    @Test
    void mantieneElBloqueoDeUnaReservaAjenaAunqueHayaStockLibre() {
        prepararInventarioDisponibleParaConsulta();
        inventario.setStockTotal(2);
        inventario.setStockDisponible(1);
        PersonaEntity otraPersona = PersonaEntity.builder().personaId(2).build();
        ReservaEntity reserva = agregarReserva(otraPersona, EstadoReserva.NOTIFICADA, LocalDateTime.now());

        comprobarRechazo(solicitud(detalle(1)), "Este videojuego está reservado para otro cliente.");

        assertEquals(EstadoReserva.NOTIFICADA, reserva.getEstadoReserva());
        assertEquals(1, inventario.getStockDisponible());
    }

    @Test
    void revalidaElVencimientoAlCrearAunqueLaConsultaHayaSidoExitosa() {
        prepararInventarioDisponibleParaConsulta();
        ReservaEntity reserva = agregarReserva(persona, EstadoReserva.NOTIFICADA, LocalDateTime.now());
        AlquilerCreateOrReplaceRequest solicitud = solicitud(detalle(1));
        assertTrue(alquilerService.validarDisponibilidad(solicitud).isPuedeCrearAlquiler());
        reserva.setFechaNotificacion(LocalDateTime.now().minusMinutes(5));

        comprobarRechazo(solicitud, "La reserva ya no está vigente.");

        assertEquals(EstadoReserva.NOTIFICADA, reserva.getEstadoReserva());
        assertEquals(0, inventario.getStockDisponible());
    }

    @ParameterizedTest
    @EnumSource(value = EstadoReserva.class, names = {"EXPIRADA", "CANCELADA"})
    void unaReservaInactivaNoCuentaComoDisponibilidad(EstadoReserva estado) {
        prepararInventarioDisponibleParaConsulta();
        ReservaEntity reserva = agregarReserva(persona, estado, LocalDateTime.now());

        comprobarRechazo(solicitud(detalle(1)),
                "El item: " + INVENTARIO_ID + " no tiene suficiente stock disponible para disminuir. Copias disponibles: 0");

        assertEquals(estado, reserva.getEstadoReserva());
        assertEquals(0, inventario.getStockDisponible());
    }

    @Test
    void unaReservaPropiaSoloHabilitaUnaUnidad() {
        prepararInventarioDisponibleParaConsulta();
        agregarReserva(persona, EstadoReserva.NOTIFICADA, LocalDateTime.now());

        comprobarRechazo(solicitud(detalle(2)),
                "Una reserva permite retirar una única unidad del mismo videojuego.");
    }

    @Test
    void rechazaInventariosDuplicadosAntesDeConsultarOConsumirUnaReserva() {
        AlquilerCreateOrReplaceRequest solicitud = solicitud(detalle(1), detalle(1));
        String mensaje = "El inventario con id " + INVENTARIO_ID + " aparece más de una vez en el alquiler.";

        assertEquals(mensaje, assertThrows(BusinessException.class,
                () -> alquilerService.validarDisponibilidad(solicitud)).getMessage());
        assertEquals(mensaje, assertThrows(BusinessException.class,
                () -> alquilerService.crearAlquiler(solicitud)).getMessage());
        verifyNoInteractions(inventarioItemRepository, alquilerRepository);
    }

    private void comprobarRechazo(AlquilerCreateOrReplaceRequest solicitud, String mensaje) {
        var resultado = alquilerService.validarDisponibilidad(solicitud);
        assertFalse(resultado.isPuedeCrearAlquiler());
        assertEquals(1, resultado.getFaltantes().size());
        assertEquals(mensaje, resultado.getDisponibilidad().getFirst().motivo());
        assertEquals(mensaje, assertThrows(BusinessException.class,
                () -> alquilerService.crearAlquiler(solicitud)).getMessage());
        verifyNoInteractions(alquilerRepository);
    }

    private void prepararInventarioDisponibleParaConsulta() {
        when(inventarioItemRepository.findById(INVENTARIO_ID)).thenReturn(Optional.of(inventario));
    }

    private ReservaEntity agregarReserva(PersonaEntity titular, EstadoReserva estado, LocalDateTime fechaNotificacion) {
        ReservaEntity reserva = ReservaEntity.builder()
                .persona(titular)
                .estadoReserva(estado)
                .fechaNotificacion(fechaNotificacion)
                .build();
        inventario.agregarReserva(reserva);
        return reserva;
    }

    private DetalleAlquilerRequest detalle(int cantidad) {
        return DetalleAlquilerRequest.builder().inventarioItemId(INVENTARIO_ID).cantidad(cantidad).build();
    }

    private AlquilerCreateOrReplaceRequest solicitud(DetalleAlquilerRequest... detalles) {
        return AlquilerCreateOrReplaceRequest.builder().personaDni(DNI).detalles(List.of(detalles)).build();
    }
}
