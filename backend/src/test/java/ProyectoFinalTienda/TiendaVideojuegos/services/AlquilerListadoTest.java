package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.controllers.AlquilerController;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlquilerListadoTest {

    @Mock
    private AlquilerRepository alquilerRepository;

    @Mock
    private AlquilerMapper alquilerMapper;

    @InjectMocks
    private AlquilerService alquilerService;

    private MockMvc mvc;

    @BeforeEach
    void prepararController() {
        AlquilerController controller = new AlquilerController();
        ReflectionTestUtils.setField(controller, "alquilerService", alquilerService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @ParameterizedTest
    @CsvSource({
            "listaractivos, false, EN_CURSO",
            "listaractivos, false,",
            "administracion/obtenertodos, true, EN_CURSO",
            "administracion/obtenertodos, true,"
    })
    void conservaLosTotalesDelRepositorioConCadaFiltro(
            String endpoint, boolean incluirInactivos, EstadoAlquiler estado) throws Exception {
        PageRequest pagina = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "fechaInicio", "alquilerId"));
        AlquilerEntity alquiler = AlquilerEntity.builder().alquilerId(12).build();
        when(alquilerRepository.buscarListado(incluirInactivos, estado, null, null, null, pagina))
                .thenReturn(new PageImpl<>(List.of(alquiler), pagina, 6));
        when(alquilerMapper.toResponse(alquiler)).thenReturn(AlquilerResponse.builder().alquilerId(12).build());

        MockHttpServletRequestBuilder solicitud = get("/api/alquileres/" + endpoint)
                .param("pagina", "1")
                .param("tamano", "5")
                .param("ordenarPor", "fechaInicio")
                .param("direccion", "desc");
        if (estado != null) {
            solicitud.param("estado", estado.name());
        }

        mvc.perform(solicitud)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].alquilerId").value(12))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void aplicaPersonaYRangoDeFechasEnElListadoActivo() throws Exception {
        var inicio = LocalDate.of(2026, 11, 1);
        var fin = LocalDate.of(2026, 11, 28);
        var pagina = PageRequest.of(0, 5, Sort.by("fechaInicio", "alquilerId"));
        when(alquilerRepository.buscarListado(false, EstadoAlquiler.FINALIZADO, inicio, fin, "mUj", pagina))
                .thenReturn(Page.empty(pagina));

        mvc.perform(get("/api/alquileres/listaractivos")
                        .param("tamano", "5")
                        .param("estado", "FINALIZADO")
                        .param("fechaInicio", inicio.toString())
                        .param("fechaFin", fin.toString())
                        .param("persona", " mUj "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(alquilerRepository).buscarListado(false, EstadoAlquiler.FINALIZADO, inicio, fin, "mUj", pagina);
    }
}
