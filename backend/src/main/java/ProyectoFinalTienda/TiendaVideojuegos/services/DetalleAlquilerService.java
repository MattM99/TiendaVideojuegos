package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DetalleAlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.*;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.DetalleAlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.DetalleAlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleAlquilerService {

    @Autowired
    private DetalleAlquilerRepository detalleAlquilerRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private DetalleAlquilerMapper detalleAlquilerMapper;


    public DetalleAlquilerResponse crearDetalle(DetalleAlquilerCreateOrReplaceRequest request) {
        AlquilerEntity alquiler = alquilerRepository.findById(request.getAlquiler_id()).orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + request.getAlquiler_id() + " no encontrado."));

        InventarioEntity inventario = inventarioRepository.findById(request.getInventario_id()).orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id: " + request.getInventario_id() + " no encontrado."));

        // Validar stock disponible
        if (inventario.getStockDisponible() <= 0) {
            throw new BusinessException("No hay stock disponible para este videojuego.");
        }

        // Actualizar stock
        inventario.setStockDisponible(inventario.getStockDisponible() - 1);
        inventario.setStockAlquilado(inventario.getStockAlquilado() + 1);

        DetalleAlquilerEntity detalle = detalleAlquilerMapper.toEntity(request, alquiler, inventario);

        // Este métod puede lanzar IllegalArgumentException si las fechas son inválidas
        detalle.calcularSubtotal();

        return detalleAlquilerMapper.toResponse(detalleAlquilerRepository.save(detalle));
    }

    public void eliminar(int id){

        DetalleAlquilerEntity detalle = detalleAlquilerRepository.findById(id)
                .orElseThrow(() -> new DetalleAlquilerNoEncontradoException("Detalle de alquiler con id: " + id + " no encontrado."));

        InventarioEntity inventario = detalle.getInventario();

        // Actualizar stock
        inventario.setStockDisponible(inventario.getStockDisponible() + 1);
        inventario.setStockAlquilado(inventario.getStockAlquilado() - 1);

        detalleAlquilerRepository.deleteById(id);
    }
 /*
    // Buscar por id con excepción si no existe
    public DetalleAlquilerResponse buscarPorId(int id){
        DetalleAlquilerEntity entity = detalleAlquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado."));
        return detalleAlquilerMapper.toResponse(entity);
    }

    public List<DetalleAlquilerResponse> buscarPorId(int personaId){
        List<DetalleAlquilerResponse> alquileres = detalleAlquilerRepository.findByPersonaId(personaId);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return detalleAlquilerMapper.toResponse(alquileres);
    }
    */
}
