package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private VideojuegoService videojuegoService;

    public InventarioEntity guardar(InventarioCreateOrReplaceRequest request) {
        VideojuegoEntity videojuego = videojuegoService.buscarPorId(request.getVideojuegoId());

        InventarioEntity entity = request.toEntity(videojuego);
        entity.validarStock();

        return inventarioRepository.save(entity);
    }

    public void eliminar(int id){
        if (!inventarioRepository.existsById(id)) {
            throw new InventarioNoEncontradoException("Inventario con id: " + id + " no encontrado.");
        }
        inventarioRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<InventarioEntity> obtenerTodos(){
        List<InventarioEntity> inventarios = inventarioRepository.findAll();
        if (inventarios.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario en el sistema.");
        }
        return inventarios;
    }

    // Buscar por id con excepción si no existe
    public InventarioEntity buscarPorId(int id){
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id: " + id + " no encontrado."));
    }

    // Buscar por videojuego con excepción si lista vacía
    public List<InventarioEntity> buscarPorVideojuego(int videojuegoId) {
        List<InventarioEntity> inventarios = inventarioRepository.findByVideojuegoId(videojuegoId);
        if (inventarios.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con la id del videojuego: " + videojuegoId);
        }
        return inventarios;
    }

    // Buscar por plataforma con excepción si lista vacía
    public List<InventarioEntity> buscarPorPlataforma(Plataformas plataforma){
        List<InventarioEntity> inventarios = inventarioRepository.findByPlataforma(plataforma);
        if (inventarios.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con la plataforma: " + plataforma);
        }
        return inventarios;
    }

    // Buscar por precio menor que valor, validando lista vacía
    public List<InventarioEntity> buscarMasBaratosQue(double valor){
        List<InventarioEntity> inventarios = inventarioRepository.findByPrecioUnitarioDiarioLessThan(valor);
        if (inventarios.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con precio menor a: " + valor);
        }
        return inventarios;
    }

    // Buscar por plataforma y precio menor que valor, validando lista vacía
    public List<InventarioEntity> buscarPorPlataformaMasBaratosQue(Plataformas plataforma, double valor){
        List<InventarioEntity> inventarios = inventarioRepository.findByPlataformaAndPrecioUnitarioDiarioLessThan(plataforma, valor);
        if (inventarios.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con plataforma " + plataforma + " y precio menor a " + valor);
        }
        return inventarios;
    }

    // Obtener stock total, lanzar excepción si null
    public Integer obtenerStockTotal(int inventarioId) {
        Integer stockTotal = inventarioRepository.findStockTotalByInventarioId(inventarioId);
        if (stockTotal == null) {
            throw new InventarioNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockTotal;
    }

    // Obtener stock disponible, lanzar excepción si null
    public Integer obtenerStockDisponible(int inventarioId) {
        Integer stockDisponible = inventarioRepository.findStockDisponibleByInventarioId(inventarioId);
        if (stockDisponible == null) {
            throw new InventarioNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockDisponible;
    }

    // Obtener stock alquilado, lanzar excepción si null
    public Integer obtenerStockAlquilado(int inventarioId) {
        Integer stockAlquilado = inventarioRepository.findStockAlquiladoByInventarioId(inventarioId);
        if (stockAlquilado == null) {
            throw new InventarioNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockAlquilado;
    }

    // Obtener stock descartado, lanzar excepción si null
    public Integer obtenerStockDescartado(int inventarioId) {
        Integer stockDescartado = inventarioRepository.findStockDescartadoByInventarioId(inventarioId);
        if (stockDescartado == null) {
            throw new InventarioNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockDescartado;
    }

    public InventarioEntity actualizarCompleto(int id, InventarioCreateOrReplaceRequest nuevosDatos) {
        InventarioEntity existente = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id " + id + " no encontrado."));

        existente.setPrecioUnitarioDiario(nuevosDatos.getPrecioUnitarioDiario());
        existente.setStockTotal(nuevosDatos.getStockTotal());
        existente.setStockDisponible(nuevosDatos.getStockDisponible());
        existente.setStockAlquilado(nuevosDatos.getStockAlquilado());
        existente.setStockDescartado(nuevosDatos.getStockDescartado());

        existente.validarStock();

        return inventarioRepository.save(existente);
    }

    public InventarioEntity actualizarPorCampo(int id, InventarioUpdateRequest datosActualizados) {
        InventarioEntity existente = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id: " + id + " no encontrado."));

        datosActualizados.actualizarInventario(existente);
        existente.validarStock();

        return inventarioRepository.save(existente);
    }

}
