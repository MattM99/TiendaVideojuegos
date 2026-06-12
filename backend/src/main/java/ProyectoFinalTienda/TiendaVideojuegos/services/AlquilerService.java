package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CerrarAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PenalizacionManualRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ItemConStockInsuficienteResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.ValidarDisponibilidadResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.*;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.DetalleAlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.*;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public ValidarDisponibilidadResponse validarDisponibilidad(
            AlquilerCreateOrReplaceRequest request) {

        List<ItemConStockInsuficienteResponse> faltantes = new ArrayList<>();

        for (DetalleAlquilerRequest detalle : request.getDetalles()) {

            InventarioItemEntity inventario =
                    inventarioItemRepository.findById(detalle.getInventarioItemId())
                            .orElseThrow(() ->
                                    new InventarioItemNoEncontradoException(
                                            "Inventario con id "
                                                    + detalle.getInventarioItemId()
                                                    + " no encontrado."
                                    ));

            if (inventario.getStockDisponible() < detalle.getCantidad()) {

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
                .build();
    }

    @Transactional
    public AlquilerResponse crearAlquiler(AlquilerCreateOrReplaceRequest request) {
        PersonaEntity persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new PersonaNoEncontradaException("No existe la persona con id " + request.getPersonaId()));
        bloqueoService.verificarNoEstaEnListaNegra(request.getPersonaId()); // Verificar si la persona está en lista negra, si está lanza excepción.

        AlquilerEntity alquiler = alquilerMapper.toEntity(request, persona);
        alquiler.setEstadoAlquiler(EstadoAlquiler.EN_CURSO);

        for (DetalleAlquilerRequest d : request.getDetalles()) {
            construirDetalle(d, alquiler, persona);
        }

        alquiler.calcularMontoDiario();

        return alquilerMapper.toResponse(alquilerRepository.save(alquiler));
    }

    private void construirDetalle(@Valid DetalleAlquilerRequest request, AlquilerEntity alquiler, PersonaEntity persona) {
        InventarioItemEntity inventario = inventarioItemRepository.findById(request.getInventarioItemId())
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + request.getInventarioItemId() + " no encontrado."));

        inventario.entregarCopias(persona, request.getCantidad());

        DetalleAlquilerEntity detalle = detalleAlquilerMapper.toEntity(request, alquiler, inventario);

        detalle.calcularSubtotal();

        alquiler.agregarDetalle(detalle);
    }

//    @Transactional
//    public AlquilerResponse crearDetalle(Integer alquilerId, DetalleAlquilerRequest request) {
//
//        AlquilerEntity alquiler = alquilerRepository.findById(alquilerId)
//                .orElseThrow(() -> new AlquilerNoEncontradoException(
//                        "Alquiler con id: " + alquilerId + " no encontrado."
//                ));
//
//        construirDetalle(request, alquiler);
//
//        alquiler.calcularMontoDiario();
//
//        return alquilerMapper.toResponse(alquilerRepository.save(alquiler));
//    }

    public void eliminar(int id){
        if (!alquilerRepository.existsById(id)) {
            throw new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado.");
        }
        alquilerRepository.deleteById(id);
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

    public Page<AlquilerResponse> listarTodos(LocalDate fechaInicio, LocalDate fechaFin, Pageable paginacion)
    {
        Page<AlquilerEntity> alquileres;

        if (fechaInicio != null && fechaFin != null) {

            alquileres = alquilerRepository.findByFechaInicioBetween(
                    fechaInicio,
                    fechaFin,
                    paginacion
            );

        } else {

            alquileres = alquilerRepository.findAll(paginacion);
        }

        return alquileres
                .map(alquilerMapper::toResponse);
    }

    // Buscar por id con excepción si no existe
    public AlquilerResponse buscarPorId(int id){
        AlquilerEntity entity = alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado."));
        return alquilerMapper.toResponse(entity);
    }

    // Buscar por usuario con excepción si lista vacía
    public Page<AlquilerResponse> buscarPorUsuario(int personaId, Pageable paginacion){
        Page<AlquilerEntity> alquileres = alquilerRepository.findByPersonaId(personaId, paginacion);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return alquileres
                .map(alquilerMapper::toResponse);
    }

}
