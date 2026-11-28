package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemEdicionRequest;
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
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        if (!videojuego.isActivo()) {
            throw new BusinessException(
                    "No se puede crear un inventario para un videojuego dado de baja."
            );
        }

        validarCreate(request);

        InventarioItemEntity entity = inventarioItemMapper.toEntity(request, videojuego);

        return mapearResponse(inventarioItemRepository.save(entity));
    }

    /// Read

    public Page<InventarioItemResponse> obtenerTodos(Pageable paginacion) {
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

    public Page<InventarioItemResponse> listarActivos(Pageable paginacion) {
        return inventarioItemRepository.findByActivoTrue(paginacion)
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
                inventarioItemRepository.findByVideojuego_VideojuegoIdAndActivoTrue(
                        videojuegoId,
                        paginacion
                );
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con la id del videojuego: " + videojuegoId);
        }
        return inventarios.map(this::mapearInventario);
    }

    public Page<InventarioItemResponse> buscarPorPlataforma(
                Pageable paginacion,
                Plataformas plataforma) {

            Page<InventarioItemEntity> inventarios =
                    inventarioItemRepository.findByPlataformaAndActivoTrue(
                            plataforma,
                            paginacion
                    );

            return inventarios.map(this::mapearInventario);
    }

    public Page<InventarioItemResponse> buscarMasBaratosQue(
            double valor,
            Pageable paginacion) {

        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "El valor debe ser mayor a 0."
            );
        }

        Page<InventarioItemEntity> inventarios =
                inventarioItemRepository.findByPrecioDiarioLessThanAndActivoTrue(
                        valor,
                        paginacion
                );

        return inventarios.map(this::mapearInventario);
    }

    public Page<InventarioItemResponse> buscarPorPlataformaMasBaratosQue(
            Plataformas plataforma,
            double valor,
            Pageable paginacion) {

        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "El valor debe ser mayor a 0."
            );
        }

        Page<InventarioItemEntity> inventarios =
                inventarioItemRepository.findByPlataformaAndPrecioDiarioLessThanAndActivoTrue(
                        plataforma,
                        valor,
                        paginacion
                );

        return inventarios.map(this::mapearInventario);
    }

    /// Update

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InventarioItemResponse editarPrecio(int id, InventarioItemEdicionRequest request) {

        InventarioItemEntity existente = bloquearInventario(id);

        if (!existente.isActivo()) {
            throw new BusinessException(
                    "El inventario está dado de baja y no se puede modificar."
            );
        }

        if (!existente.getVideojuego().isActivo()) {
            throw new BusinessException(
                    "El videojuego asociado está dado de baja y no se puede modificar su inventario."
            );
        }

        validarUpdate(
                existente.getVideojuego().getVideojuegoId(),
                existente.getPlataforma(),
                id
        );

        existente.setPrecioDiario(request.precioDiario());

        return mapearResponse(inventarioItemRepository.save(existente));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InventarioItemResponse actualizarPorCampo(int id, InventarioItemUpdateRequest datosActualizados) {

        InventarioItemEntity existente = bloquearInventario(id);

        if (!existente.isActivo()) {
            throw new BusinessException(
                    "El inventario está dado de baja y no se puede modificar."
            );
        }

        if (!existente.getVideojuego().isActivo()) {
            throw new BusinessException(
                    "El videojuego asociado está dado de baja y no se puede modificar su inventario."
            );
        }

        validarUpdate(
                existente.getVideojuego().getVideojuegoId(),
                existente.getPlataforma(),
                id
        );

        int stockDisponibleAnterior = existente.getStockDisponible();
        ajustarStock(existente, datosActualizados.getStockTotal(), datosActualizados.getStockDisponible());
        if (datosActualizados.getPrecioDiario() != null &&
                datosActualizados.getPrecioDiario().compareTo(BigDecimal.ZERO) > 0) {
            existente.setPrecioDiario(datosActualizados.getPrecioDiario());
        }

        InventarioItemEntity guardado = inventarioItemRepository.save(existente);
        if (guardado.getStockDisponible() > stockDisponibleAnterior) {
            eventPublisher.publishEvent(new StockDisponibleEvent(guardado.getInventarioItemId()));
        }
        return mapearResponse(guardado);
    }

    private void ajustarStock(InventarioItemEntity inventario, Integer totalSolicitado, Integer disponibleSolicitado) {
        int total = totalSolicitado != null && totalSolicitado >= 0 ? totalSolicitado : inventario.getStockTotal();
        int disponible = disponibleSolicitado != null && disponibleSolicitado >= 0
                ? disponibleSolicitado : inventario.getStockDisponible();
        validarStock(disponible, total);

        if (total == inventario.getStockTotal() && disponible == inventario.getStockDisponible()) {
            return;
        }

        long alquiladas = inventarioItemRepository.contarUnidadesAlquiladas(
                inventario.getInventarioItemId(), EstadoAlquiler.EN_CURSO);
        // La unidad sigue apartada hasta que el proceso de vencimiento cambie el estado.
        long reservadas = inventario.getListaDeEspera().stream()
                .filter(reserva -> reserva.getEstadoReserva() == EstadoReserva.NOTIFICADA)
                .count();
        if (disponible + alquiladas + reservadas > total) {
            throw new StockNoValidoException("El ajuste de stock debe respetar las unidades alquiladas o reservadas.");
        }

        inventario.setStockTotal(total);
        inventario.setStockDisponible(disponible);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InventarioItemResponse agregarStock(int id, int cantidad) {

        InventarioItemEntity existente = bloquearInventario(id);

        if (!existente.isActivo()) {
            throw new BusinessException(
                    "El inventario está dado de baja y no se puede agregar stock."
            );
        }

        if (!existente.getVideojuego().isActivo()) {
            throw new BusinessException(
                    "El videojuego asociado está dado de baja y no se puede agregar stock."
            );
        }

        existente.aumentarStockTotal(cantidad);
        existente.aumentarStockDisponible(cantidad);

        InventarioItemEntity guardado =
                inventarioItemRepository.save(existente);

        eventPublisher.publishEvent(
                new StockDisponibleEvent(
                        guardado.getInventarioItemId()
                )
        );

        System.out.println(
                "Evento publicado para inventario " + guardado.getInventarioItemId()
        );

        return mapearResponse(guardado);
    }

    // Solo para pruebas
//    public InventarioItemResponse disminuirStockDisponible(int id, int cantidad) {
//
//        InventarioItemEntity existente = obtenerInventarioPorId(id);
//        existente.disminuirStockDisponible(cantidad);
//        return mapearResponse(inventarioItemRepository.save(existente));
//    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void devolverVideojuego(int inventarioId, int cantidad) {
        InventarioItemEntity inventario = bloquearInventario(inventarioId);
        inventario.aumentarStockDisponible(cantidad);

        eventPublisher.publishEvent(
                new StockDisponibleEvent(
                        inventario.getInventarioItemId()
                )
        );
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void eliminar(int id) {
        InventarioItemEntity inventario = bloquearInventario(id);

        if (!inventario.isActivo()) {
            throw new BusinessException(
                    "El inventario ya se encuentra dado de baja."
            );
        }

        validarSinCompromisos(inventario);

        inventario.setActivo(false);
        inventarioItemRepository.save(inventario);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InventarioItemResponse darDeBaja(int id) {
        InventarioItemEntity existente = bloquearInventario(id);

        if (!existente.isActivo()) {
            throw new BusinessException(
                    "El inventario ya se encuentra dado de baja."
            );
        }

        validarSinCompromisos(existente);

        existente.setActivo(false);

        return mapearResponse(inventarioItemRepository.save(existente));
    }

    /// Aggregate root

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservaResponse crearReserva(
            Integer inventarioId,
            ReservaRequest request
    ) {

        PersonaEntity persona = personaRepository.findByDni(
                        request.getPersonaDni()
                )
                .orElseThrow(() ->
                        new PersonaNoEncontradaException(
                                "Persona no encontrada con DNI: "
                                        + request.getPersonaDni()
                        ));

        if (!persona.isActivo()) {
            throw new BusinessException(
                    "La persona está dada de baja y no puede realizar reservas."
            );
        }

        InventarioItemEntity inventario = bloquearInventario(inventarioId);

        if (!inventario.isActivo()) {
            throw new BusinessException(
                    "El inventario está dado de baja y no se pueden realizar reservas."
            );
        }

        if (!inventario.getVideojuego().isActivo()) {
            throw new BusinessException(
                    "El videojuego asociado está dado de baja y no se pueden realizar reservas."
            );
        }

        if (inventario.getStockDisponible() > 0) {
            throw new BusinessException(
                    "El videojuego tiene stock disponible, no es necesario reservar."
            );
        }

        boolean yaReservo = inventario.getListaDeEspera().stream()
                .anyMatch(r ->
                        r.getPersona().getDni().equals(persona.getDni())
                                && r.getEstadoReserva() == EstadoReserva.PENDIENTE
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

    private InventarioItemEntity bloquearInventario(int id) {
        return inventarioItemRepository.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + id + " no encontrado."
                ));
    }

    private InventarioItemEntity obtenerInventarioPorId(int id) {
        return inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + id + " no encontrado."
                ));
    }

    private void validarSinCompromisos(InventarioItemEntity inventario) {

        if (inventario.getStockTotal() != inventario.getStockDisponible()) {
            throw new StockNoValidoException(
                    "Hay copias comprometidas."
            );
        }

        boolean tieneReservasActivas = inventario.getListaDeEspera()
                .stream()
                .anyMatch(reserva ->
                        reserva.getEstadoReserva() == EstadoReserva.PENDIENTE
                                || reserva.getEstadoReserva() == EstadoReserva.NOTIFICADA
                );

        if (tieneReservasActivas) {
            throw new BusinessException(
                    "No se puede dar de baja el inventario porque tiene reservas pendientes o notificadas."
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
            throw new BusinessException(
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


