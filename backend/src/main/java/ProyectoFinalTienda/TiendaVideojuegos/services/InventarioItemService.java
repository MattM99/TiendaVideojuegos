package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.ReservaRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /// Create

    public InventarioItemResponse guardar(InventarioItemCreateOrReplaceRequest request) {
        VideojuegoEntity videojuego = videojuegoRepository.findById(request.getVideojuegoId()).
                orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + request.getVideojuegoId() + " no encontrado."));

        validarCreate(request);

        InventarioItemEntity entity = inventarioItemMapper.toEntity(request, videojuego);

        return mapearResponse(inventarioItemRepository.save(entity));
    }

    /// Read

    public List<InventarioItemResponse> obtenerTodos(){
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findAll();
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario en el sistema.");
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    public Page<InventarioItemResponse> listarTodos(Pageable paginacion) {
        return inventarioItemRepository.findAll(paginacion)
                .map(inventario -> {

                    VideojuegoResponse videojuegoResponse =
                            videojuegoMapper.toResponse(
                                    inventario.getVideojuego()
                            );

                    return inventarioItemMapper.toResponse(
                            inventario,
                            videojuegoResponse
                    );
                });
    }

    public InventarioItemResponse buscarPorId(int id){
        InventarioItemEntity entity = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));
        return mapearResponse(entity);
    }

    public Page<InventarioItemResponse> buscarPorVideojuego(int videojuegoId, Pageable paginacion) {
        Page<InventarioItemEntity> inventarios =
                inventarioItemRepository.findByVideojuegoId(videojuegoId, paginacion);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con la id del videojuego: " + videojuegoId);
        }
        return inventarios.map(this::mapearInventario);
    }

    public Page<InventarioItemResponse> buscarPorPlataforma(
                Pageable paginacion,
                Plataformas plataforma) {

            Page<InventarioItemEntity> inventarios =
                    inventarioItemRepository.findByPlataforma(plataforma, paginacion);

            if (inventarios.isEmpty()) {
                throw new InventarioItemNoEncontradoException(
                        "No se encontró ningún inventario con la plataforma: " + plataforma);
            }

        return inventarios.map(this::mapearInventario);
        }

    public Page<InventarioItemResponse> buscarMasBaratosQue(
            double valor,
            Pageable paginacion) {

        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }

        Page<InventarioItemEntity> inventarios =
                inventarioItemRepository.findByPrecioDiarioLessThan(
                        valor,
                        paginacion);

        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException(
                    "No se encontró ningún inventario con precio menor a: " + valor);
        }

        return inventarios.map(this::mapearInventario);
    }

    public Page<InventarioItemResponse> buscarPorPlataformaMasBaratosQue(
            Plataformas plataforma,
            double valor,
            Pageable paginacion) {

        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }

        Page<InventarioItemEntity> inventarios =
                inventarioItemRepository.findByPlataformaAndPrecioDiarioLessThan(
                        plataforma,
                        valor,
                        paginacion);

        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException(
                    "No se encontró ningún inventario con plataforma "
                            + plataforma
                            + " y precio menor a "
                            + valor);
        }

        return inventarios.map(this::mapearInventario);
    }

    /// Update

    public InventarioItemResponse actualizarCompleto(int id, InventarioItemCreateOrReplaceRequest nuevosDatos) {

        InventarioItemEntity existente = obtenerInventarioPorId(id);

        validarUpdate(
                nuevosDatos.getVideojuegoId(),
                nuevosDatos.getPlataforma(),
                id
        );

        validarStock(nuevosDatos.getStockDisponible(), nuevosDatos.getStockTotal());

        existente.setPrecioDiario(nuevosDatos.getPrecioDiario());
        existente.setStockTotal(nuevosDatos.getStockTotal());
        existente.setStockDisponible(nuevosDatos.getStockDisponible());

        return mapearResponse(inventarioItemRepository.save(existente));
    }

    public InventarioItemResponse actualizarPorCampo(int id, InventarioItemUpdateRequest datosActualizados) {
        InventarioItemEntity existente = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));

        validarUpdate(
                existente.getVideojuego().getVideojuegoId(),
                existente.getPlataforma(),
                id
        );
        inventarioItemMapper.actualizarEntity(existente, datosActualizados);
        validarStock(existente.getStockDisponible(), existente.getStockTotal());

        return mapearResponse(inventarioItemRepository.save(existente));
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

        return mapearResponse(guardado);
    }

    // Solo para pruebas
