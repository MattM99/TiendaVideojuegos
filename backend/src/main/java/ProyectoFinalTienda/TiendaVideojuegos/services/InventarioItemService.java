package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.ReservaRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioItemResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ReservaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.exception.*;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.InventarioItemMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.ReservaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioItemService {

    @Autowired
    private InventarioItemRepository inventarioItemRepository;
    @Autowired
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private InventarioItemMapper inventarioItemMapper;
    @Autowired
    private VideojuegoMapper videojuegoMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ReservaMapper reservaMapper;
    @Autowired
    private PersonaRepository  personaRepository;

    public InventarioItemResponse guardar(InventarioItemCreateOrReplaceRequest request) {
        VideojuegoEntity videojuego = videojuegoRepository.findById(request.getVideojuegoId()).
                orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + request.getVideojuegoId() + " no encontrado."));

        if(validarDatosDeInventario(request)){
            InventarioItemEntity entity = inventarioItemMapper.toEntity(request, videojuego);
            // Validar que el stock sea correcto antes de guardar
            //entity.validarStock();
            return inventarioItemMapper.toResponse(inventarioItemRepository.save(entity), videojuegoMapper.toResponse(videojuego));
        }else{
            throw new IllegalArgumentException("Validaciones fallidas para el inventario.");
        }
    }

    public void eliminar(int id){
        if (!inventarioItemRepository.existsById(id)) {
            throw new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado.");
        }
        inventarioItemRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<InventarioItemResponse> obtenerTodos(){
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findAll();
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario en el sistema.");
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por id con excepción si no existe
    public InventarioItemResponse buscarPorId(int id){
        InventarioItemEntity entity = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));
        return inventarioItemMapper.toResponse(entity, videojuegoMapper.toResponse(entity.getVideojuego()));
    }

    // Buscar por videojuego con excepción si lista vacía
    public List<InventarioItemResponse> buscarPorVideojuego(int videojuegoId) {
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByVideojuegoId(videojuegoId);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con la id del videojuego: " + videojuegoId);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por plataforma con excepción si lista vacía
    public List<InventarioItemResponse> buscarPorPlataforma(Plataformas plataforma) throws IllegalArgumentException{

        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByPlataforma(plataforma);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con la plataforma: " + plataforma);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por precio menor que valor, validando lista vacía
    public List<InventarioItemResponse> buscarMasBaratosQue(double valor){
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByPrecioDiarioLessThan(valor);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con precio menor a: " + valor);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por plataforma y precio menor que valor, validando lista vacía
    public List<InventarioItemResponse> buscarPorPlataformaMasBaratosQue(Plataformas plataforma, double valor){
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByPlataformaAndPrecioDiarioLessThan(plataforma, valor);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con plataforma " + plataforma + " y precio menor a " + valor);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Obtener stock total, lanzar excepción si null
    public Integer obtenerStockTotal(int inventarioId) {
        Integer stockTotal = inventarioItemRepository.findStockTotalByInventarioId(inventarioId);
        if (stockTotal == null) {
            throw new InventarioItemNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockTotal;
    }

    // Obtener stock disponible, lanzar excepción si null
    public Integer obtenerStockDisponible(int inventarioId) {
        Integer stockDisponible = inventarioItemRepository.findStockDisponibleByInventarioId(inventarioId);
        if (stockDisponible == null) {
            throw new InventarioItemNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockDisponible;
    }

    public InventarioItemResponse actualizarCompleto(int id, InventarioItemCreateOrReplaceRequest nuevosDatos) {
        InventarioItemEntity existente = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id " + id + " no encontrado."));

        existente.setPrecioDiario(nuevosDatos.getPrecioDiario());
        existente.setStockTotal(nuevosDatos.getStockTotal());
        existente.setStockDisponible(nuevosDatos.getStockDisponible());

        existente.validarStock();

        return inventarioItemMapper.toResponse(inventarioItemRepository.save(existente), videojuegoMapper.toResponse(existente.getVideojuego()));
    }

    public InventarioItemResponse actualizarPorCampo(int id, InventarioItemUpdateRequest datosActualizados) {
        InventarioItemEntity existente = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));

        inventarioItemMapper.actualizarEntity(existente, datosActualizados);
        existente.validarStock();

        return inventarioItemMapper.toResponse(inventarioItemRepository.save(existente), videojuegoMapper.toResponse(existente.getVideojuego()));
    }


    public boolean validarDatosDeInventario (InventarioItemCreateOrReplaceRequest request) throws IllegalArgumentException {

        if(request.getStockDisponible() > request.getStockTotal()) {
            throw new IllegalArgumentException("El stock disponible no puede ser mayor que el stock total.");
        }

        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByVideojuegoId(request.getVideojuegoId());

        if (inventarios.stream().anyMatch(i -> i.getPlataforma() == request.getPlataforma())) {
            throw new IllegalArgumentException("Ya existe un inventario para el videojuego con la misma plataforma.");
        }

        return true;
    }

    @Transactional
    public InventarioItemResponse agregarStock(int id, int cantidad) {

        InventarioItemEntity existente = obtenerInventarioPorId(id);

        existente.aumentarStockTotal(cantidad);

        existente.aumentarStockDisponible(cantidad);

        InventarioItemEntity guardado =
                inventarioItemRepository.save(existente);

        eventPublisher.publishEvent(
                new StockDisponibleEvent(
                        guardado.getInventarioItemId()
                )
        );

        System.out.println("Evento publicado para inventario " + guardado.getInventarioItemId());

        return inventarioItemMapper.toResponse(
                guardado,
                videojuegoMapper.toResponse(
                        guardado.getVideojuego()
                )
        );
    }

    public InventarioItemResponse disminuirStockDisponible(int id, int cantidad) {

        InventarioItemEntity existente = obtenerInventarioPorId(id);
        existente.disminuirStockDisponible(cantidad);
        return inventarioItemMapper.toResponse(
                inventarioItemRepository.save(existente),
                videojuegoMapper.toResponse(existente.getVideojuego())
        );
    }

    @Transactional
    public void devolverVideojuego(
            InventarioItemEntity inventario,
            int cantidad
    ) {

        inventario.aumentarStockDisponible(cantidad);

        eventPublisher.publishEvent(
                new StockDisponibleEvent(
                        inventario.getInventarioItemId()
                )
        );
    }

    public InventarioItemResponse darDeBaja(int id) throws StockNoValidoException {
        InventarioItemEntity existente = obtenerInventarioPorId(id);

        if (existente.getStockTotal() != existente.getStockDisponible()) {
            throw new StockNoValidoException("Hay copias alquiladas, por favor complete " +
                    "los alquileres activos antes de dar de baja el stock");
        }

        existente.setStockTotal(0);
        existente.setStockDisponible(0);

        return inventarioItemMapper.toResponse(
                inventarioItemRepository.save(existente),
                videojuegoMapper.toResponse(existente.getVideojuego()));
    }

    private InventarioItemEntity obtenerInventarioPorId(int id) {
        return inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + id + " no encontrado."
                ));
    }


    // Aggregate root

    @Transactional
    public ReservaResponse crearReserva(
            Integer inventarioId,
            ReservaRequest request
    ) {

        PersonaEntity persona = personaRepository.findById(
                        request.getPersonaId()
                )
                .orElseThrow(() ->
                        new PersonaNoEncontradaException(
                                "Persona no encontrada con id: "
                                        + request.getPersonaId()
                        ));

        InventarioItemEntity inventario =
                inventarioItemRepository.findById(inventarioId)
                        .orElseThrow(() ->
                                new InventarioItemNoEncontradoException(
                                        "Inventario con id: "
                                                + inventarioId
                                                + " no encontrado."
                                ));

        if (inventario.getStockDisponible() > 0) {
            throw new BusinessException(
                    "El videojuego tiene stock disponible, no es necesario reservar."
            );
        }

        boolean yaReservo = inventario.getListaDeEspera().stream()
                .anyMatch(r ->
                        r.getPersona().getPersonaId()
                                == persona.getPersonaId()
                                && r.getEstadoReserva()
                                == EstadoReserva.PENDIENTE
                );

        if (yaReservo) {
            throw new BusinessException(
                    "La persona ya posee una reserva pendiente para este videojuego."
            );
        }

        ReservaEntity reserva = ReservaEntity.builder()
                .persona(persona)
                .estadoReserva(EstadoReserva.PENDIENTE)
                .fechaReserva(LocalDateTime.now())
                .build();

        inventario.agregarReserva(reserva);

        inventarioItemRepository.saveAndFlush(inventario);

        return reservaMapper.toResponse(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservas(Integer inventarioId) {

        InventarioItemEntity inventario =  obtenerInventarioPorId(inventarioId);

        return inventario.getListaDeEspera().stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

}


