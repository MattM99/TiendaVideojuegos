package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.InventarioMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private InventarioMapper inventarioMapper;
    @Autowired
    private VideojuegoMapper videojuegoMapper;

    public InventarioResponse guardar(InventarioCreateOrReplaceRequest request) {
        VideojuegoEntity videojuego = videojuegoRepository.findById(request.getVideojuegoId()).
                orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + request.getVideojuegoId() + " no encontrado."));

        if(validarDatosDeInventario(request)){
            InventarioEntity entity = inventarioMapper.toEntity(request, videojuego);
            // Validar que el stock sea correcto antes de guardar
            //entity.validarStock();
            return inventarioMapper.toResponse(inventarioRepository.save(entity), videojuegoMapper.toResponse(videojuego));
        }else{
            throw new IllegalArgumentException("Validaciones fallidas para el inventario.");
        }
    }

    public void eliminar(int id){
        if (!inventarioRepository.existsById(id)) {
            throw new InventarioNoEncontradoException("Inventario con id: " + id + " no encontrado.");
        }
        inventarioRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<InventarioResponse> obtenerTodos(){
        List<InventarioEntity> inventarios = inventarioRepository.findAll();
        if (inventarios.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario en el sistema.");
        }
        return inventarioMapper.toResponseList(inventarios);
    }

    // Buscar por id con excepción si no existe
    public InventarioResponse buscarPorId(int id){
        InventarioEntity entity = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id: " + id + " no encontrado."));
        return inventarioMapper.toResponse(entity, videojuegoMapper.toResponse(entity.getVideojuego()));
    }

    // Buscar por videojuego con excepción si lista vacía
    public List<InventarioResponse> buscarPorVideojuego(int videojuegoId) {
        List<InventarioEntity> responses = inventarioRepository.findByVideojuegoId(videojuegoId);
        if (responses.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con la id del videojuego: " + videojuegoId);
        }
        return inventarioMapper.toResponseList(responses);
    }

    // Buscar por plataforma con excepción si lista vacía
    public List<InventarioResponse> buscarPorPlataforma(String plataforma) throws IllegalArgumentException{
        if (!esPlataformaValida(plataforma)) {
            throw new IllegalArgumentException("La plataforma ingresada no es válida: " + plataforma);
        }

        List<InventarioEntity> responses = inventarioRepository.findByPlataforma(Plataformas.valueOf(plataforma.trim().toUpperCase()));
        if (responses.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con la plataforma: " + plataforma);
        }
        return inventarioMapper.toResponseList(responses);
    }

    // Buscar por precio menor que valor, validando lista vacía
    public List<InventarioResponse> buscarMasBaratosQue(double valor){
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        List<InventarioEntity> responses = inventarioRepository.findByPrecioUnitarioDiarioLessThan(valor);
        if (responses.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con precio menor a: " + valor);
        }
        return inventarioMapper.toResponseList(responses);
    }

    // Buscar por plataforma y precio menor que valor, validando lista vacía
    public List<InventarioResponse> buscarPorPlataformaMasBaratosQue(String plataforma, double valor){
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        if (!esPlataformaValida(plataforma)) {
            throw new IllegalArgumentException("La plataforma ingresada no es válida: " + plataforma);
        }
        List<InventarioEntity> responses = inventarioRepository.findByPlataformaAndPrecioUnitarioDiarioLessThan(Plataformas.valueOf(plataforma.trim().toUpperCase()), valor);
        if (responses.isEmpty()) {
            throw new InventarioNoEncontradoException("No se encontró ningún inventario con plataforma " + plataforma + " y precio menor a " + valor);
        }
        return inventarioMapper.toResponseList(responses);
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

    public InventarioResponse actualizarCompleto(int id, InventarioCreateOrReplaceRequest nuevosDatos) {
        InventarioEntity existente = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id " + id + " no encontrado."));

        existente.setPrecioUnitarioDiario(nuevosDatos.getPrecioUnitarioDiario());
        existente.setStockTotal(nuevosDatos.getStockTotal());
        existente.setStockDisponible(nuevosDatos.getStockDisponible());
        existente.setStockAlquilado(nuevosDatos.getStockAlquilado());
        existente.setStockDescartado(nuevosDatos.getStockDescartado());

        validarStock(existente);

        return inventarioMapper.toResponse(inventarioRepository.save(existente), videojuegoMapper.toResponse(existente.getVideojuego()));
    }

    public InventarioResponse actualizarPorCampo(int id, InventarioUpdateRequest datosActualizados) {
        InventarioEntity existente = inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario con id: " + id + " no encontrado."));

        inventarioMapper.actualizarEntity(existente, datosActualizados);
        validarStock(existente);

        return inventarioMapper.toResponse(inventarioRepository.save(existente), videojuegoMapper.toResponse(existente.getVideojuego()));
    }


    public boolean validarDatosDeInventario (InventarioCreateOrReplaceRequest request) throws IllegalArgumentException {

        if(request.getStockTotal() < request.getStockAlquilado() + request.getStockDescartado()) {
            throw new IllegalArgumentException("El stock total no puede ser menor que la suma de stock alquilado y stock descartado.");
        }

        if(request.getStockDisponible() > request.getStockTotal()) {
            throw new IllegalArgumentException("El stock disponible no puede ser mayor que el stock total.");
        }

        if (request.getStockAlquilado() > request.getStockTotal() - request.getStockDisponible()) {
            throw new IllegalArgumentException("El stock alquilado no puede ser mayor que el stock total menos el stock disponible.");
        }

        if(request.getStockDescartado() > request.getStockTotal() - request.getStockDisponible() - request.getStockAlquilado()) {
            throw new IllegalArgumentException("El stock descartado no puede ser mayor que el stock total menos el stock disponible y el stock alquilado.");
        }

        List<InventarioEntity> inventarios = inventarioRepository.findByVideojuegoId(request.getVideojuegoId());

        if (inventarios.stream().anyMatch(i -> i.getPlataforma() == request.getPlataforma())) {
            throw new IllegalArgumentException("Ya existe un inventario para el videojuego con la misma plataforma.");
        }

        return true;
    }

    public boolean esStockValido(InventarioEntity inventario) {
        return (inventario.getStockDisponible() + inventario.getStockAlquilado() + inventario.getStockDescartado()) <= inventario.getStockTotal();
    }

    public void validarStock(InventarioEntity inventario) {
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


