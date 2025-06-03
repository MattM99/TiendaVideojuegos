package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    public List<InventarioEntity> obtenerTodos(){
        return inventarioRepository.findAll();
    }

    public InventarioEntity guardar(InventarioEntity inventario){
        return inventarioRepository.save(inventario);
    }

    public void eliminar(int id){
        inventarioRepository.deleteById(id);
    }

    public Optional<InventarioEntity> buscarPorId(int id){
        return inventarioRepository.findById(id);
    }

    public List<InventarioEntity> buscarPorPlataforma(Plataformas plataforma){
        return inventarioRepository.findByPlatform(plataforma);
    }

    public List<InventarioEntity> buscarMasBaratosQue(double valor){
        return inventarioRepository.findByPrecioUnitarioDiarioLessThan(valor);
    }

    public List<InventarioEntity> buscarPorPlataformaMasBaratosQue(Plataformas plataforma, double valor){
        return inventarioRepository.findByPlataformaAndPrecioUnitarioDiarioLessThan(plataforma, valor);
    }

    public Integer obtenerStockTotal(int inventarioId) {
        return inventarioRepository.findStockTotalByInventarioId(inventarioId);
    }

    public Integer obtenerStockDisponible(int inventarioId) {
        return inventarioRepository.findStockDisponibleByInventarioId(inventarioId);
    }

    public Integer obtenerStockAlquilado(int inventarioId) {
        return inventarioRepository.findStockAlquiladoByInventarioId(inventarioId);
    }

    public Integer obtenerStockDescartado(int inventarioId) {
        return inventarioRepository.findStockDescartadoByInventarioId(inventarioId);
    }
}
