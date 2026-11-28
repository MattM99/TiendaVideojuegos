package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.controllers.PersonaController;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PersonaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PersonaListadoTest {

    private static final String ENDPOINT = "/api/personas/administracion/obtenertodos";

    @Mock
    private PersonaRepository personaRepository;
    @Spy
    private PersonaMapper personaMapper;
    @InjectMocks
    private PersonaService personaService;

    private MockMvc mvc;

    @BeforeEach
    void prepararController() {
        PersonaController controller = new PersonaController();
        ReflectionTestUtils.setField(controller, "personaService", personaService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @ParameterizedTest
    @CsvSource({"0, 5", "1, 1"})
    void paginaSeisPersonasVisiblesDeSieteAlExcluirAlFounder(int indice, int longitud) throws Exception {
        String dniFounder = "0000000";
        Sort orden = Sort.by(Sort.Order.desc("apellido"));
        PageRequest pagina = PageRequest.of(indice, 5, orden.and(Sort.by("personaId")));
        List<PersonaEntity> personas = Stream.of(dniFounder, "1000001", "1000002", "1000003", "1000004", "1000005", "1000006")
                .map(dni -> persona(dni, !"1000006".equals(dni)))
                .toList();
        List<PersonaEntity> visibles = personas.stream().filter(persona -> !dniFounder.equals(persona.getDni())).toList();
        int desde = Math.toIntExact(pagina.getOffset());
        PageRequest paginaSinFiltro = PageRequest.of(0, 5, orden);
        when(personaRepository.findAll(paginaSinFiltro))
                .thenReturn(new PageImpl<>(personas.subList(0, 5), paginaSinFiltro, personas.size()));
        when(personaRepository.buscarListado(dniFounder, null, pagina))
                .thenReturn(new PageImpl<>(visibles.subList(desde, Math.min(desde + 5, visibles.size())), pagina, visibles.size()));

        mvc.perform(get(ENDPOINT).param("tamano", "5").param("direccion", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value(dniFounder))
                .andExpect(jsonPath("$.totalElements").value(7));

        mvc.perform(get(ENDPOINT)
                        .param("pagina", String.valueOf(indice))
                        .param("tamano", "5")
                        .param("direccion", "desc")
                        .param("excluirFounder", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(longitud))
                .andExpect(jsonPath("$.content[0].dni").value(visibles.get(desde).getDni()))
                .andExpect(jsonPath("$.content[*].dni", not(hasItem(dniFounder))))
                .andExpect(jsonPath("$.number").value(indice))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void enviaElTerminoAlRepositorioConElTamanoYElOrdenSolicitados() throws Exception {
        PageRequest pagina = PageRequest.of(0, 20, Sort.by("nombre", "personaId"));
        when(personaRepository.buscarListado("0000000", "FraN", pagina))
                .thenReturn(new PageImpl<>(List.of(persona("1234567", false)), pagina, 1));

        mvc.perform(get(ENDPOINT)
                        .param("busqueda", " FraN ")
                        .param("excluirFounder", "true")
                        .param("tamano", "20")
                        .param("ordenarPor", "nombre")
                        .param("direccion", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value("1234567"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void buscarSinExclusionPuedeDevolverUnaPaginaVacia() throws Exception {
        PageRequest pagina = PageRequest.of(0, 10, Sort.by("apellido", "personaId"));
        when(personaRepository.buscarListado(null, "sin coincidencias", pagina)).thenReturn(Page.empty(pagina));

        mvc.perform(get(ENDPOINT).param("busqueda", "sin coincidencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void unaLlamadaSinFiltrosConservaElListadoAnterior() throws Exception {
        PageRequest pagina = PageRequest.of(0, 10, Sort.by("apellido"));
        when(personaRepository.findAll(pagina)).thenReturn(new PageImpl<>(
                List.of(persona("0000000", true), persona("1234567", false)), pagina, 2));

        mvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value("0000000"))
                .andExpect(jsonPath("$.content[1].dni").value("1234567"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void soloExcluyeAlFounderDelListadoSiSeActivaElFiltro() throws Exception {
        String dniFounder = "0000000";
        PersonaEntity founder = persona(dniFounder, true);
        PageRequest pagina = PageRequest.of(0, 10, Sort.by("apellido", "personaId"));
        when(personaRepository.buscarListado(null, dniFounder, pagina))
                .thenReturn(new PageImpl<>(List.of(founder), pagina, 1));
        when(personaRepository.buscarListado(dniFounder, dniFounder, pagina)).thenReturn(Page.empty(pagina));
        when(personaRepository.findByDni(dniFounder)).thenReturn(Optional.of(founder));

        mvc.perform(get(ENDPOINT).param("busqueda", dniFounder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value(dniFounder))
                .andExpect(jsonPath("$.totalElements").value(1));

        mvc.perform(get(ENDPOINT).param("busqueda", dniFounder).param("excluirFounder", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].dni").value(dniFounder))
                .andExpect(jsonPath("$.totalElements").value(1));

        mvc.perform(get(ENDPOINT).param("busqueda", dniFounder).param("excluirFounder", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        mvc.perform(get("/api/personas/dni/{dni}", dniFounder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value(dniFounder));
    }

    private PersonaEntity persona(String dni, boolean activa) {
        return PersonaEntity.builder().dni(dni).nombre("Franco").apellido("Prueba").activo(activa).build();
    }
}