//    public InventarioItemResponse disminuirStockDisponible(int id, int cantidad) {
//
//        InventarioItemEntity existente = obtenerInventarioPorId(id);
//        existente.disminuirStockDisponible(cantidad);
//        return mapearResponse(inventarioItemRepository.save(existente));
//    }

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

    /// Delete

    public void eliminar(int id) {
        InventarioItemEntity inventario = obtenerInventarioPorId(id);

        validarSinCompromisos(inventario);

        inventarioItemRepository.delete(inventario);
    }

    public InventarioItemResponse darDeBaja(int id) throws StockNoValidoException {
        InventarioItemEntity existente = obtenerInventarioPorId(id);

        validarSinCompromisos(existente);

        /// En una evolución futura del sistema agregaría un estado de inventario inactivo o
        /// discontinuado. En esta versión decidimos priorizar la consistencia de los alquileres
        /// y reservas existentes.

        existente.setStockTotal(0);
        existente.setStockDisponible(0);

        return mapearResponse(inventarioItemRepository.save(existente));
    }

    /// Aggregate root

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

        InventarioItemEntity inventario = obtenerInventarioPorId(inventarioId);

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

    /// Helpers privados

    private InventarioItemEntity obtenerInventarioPorId(int id) {
        return inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + id + " no encontrado."
                ));
    }

    private void validarSinCompromisos(InventarioItemEntity inventario) {
        if (inventario.getStockTotal()
                != inventario.getStockDisponible()) {

            throw new StockNoValidoException(
                    "Hay copias comprometidas."
            );
        }
    }

    private void validarCreate(InventarioItemCreateOrReplaceRequest request) throws IllegalArgumentException {

        validarStock(request.getStockDisponible(), request.getStockTotal());

        boolean exists = inventarioItemRepository
                .existsByVideojuego_VideojuegoIdAndPlataforma(
                        request.getVideojuegoId(),
                        request.getPlataforma()
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Ya existe un inventario para ese videojuego en la misma plataforma."
            );
        }

    }

    private void validarUpdate(
            Integer videojuegoId,
            Plataformas plataforma,
            Integer inventarioIdActual
    ) {

        if (videojuegoId == null || plataforma == null) {
            return;
        }

        boolean exists = inventarioItemRepository
                .existsByVideojuego_VideojuegoIdAndPlataformaAndInventarioItemIdNot(
                        videojuegoId,
                        plataforma,
                        inventarioIdActual
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "Ya existe un inventario para ese videojuego en la misma plataforma."
            );
        }
    }

    private InventarioItemResponse mapearResponse(InventarioItemEntity inventario) {
        return inventarioItemMapper.toResponse(
                inventario,
                videojuegoMapper.toResponse(
                        inventario.getVideojuego()
                )
        );
    }

    private InventarioItemResponse mapearInventario(InventarioItemEntity inventario) {
        VideojuegoResponse videojuegoResponse =
                videojuegoMapper.toResponse(inventario.getVideojuego());

        return inventarioItemMapper.toResponse(inventario, videojuegoResponse);
    }

    private void validarStock(Integer disponible, Integer total) {
        if (disponible != null && total != null && disponible > total) {
            throw new IllegalArgumentException(
                    "El stock disponible no puede ser mayor que el stock total."
            );
        }
    }

}


