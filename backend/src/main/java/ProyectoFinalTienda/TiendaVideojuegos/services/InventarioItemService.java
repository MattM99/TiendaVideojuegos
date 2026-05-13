package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.InventarioItemUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.InventarioItemResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioItemNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.StockNoValidoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.InventarioItemMapper;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        if (validarDatosDeInventario(request)) {
            InventarioItemEntity entity = inventarioItemMapper.toEntity(request, videojuego);
            // Validar que el stock sea correcto antes de guardar
            //entity.validarStock();
            return inventarioItemMapper.toResponse(inventarioItemRepository.save(entity), videojuegoMapper.toResponse(videojuego));
        } else {
            throw new IllegalArgumentException("Validaciones fallidas para el inventario.");
        }
    }

    public void eliminar(int id) {
        if (!inventarioItemRepository.existsById(id)) {
            throw new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado.");
        }
        inventarioItemRepository.deleteById(id);
    }


    public Page<InventarioItemResponse> listarTodos(Pageable paginacion) {
        return inventarioItemRepository.findAll(paginacion)
                .map(inventario -> {

                    VideojuegoResponse videojuegoResponse =
                            videojuegoMapper.toResponse(
                                    inventario.getVideojuego()
                            );

                    return inventarioItemMapper.toResponse(
                            inventario,
                            videojuegoResponse
                    );
                });
    }

    // Buscar por id con excepción si no existe
    public InventarioItemResponse buscarPorId(int id) {
        InventarioItemEntity entity = inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException("Inventario con id: " + id + " no encontrado."));
        return inventarioItemMapper.toResponse(entity, videojuegoMapper.toResponse(entity.getVideojuego()));
    }

    // Buscar por videojuego con excepción si lista vacía
    public Page<InventarioItemResponse> buscarPorVideojuego(int videojuegoId, Pageable paginacion) {
        return inventarioItemRepository.findByVideojuegoVideojuegoId(videojuegoId, paginacion)
                .map(inventario -> {

                    VideojuegoResponse videojuegoResponse =
                            videojuegoMapper.toResponse(inventario.getVideojuego());

                    return inventarioItemMapper.toResponse(inventario, videojuegoResponse);
                });
    }

    // Buscar por plataforma con excepción si lista vacía
    public Page<InventarioItemResponse> buscarPorPlataforma(String plataformaString, Pageable paginacion) throws IllegalArgumentException {
        if (!esPlataformaValida(plataformaString)) {
            throw new IllegalArgumentException("La plataforma ingresada no es válida: " + plataformaString);
        }

        Plataformas plataforma = Plataformas.valueOf(plataformaString);

        return inventarioItemRepository.findByPlataforma(plataforma, paginacion)
                .map(inventario -> {

                    VideojuegoResponse videojuegoResponse =
                            videojuegoMapper.toResponse(inventario.getVideojuego());

                    return inventarioItemMapper.toResponse(inventario, videojuegoResponse);
                });
    }

    // Buscar por precio menor que valor, validando lista vacía
    public Page<InventarioItemResponse> buscarMasBaratosQue(double valor, Pageable paginacion) {
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        return inventarioItemRepository.findByPrecioDiarioLessThan(valor, paginacion)
                .map(inventario -> {

                    VideojuegoResponse videojuegoResponse =
                            videojuegoMapper.toResponse(inventario.getVideojuego());

                    return inventarioItemMapper.toResponse(inventario, videojuegoResponse);
                });
    }

    // Buscar por plataforma y precio menor que valor, validando lista vacía
    public Page<InventarioItemResponse> buscarPorPlataformaMasBaratosQue(String plataforma, double valor, Pageable paginacion) {
        if (valor < 0) {
            throw new IllegalArgumentException("El valor debe ser mayor a 0.");
        }
        if (!esPlataformaValida(plataforma)) {
            throw new IllegalArgumentException("La plataforma ingresada no es válida: " + plataforma);
        }
        return inventarioItemRepository.findByPlataformaAndPrecioDiarioLessThan(plataforma.trim().toUpperCase(), valor, paginacion)
                .map(inventario -> {

                    VideojuegoResponse videojuegoResponse =
                            videojuegoMapper.toResponse(inventario.getVideojuego());

                    return inventarioItemMapper.toResponse(inventario, videojuegoResponse);
                });
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


    public boolean validarDatosDeInventario(InventarioItemCreateOrReplaceRequest request) throws IllegalArgumentException {

        if (request.getStockDisponible() > request.getStockTotal()) {
            throw new IllegalArgumentException("El stock disponible no puede ser mayor que el stock total.");
        }

        List<InventarioItemEntity> inventarios = inventarioItemRepository.findByVideojuegoVideojuegoId(request.getVideojuegoId());

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
        return false;
    }

    public InventarioItemResponse agregarStock(int id, int cantidad) {
        InventarioItemEntity existente = obtenerInventarioPorId(id);

        existente.setStockDisponible(existente.getStockDisponible() + cantidad);
        existente.setStockTotal(existente.getStockTotal() + cantidad);

        validarStock(existente);

        return inventarioItemMapper.toResponse(
                inventarioItemRepository.save(existente),
                videojuegoMapper.toResponse(existente.getVideojuego())
        );
    }

    public InventarioItemResponse darDeBaja(int id) throws StockNoValidoException {
        InventarioItemEntity existente = obtenerInventarioPorId(id);

        if (existente.getStockTotal() != existente.getStockDisponible()) {
            throw new StockNoValidoException("Hay copias alquiladas, por favor complete " +
                    "los alquileres activos antes de dar de baja el stock");
        }

        existente.setStockTotal(0);
        existente.setStockDisponible(0);

        return inventarioItemMapper.toResponse(
                inventarioItemRepository.save(existente),
                videojuegoMapper.toResponse(existente.getVideojuego()));
    }

    private InventarioItemEntity obtenerInventarioPorId(int id) {
        return inventarioItemRepository.findById(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + id + " no encontrado."
                ));
    }
}


