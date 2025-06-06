package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.DetalleAlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.DetalleAlquilerNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.DetalleAlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.DetalleAlquilerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleAlquilerService {

    @Autowired
    private DetalleAlquilerRepository detalleAlquilerRepository;
    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private InventarioService inventarioService;

    public DetalleAlquilerEntity crearDetalle(DetalleAlquilerCreateOrReplaceRequest request) {
        AlquilerEntity alquiler = alquilerService.buscarPorId(request.getAlquiler_id());

        InventarioEntity inventario = inventarioService.buscarPorId(request.getInventario_id());

        // Validar stock disponible
        if (inventario.getStockDisponible() <= 0) {
            throw new BusinessException("No hay stock disponible para este videojuego.");
        }

        // Actualizar stock
        inventario.setStockDisponible(inventario.getStockDisponible() - 1);
        inventario.setStockAlquilado(inventario.getStockAlquilado() + 1);

        DetalleAlquilerEntity detalle = request.toEntity(alquiler, inventario);

        // Este métod puede lanzar IllegalArgumentException si las fechas son inválidas
        detalle.calcularSubtotal();

        return detalleAlquilerRepository.save(detalle);
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

}
