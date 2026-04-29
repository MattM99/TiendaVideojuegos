package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.AlquilerNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private DetalleAlquilerService detalleAlquilerService;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private InventarioItemRepository InventarioItemRepository;
    @Autowired
    private BloqueoService bloqueoService;
    @Autowired
    private AlquilerMapper alquilerMapper;
    @Autowired
    InventarioItemRepository inventarioItemRepository;

    @Transactional
    public AlquilerResponse guardar(AlquilerCreateOrReplaceRequest request) {
        PersonaEntity persona = personaRepository.findById(request.getPersonaId()).orElseThrow();
        bloqueoService.verificarNoEstaEnListaNegra(request.getPersonaId()); // Verificar si la persona está en lista negra, si está lanza excepción.

        AlquilerEntity entity = alquilerMapper.toEntity(request, persona);
        entity.setEstadoAlquiler(EstadoAlquiler.EN_CURSO);

        for (DetalleAlquilerRequest d : request.getDetalles()) {
            InventarioItemEntity inventarioItem = inventarioItemRepository.findById(d.getInventarioItemId()).orElseThrow(() -> new BusinessException("Inventario item con id: " + d.getInventarioItemId() + " no encontrado."));

            if (inventarioItem.getStockDisponible() < d.getCantidad()) {
                throw new BusinessException("Stock insuficiente para el item con id: " + d.getInventarioItemId());
            }
            inventarioItem.setStockDisponible(
                    inventarioItem.getStockDisponible() - d.getCantidad()
            );

            DetalleAlquilerEntity detalle = DetalleAlquilerEntity.builder()
                    .inventarioItem(inventarioItem)
                    .cantidad(d.getCantidad())
                    .build();

            entity.agregarDetalle(detalle);
            detalle.calcularSubtotal();
        }
        return alquilerMapper.toResponse(alquilerRepository.save(entity));
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
