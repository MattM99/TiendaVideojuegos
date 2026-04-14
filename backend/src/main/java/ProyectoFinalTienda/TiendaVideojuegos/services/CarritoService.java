package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.CarritoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.CarritoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.*;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.CarritoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CarritoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CarritoRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private CarritoMapper carritoMapper;


    public CarritoResponse crearDetalle(CarritoCreateOrReplaceRequest request) {
        AlquilerEntity alquiler = alquilerRepository.findById(request.getAlquiler_id()).orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + request.getAlquiler_id() + " no encontrado."));

        InventarioEntity inventario = inventarioRepository.findById(request.getInventario_id()).orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id: " + request.getInventario_id() + " no encontrado."));

        // Validar stock disponible
        if (inventario.getStockDisponible() <= 0) {
            throw new BusinessException("No hay stock disponible para este videojuego.");
        }

        // Actualizar stock
        inventario.setStockDisponible(inventario.getStockDisponible() - 1);
        inventario.setStockAlquilado(inventario.getStockAlquilado() + 1);

        CarritoEntity detalle = carritoMapper.toEntity(request, alquiler, inventario);

        // Este métod puede lanzar IllegalArgumentException si las fechas son inválidas
        detalle.calcularSubtotal();

        return carritoMapper.toResponse(carritoRepository.save(detalle));
    }

    public void eliminar(int id){

        CarritoEntity detalle = carritoRepository.findById(id)
                .orElseThrow(() -> new DetalleAlquilerNoEncontradoException("Detalle de alquiler con id: " + id + " no encontrado."));

        InventarioEntity inventario = detalle.getInventario();

        // Actualizar stock
        inventario.setStockDisponible(inventario.getStockDisponible() + 1);
        inventario.setStockAlquilado(inventario.getStockAlquilado() - 1);

        carritoRepository.deleteById(id);
    }
 /*
    // Buscar por id con excepción si no existe
    public CarritoResponse buscarPorId(int id){
        CarritoEntity entity = carritoRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado."));
        return carritoMapper.toResponse(entity);
    }

    public List<CarritoResponse> buscarPorId(int personaId){
        List<CarritoResponse> alquileres = carritoRepository.findByPersonaId(personaId);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return carritoMapper.toResponse(alquileres);
    }
    */
}
