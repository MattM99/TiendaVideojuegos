package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.exception.GlobalExceptionHandler;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitConfig(VideojuegoAltaTest.Configuracion.class)
class VideojuegoAltaTest {

    private static final int VIDEOJUEGO_ID = 7;

    @Autowired
    private VideojuegoController controller;
    @Autowired
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private InventarioItemRepository inventarioItemRepository;

    private MockMvc mvc;

    @BeforeEach
    void prepararMvc() {
        reset(videojuegoRepository, inventarioItemRepository);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void limpiarAutenticacion() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"FOUNDER", "ADMINISTRADOR"})
    void reactivaElMismoVideojuegoYConservaLosVinculosHistoricos(Roles rol) throws Exception {
        var videojuego = VideojuegoEntity.builder()
                .videojuegoId(VIDEOJUEGO_ID)
                .titulo("Terraria")
                .desarrollador("Re-Logic")
                .genero(Generos.AVENTURA)
                .lanzamiento(Year.of(2011))
                .descripcion("Aventura de exploración y construcción")
                .multijugador(true)
                .activo(false)
                .build();
        var inventario = InventarioItemEntity.builder()
                .inventarioItemId(11)
                .videojuego(videojuego)
                .stockTotal(2)
                .stockDisponible(2)
                .activo(false)
                .build();
        var alquiler = AlquilerEntity.builder().build();
        var detalle = DetalleAlquilerEntity.builder()
                .alquiler(alquiler)
                .inventarioItem(inventario)
                .cantidad(1)
                .build();
        var reserva = ReservaEntity.builder()
                .inventarioItem(inventario)
                .estadoReserva(EstadoReserva.CONCLUIDA)
                .build();
        inventario.setDetalleAlquileres(List.of(detalle));
        inventario.setListaDeEspera(List.of(reserva));
        videojuego.setInventario(List.of(inventario));

        when(videojuegoRepository.buscarPorIdParaActualizar(VIDEOJUEGO_ID)).thenReturn(Optional.of(videojuego));
        when(videojuegoRepository.save(videojuego)).thenReturn(videojuego);

        altaComo(rol)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.videojuegoId").value(VIDEOJUEGO_ID))
                .andExpect(jsonPath("$.titulo").value("Terraria"))
                .andExpect(jsonPath("$.desarrollador").value("Re-Logic"))
                .andExpect(jsonPath("$.genero").value("AVENTURA"))
                .andExpect(jsonPath("$.lanzamiento").value("2011"))
                .andExpect(jsonPath("$.descripcion").value("Aventura de exploración y construcción"))
                .andExpect(jsonPath("$.multijugador").value("Sí"));

        verify(videojuegoRepository).save(videojuego);
        verifyNoInteractions(inventarioItemRepository);
        assertSame(inventario, videojuego.getInventario().getFirst());
        assertSame(videojuego, inventario.getVideojuego());
        assertFalse(inventario.isActivo());
        assertEquals(2, inventario.getStockTotal());
        assertEquals(2, inventario.getStockDisponible());
        assertSame(detalle, inventario.getDetalleAlquileres().getFirst());
        assertSame(alquiler, detalle.getAlquiler());
        assertSame(inventario, detalle.getInventarioItem());
        assertEquals(1, detalle.getCantidad());
        assertSame(reserva, inventario.getListaDeEspera().getFirst());
        assertSame(inventario, reserva.getInventarioItem());
        assertEquals(EstadoReserva.CONCLUIDA, reserva.getEstadoReserva());
    }

    @Test
    void rechazaElAltaParaEmpleado() throws Exception {
        altaComo(Roles.EMPLEADO).andExpect(status().isForbidden());

        verifyNoInteractions(videojuegoRepository, inventarioItemRepository);
    }

    @Test
    void rechazaElAltaSiYaEstaActivo() throws Exception {
        var videojuego = VideojuegoEntity.builder().videojuegoId(VIDEOJUEGO_ID).activo(true).build();
        when(videojuegoRepository.buscarPorIdParaActualizar(VIDEOJUEGO_ID)).thenReturn(Optional.of(videojuego));

        altaComo(Roles.ADMINISTRADOR)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El videojuego ya se encuentra activo."));

        verify(videojuegoRepository, never()).save(any());
    }

    @Test
    void devuelve404SiNoExisteElVideojuego() throws Exception {
        when(videojuegoRepository.buscarPorIdParaActualizar(VIDEOJUEGO_ID)).thenReturn(Optional.empty());

        altaComo(Roles.ADMINISTRADOR).andExpect(status().isNotFound());

        verify(videojuegoRepository, never()).save(any());
    }

    @Test
    void conservaElRechazoDeTitulosDuplicados() throws Exception {
        when(videojuegoRepository.existsByTitulo("Terraria")).thenReturn(true);

        mvc.perform(post("/api/videojuegos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Terraria",
                                  "desarrollador": "Re-Logic",
                                  "genero": "AVENTURA",
                                  "lanzamiento": "2011",
                                  "descripcion": "Aventura de exploración y construcción"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un videojuego con ese título."));

        verify(videojuegoRepository, never()).save(any());
    }

    private ResultActions altaComo(Roles rol) throws Exception {
        var cuenta = CuentaEntity.builder().rol(rol).build();
        var autenticacion = UsernamePasswordAuthenticationToken.authenticated(cuenta, null, cuenta.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(autenticacion);

        return mvc.perform(patch("/api/videojuegos/{id}/alta", VIDEOJUEGO_ID));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableMethodSecurity(proxyTargetClass = true)
    @Import({VideojuegoController.class, VideojuegoService.class, VideojuegoMapper.class})
    static class Configuracion {

        @Bean
        VideojuegoRepository videojuegoRepository() {
            return mock(VideojuegoRepository.class);
        }

        @Bean
        InventarioItemRepository inventarioItemRepository() {
            return mock(InventarioItemRepository.class);
        }
    }
}
