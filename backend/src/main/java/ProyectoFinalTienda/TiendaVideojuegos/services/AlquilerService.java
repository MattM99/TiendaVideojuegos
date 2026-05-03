package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.*;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.DetalleAlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private InventarioItemRepository InventarioItemRepository;
    @Autowired
    private BloqueoService bloqueoService;
    @Autowired
    private AlquilerMapper alquilerMapper;
    @Autowired
    private DetalleAlquilerMapper detalleAlquilerMapper;
    @Autowired
    InventarioItemRepository inventarioItemRepository;

    @Transactional
    public AlquilerResponse guardar(AlquilerCreateOrReplaceRequest request) {
        PersonaEntity persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new PersonaNoEncontradaException("No existe la persona con id " + request.getPersonaId()));
        bloqueoService.verificarNoEstaEnListaNegra(request.getPersonaId()); // Verificar si la persona está en lista negra, si está lanza excepción.

        AlquilerEntity alquiler = alquilerMapper.toEntity(request, persona);
        alquiler.setEstadoAlquiler(EstadoAlquiler.EN_CURSO);

        for (DetalleAlquilerRequest d : request.getDetalles()) {
            construirDetalle(d, alquiler);
        }

        alquiler.calcularTotal();

        return alquilerMapper.toResponse(alquilerRepository.save(alquiler));
    }

    private void construirDetalle(@Valid DetalleAlquilerRequest request, AlquilerEntity alquiler) {
        InventarioItemEntity inventario = inventarioItemRepository.findById(request.getInventarioItemId())
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + request.getInventarioItemId() + " no encontrado."));

        // Validar stock disponible
        if (inventario.getStockDisponible() < request.getCantidad()) {
            throw new BusinessException("Stock insuficiente para el item con id: " + request.getInventarioItemId());
        }

        // Actualizar stock
        inventario.setStockDisponible(inventario.getStockDisponible() - request.getCantidad());

        DetalleAlquilerEntity detalle = detalleAlquilerMapper.toEntity(request, alquiler, inventario);

        detalle.calcularSubtotal();

        alquiler.agregarDetalle(detalle);
    }

    @Transactional
    public AlquilerResponse crearDetalle(Integer alquilerId, DetalleAlquilerRequest request) {

        AlquilerEntity alquiler = alquilerRepository.findById(alquilerId)
                .orElseThrow(() -> new AlquilerNoEncontradoException(
                        "Alquiler con id: " + alquilerId + " no encontrado."
                ));

        construirDetalle(request, alquiler);

        alquiler.calcularTotal();

        return alquilerMapper.toResponse(alquilerRepository.save(alquiler));
    }

    public void eliminar(int id){
        if (!alquilerRepository.existsById(id)) {
            throw new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado.");
        }
        alquilerRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<AlquilerResponse> obtenerTodos(){
        List<AlquilerEntity> alquileres = alquilerRepository.findAll();
        if (alquileres.isEmpty()) {
            throw new AlquilerNoEncontradoException("No se encontró ningún alquiler en el sistema.");
        }
        return alquilerMapper.toResponseList(alquileres);
    }

    // Buscar por id con excepción si no existe
    public AlquilerResponse buscarPorId(int id){
        AlquilerEntity entity = alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado."));
        return alquilerMapper.toResponse(entity);
    }

    // Buscar por usuario con excepción si lista vacía
    public List<AlquilerResponse> buscarPorUsuario(int personaId){
        List<AlquilerEntity> alquileres = alquilerRepository.findByPersonaId(personaId);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return alquilerMapper.toResponseList(alquileres);
    }

}
