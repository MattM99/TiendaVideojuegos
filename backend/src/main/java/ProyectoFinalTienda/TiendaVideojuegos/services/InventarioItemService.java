package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioItemResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioItemNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.InventarioItemMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public InventarioItemResponse guardar(InventarioItemCreateOrReplaceRequest request) {
        VideojuegoEntity videojuego = videojuegoRepository.findById(request.getVideojuegoId()).
                orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + request.getVideojuegoId() + " no encontrado."));

        if(validarDatosDeInventario(request)){
            InventarioItemEntity entity = inventarioItemMapper.toEntity(request, videojuego);
            // Validar que el stock sea correcto antes de guardar
            //entity.validarStock();
            return inventarioItemMapper.toResponse(inventarioItemRepository.save(entity), videojuegoMapper.toResponse(videojuego));
        }else{
            throw new IllegalArgumentException("Validaciones fallidas para el inventario.");
        }
    }

    public void eliminar(int id){
        if (!inventarioItemRepository.existsById(id)) {
            throw new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado.");
        }
        inventarioItemRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<InventarioItemResponse> obtenerTodos(){
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findAll();
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario en el sistema.");
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por id con excepción si no existe
    public InventarioItemResponse buscarPorId(int id){
        InventarioItemEntity entity = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));
        return inventarioItemMapper.toResponse(entity, videojuegoMapper.toResponse(entity.getVideojuego()));
    }

    // Buscar por videojuego con excepción si lista vacía
    public List<InventarioItemResponse> buscarPorVideojuego(int videojuegoId) {
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByVideojuegoId(videojuegoId);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con la id del videojuego: " + videojuegoId);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por plataforma con excepción si lista vacía
    public List<InventarioItemResponse> buscarPorPlataforma(String plataforma) throws IllegalArgumentException{
        if (!esPlataformaValida(plataforma)) {
            throw new IllegalArgumentException("La plataforma ingresada no es válida: " + plataforma);
        }

        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByPlataforma(Plataformas.valueOf(plataforma.trim().toUpperCase()));
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con la plataforma: " + plataforma);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por precio menor que valor, validando lista vacía
    public List<InventarioItemResponse> buscarMasBaratosQue(double valor){
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByPrecioDiarioLessThan(valor);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con precio menor a: " + valor);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Buscar por plataforma y precio menor que valor, validando lista vacía
    public List<InventarioItemResponse> buscarPorPlataformaMasBaratosQue(String plataforma, double valor){
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        if (!esPlataformaValida(plataforma)) {
            throw new IllegalArgumentException("La plataforma ingresada no es válida: " + plataforma);
        }
        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByPlataformaAndPrecioDiarioLessThan(Plataformas.valueOf(plataforma.trim().toUpperCase()), valor);
        if (inventarios.isEmpty()) {
            throw new InventarioItemNoEncontradoException("No se encontró ningún inventario con plataforma " + plataforma + " y precio menor a " + valor);
        }
        return inventarioItemMapper.toResponseList(inventarios);
    }

    // Obtener stock total, lanzar excepción si null
    public Integer obtenerStockTotal(int inventarioId) {
        Integer stockTotal = inventarioItemRepository.findStockTotalByInventarioId(inventarioId);
        if (stockTotal == null) {
            throw new InventarioItemNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockTotal;
    }

    // Obtener stock disponible, lanzar excepción si null
    public Integer obtenerStockDisponible(int inventarioId) {
        Integer stockDisponible = inventarioItemRepository.findStockDisponibleByInventarioId(inventarioId);
        if (stockDisponible == null) {
            throw new InventarioItemNoEncontradoException("No se encontró inventario con id: " + inventarioId);
        }
        return stockDisponible;
    }

    // Obtener stock alquilado, lanzar excepción si null
//    public Integer obtenerStockAlquilado(int inventarioId) {
//        Integer stockAlquilado = inventarioItemRepository.findStockAlquiladoByInventarioId(inventarioId);
//        if (stockAlquilado == null) {
//            throw new InventarioItemNoEncontradoException("No se encontró inventario con id: " + inventarioId);
//        }
//        return stockAlquilado;
//    }

    // Obtener stock descartado, lanzar excepción si null
//    public Integer obtenerStockDescartado(int inventarioId) {
//        Integer stockDescartado = inventarioItemRepository.findStockDescartadoByInventarioId(inventarioId);
//        if (stockDescartado == null) {
//            throw new InventarioItemNoEncontradoException("No se encontró inventario con id: " + inventarioId);
//        }
//        return stockDescartado;
//    }

    public InventarioItemResponse actualizarCompleto(int id, InventarioItemCreateOrReplaceRequest nuevosDatos) {
        InventarioItemEntity existente = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id " + id + " no encontrado."));

        existente.setPrecioDiario(nuevosDatos.getPrecioDiario());
        existente.setStockTotal(nuevosDatos.getStockTotal());
        existente.setStockDisponible(nuevosDatos.getStockDisponible());

        validarStock(existente);

        return inventarioItemMapper.toResponse(inventarioItemRepository.save(existente), videojuegoMapper.toResponse(existente.getVideojuego()));
    }

    public InventarioItemResponse actualizarPorCampo(int id, InventarioItemUpdateRequest datosActualizados) {
        InventarioItemEntity existente = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));

        inventarioItemMapper.actualizarEntity(existente, datosActualizados);
        validarStock(existente);

        return inventarioItemMapper.toResponse(inventarioItemRepository.save(existente), videojuegoMapper.toResponse(existente.getVideojuego()));
    }


    public boolean validarDatosDeInventario (InventarioItemCreateOrReplaceRequest request) throws IllegalArgumentException {

//        if(request.getStockTotal() < request.getStockAlquilado() + request.getStockDescartado()) {
//            throw new IllegalArgumentException("El stock total no puede ser menor que la suma de stock alquilado y stock descartado.");
//        }

        if(request.getStockDisponible() > request.getStockTotal()) {
            throw new IllegalArgumentException("El stock disponible no puede ser mayor que el stock total.");
        }

//        if (request.getStockAlquilado() > request.getStockTotal() - request.getStockDisponible()) {
//            throw new IllegalArgumentException("El stock alquilado no puede ser mayor que el stock total menos el stock disponible.");
//        }

//        if(request.getStockDescartado() > request.getStockTotal() - request.getStockDisponible() - request.getStockAlquilado()) {
//            throw new IllegalArgumentException("El stock descartado no puede ser mayor que el stock total menos el stock disponible y el stock alquilado.");
//        }

        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByVideojuegoId(request.getVideojuegoId());

        if (inventarios.stream().anyMatch(i -> i.getPlataforma() == request.getPlataforma())) {
            throw new IllegalArgumentException("Ya existe un inventario para el videojuego con la misma plataforma.");
        }

        return true;
    }

    public boolean esStockValido(InventarioItemEntity inventario) {
        return inventario.getStockDisponible() <= inventario.getStockTotal();
    }

    public void validarStock(InventarioItemEntity inventario) {
        if (!esStockValido(inventario)) {
            throw new IllegalStateException("La suma de los stocks excede el stock total.");
        }
    }

    public boolean esPlataformaValida(String PlataformaStr) {
        try {
            Plataformas.valueOf(PlataformaStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}


