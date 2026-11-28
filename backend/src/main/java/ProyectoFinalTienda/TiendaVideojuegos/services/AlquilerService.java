package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CerrarAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PenalizacionManualRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ItemConStockInsuficienteResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ValidarDisponibilidadResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ValidarDisponibilidadResponse.DisponibilidadItem;
import ProyectoFinalTienda.TiendaVideojuegos.exception.*;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.DetalleAlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.*;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private BloqueoService bloqueoService;
    @Autowired
    private AlquilerMapper alquilerMapper;
    @Autowired
    private DetalleAlquilerMapper detalleAlquilerMapper;
    @Autowired
    private InventarioItemRepository inventarioItemRepository;
    @Autowired
    private InventarioItemService inventarioItemService;

    /// Create

    @Transactional
    public AlquilerResponse crearAlquiler(AlquilerCreateOrReplaceRequest request) {
        PersonaEntity persona = obtenerPersonaHabilitada(request.getPersonaDni());
        validarDetalles(request.getDetalles());

        AlquilerEntity alquiler = alquilerMapper.toEntity(request, persona);
        alquiler.setEstadoAlquiler(EstadoAlquiler.EN_CURSO);

        for (DetalleAlquilerRequest detalle : request.getDetalles()) {
            construirDetalle(detalle, alquiler, persona);
        }

        alquiler.calcularMontoDiario();

        return alquilerMapper.toResponse(alquilerRepository.save(alquiler));
    }

    private PersonaEntity obtenerPersonaHabilitada(String dni) {
        PersonaEntity persona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new PersonaNoEncontradaException("No existe la persona con DNI " + dni));

        if (!persona.isActivo()) {
            throw new BusinessException(
                    "La persona está dada de baja y no puede realizar alquileres."
            );
        }

        bloqueoService.verificarNoEstaEnListaNegra(dni);
        return persona;
    }

    private void construirDetalle(DetalleAlquilerRequest request, AlquilerEntity alquiler, PersonaEntity persona) {
        InventarioItemEntity inventario = obtenerInventarioHabilitado(request.getInventarioItemId());
        DisponibilidadItem disponibilidad = evaluarDisponibilidad(persona, inventario, request.getCantidad());
        if (disponibilidad.motivo() != null) {
            throw new BusinessException(disponibilidad.motivo());
        }

        inventario.entregarCopias(persona, request.getCantidad());

        DetalleAlquilerEntity detalle = detalleAlquilerMapper.toEntity(request, alquiler, inventario);
        detalle.calcularSubtotal();
        alquiler.agregarDetalle(detalle);
    }

    private InventarioItemEntity obtenerInventarioHabilitado(int inventarioId) {
        InventarioItemEntity inventario = inventarioItemRepository.findById(inventarioId)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + inventarioId + " no encontrado."));

        if (!inventario.isActivo()) {
            throw new BusinessException(
                    "El inventario está dado de baja y no se puede alquilar."
            );
        }

        if (!inventario.getVideojuego().isActivo()) {
            throw new BusinessException(
                    "El videojuego asociado está dado de baja y no se puede alquilar."
            );
        }

        return inventario;
    }

    /// Read

    public Page<AlquilerResponse> listarActivos(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable paginacion){

        Page<AlquilerEntity> alquileres;

        if (fechaInicio != null && fechaFin != null) {

            alquileres = alquilerRepository.findByFechaInicioBetweenAndActivoTrue(
                    fechaInicio,
                    fechaFin,
                    paginacion
            );

        } else {

            alquileres = alquilerRepository.findByActivoTrue(paginacion);
        }

        return alquileres.map(alquilerMapper::toResponse);
    }

    public Page<AlquilerResponse> obtenerTodos(Pageable paginacion) {

        return alquilerRepository.findAll(paginacion)
                .map(alquilerMapper::toResponse);
    }

    public AlquilerResponse buscarPorId(int id) {
        AlquilerEntity entity = alquilerRepository.findByAlquilerIdAndActivoTrue(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException(
                        "Alquiler con id: " + id + " no encontrado."));

        return alquilerMapper.toResponse(entity);
    }

    public AlquilerResponse buscarPorIdAdministracion(int id) {

        AlquilerEntity entity = alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException(
                        "Alquiler con id: " + id + " no encontrado."));

        return alquilerMapper.toResponse(entity);
    }

    public Page<AlquilerResponse> buscarPorUsuario(int personaId, Pageable paginacion){
        Page<AlquilerEntity> alquileres = alquilerRepository.findByPersonaId(personaId, paginacion);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return alquileres
                .map(alquilerMapper::toResponse);
    }

    /// Update

    @Transactional
    public AlquilerResponse actualizar(int id, AlquilerUpdateRequest request) {
        AlquilerEntity alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException(
                        "Alquiler con id: " + id + " no encontrado."
                ));

        if (!alquiler.isActivo()) {
            throw new BusinessException(
                    "No se puede actualizar un alquiler dado de baja."
            );
        }

        if (alquiler.getEstadoAlquiler() != EstadoAlquiler.EN_CURSO) {
            throw new BusinessException(
                    "Solo se pueden actualizar alquileres en curso."
            );
        }

        LocalDate nuevaFechaFin = request.getFechaFin();

        if (nuevaFechaFin == null) {
            throw new BusinessException(
                    "La fecha de fin es obligatoria."
            );
        }

        if (!nuevaFechaFin.isAfter(LocalDate.now())) {
            throw new BusinessException(
                    "La fecha de fin debe estar en el futuro."
            );
        }

        if (!nuevaFechaFin.isAfter(alquiler.getFechaInicio())) {
            throw new BusinessException(
                    "La fecha de fin debe ser posterior a la fecha de inicio."
            );
        }

        alquiler.setFechaFin(nuevaFechaFin);

        return alquilerMapper.toResponse(
                alquilerRepository.save(alquiler)
        );
    }

    @Transactional
    public AlquilerResponse cerrarAlquiler(
            Integer alquilerId,
            CerrarAlquilerRequest request
    ) {

        AlquilerEntity alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new AlquilerNoEncontradoException(
                        "Alquiler con id: " + alquilerId + " no encontrado."
                ));

        if (!alquiler.isActivo()) {
            throw new BusinessException(
                    "No se puede cerrar un alquiler dado de baja."
            );
        }

        if (alquiler.getEstadoAlquiler() != EstadoAlquiler.EN_CURSO) {
            throw new BusinessException(
                    "Solo se pueden cerrar alquileres en curso."
            );
        }

        // registrar devolución
        alquiler.setFechaDevolucion(LocalDate.now());

        // generar penalización automática
        alquiler.generarPenalizacionPorRetraso();

        // agregar penalizaciones manuales
        for (PenalizacionManualRequest p : request.getPenalizaciones()) {

            PenalizacionEntity penalizacion = PenalizacionEntity.builder()
                    .motivo(p.getMotivo())
                    .monto(p.getMonto())
                    .build();

            alquiler.agregarPenalizacion(penalizacion);
        }

        // crear pago
        PagoEntity pago = PagoEntity.crear(
                alquiler,
                request.getMetodoPago(),
                request.getDescuento()
        );

        alquiler.asignarPago(pago);

        // actualizar stock de los items alquilados
        for (DetalleAlquilerEntity detalle : alquiler.getItems()) {
            InventarioItemEntity inventario = detalle.getInventarioItem();
            inventarioItemService.devolverVideojuego(inventario, detalle.getCantidad());
        }

        // finalizar alquiler
        alquiler.setEstadoAlquiler(EstadoAlquiler.FINALIZADO);

        return alquilerMapper.toResponse(
                alquilerRepository.save(alquiler)
        );
    }

    /// Delete

    @Transactional
    public void eliminar(int id) {

        AlquilerEntity alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException(
                        "Alquiler con id: " + id + " no encontrado."
                ));

        if (!alquiler.isActivo()) {
            throw new BusinessException(
                    "El alquiler ya se encuentra dado de baja."
            );
        }

        if (alquiler.getEstadoAlquiler() == EstadoAlquiler.EN_CURSO) {
            throw new BusinessException(
                    "No se puede dar de baja un alquiler que está EN_CURSO."
            );
        }

        alquiler.setActivo(false);

        alquilerRepository.save(alquiler);
    }

    /// -------------- Regla de negocio -------------- ///

    @Transactional(readOnly = true)
    public ValidarDisponibilidadResponse validarDisponibilidad(
            AlquilerCreateOrReplaceRequest request) {

        PersonaEntity persona = obtenerPersonaHabilitada(request.getPersonaDni());
        validarDetalles(request.getDetalles());

        List<DisponibilidadItem> disponibilidad = new ArrayList<>();
        List<ItemConStockInsuficienteResponse> faltantes = new ArrayList<>();

        for (DetalleAlquilerRequest detalle : request.getDetalles()) {
            InventarioItemEntity inventario = obtenerInventarioHabilitado(detalle.getInventarioItemId());
            DisponibilidadItem item = evaluarDisponibilidad(persona, inventario, detalle.getCantidad());
            disponibilidad.add(item);

            if (item.motivo() != null) {
                faltantes.add(
                        ItemConStockInsuficienteResponse.builder()
                                .inventarioItemId(inventario.getInventarioItemId())
                                .titulo(inventario.getVideojuego().getTitulo())
                                .plataforma(inventario.getPlataforma())
                                .cantidadSolicitada(detalle.getCantidad())
                                .cantidadDisponible(inventario.getStockDisponible())
                                .build()
                );
            }
        }

        return ValidarDisponibilidadResponse.builder()
                .puedeCrearAlquiler(faltantes.isEmpty())
                .faltantes(faltantes)
                .disponibilidad(disponibilidad)
                .build();
    }

    private void validarDetalles(List<DetalleAlquilerRequest> detalles) {
        Set<Integer> inventariosSolicitados = new HashSet<>();
        for (DetalleAlquilerRequest detalle : detalles) {
            if (detalle == null || detalle.getCantidad() <= 0) {
                throw new BusinessException("La cantidad debe ser mayor a 0.");
            }
            if (!inventariosSolicitados.add(detalle.getInventarioItemId())) {
                throw new BusinessException("El inventario con id " + detalle.getInventarioItemId()
                        + " aparece más de una vez en el alquiler.");
            }
        }
    }

    private DisponibilidadItem evaluarDisponibilidad(
            PersonaEntity persona, InventarioItemEntity inventario, int cantidad) {
        int inventarioId = inventario.getInventarioItemId();
        int stockLibre = inventario.getStockDisponible();
        List<ReservaEntity> reservas = inventario.getListaDeEspera();
        // Comprobar la vigencia de la misma reserva que selecciona entregarCopias().
        ReservaEntity reservaPropia = reservas.stream()
                .filter(reserva -> reserva.getEstadoReserva() == EstadoReserva.NOTIFICADA)
                .filter(reserva -> reserva.getPersona().getPersonaId() == persona.getPersonaId())
                .findFirst()
                .orElse(null);

        if (reservaPropia != null) {
            if (reservaPropia.getFechaNotificacion() == null || reservaPropia.estaVencida(LocalDateTime.now())) {
                return new DisponibilidadItem(inventarioId, cantidad, stockLibre, 0, false,
                        "La reserva ya no está vigente.");
            }
            String motivo = cantidad == 1 ? null
                    : "Una reserva permite retirar una única unidad del mismo videojuego.";
            return new DisponibilidadItem(inventarioId, cantidad, stockLibre, 1, true, motivo);
        }

        if (reservas.stream().anyMatch(reserva -> reserva.getEstadoReserva() == EstadoReserva.NOTIFICADA)) {
            return new DisponibilidadItem(inventarioId, cantidad, stockLibre, 0, false,
                    "Este videojuego está reservado para otro cliente.");
        }

        String motivo = cantidad <= stockLibre ? null
                : "El item: " + inventarioId + " no tiene suficiente stock disponible para disminuir. Copias disponibles: "
                        + stockLibre;
        return new DisponibilidadItem(inventarioId, cantidad, stockLibre, stockLibre, false, motivo);
    }

}
