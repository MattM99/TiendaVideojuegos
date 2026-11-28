package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private VideojuegoMapper videojuegoMapper;
    @Autowired
    private InventarioItemRepository inventarioItemRepository;

    /// Create

    public VideojuegoResponse guardar(VideojuegoCreateOrReplaceRequest request) {

        validarLanzamiento(request.getLanzamiento());
        validarTituloDuplicado(request.getTitulo(), null);

        VideojuegoEntity entity =
                videojuegoMapper.toEntityFromRequest(request);

        return videojuegoMapper.toResponse(
                videojuegoRepository.save(entity)
        );
    }

    /// Read

    public Page<VideojuegoResponse> listarActivos(Pageable paginacion)
    {
        return videojuegoRepository.findByActivoTrue(paginacion)
                .map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> obtenerTodos(Pageable paginacion) {
        return videojuegoRepository.findAll(paginacion)
                .map(videojuegoMapper::toResponse);
    }

    public VideojuegoResponse buscarPorId(int id){
        VideojuegoEntity entity = videojuegoRepository.findById(id).orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + id + " no encontrado."));
        return videojuegoMapper.toResponse(entity);
    }

    public Page<VideojuegoResponse> buscarPorTitulo(
            String titulo,
            Pageable paginacion) {

        Page<VideojuegoEntity> videojuegos =
                videojuegoRepository.findByTituloContainingAndActivoTrue(
                        titulo,
                        paginacion
                );

        return videojuegos.map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarPorDesarrollador(
            String desarrollador,
            Pageable paginacion) {

        Page<VideojuegoEntity> videojuegos =
                videojuegoRepository.findByDesarrolladorContainingAndActivoTrue(
                        desarrollador,
                        paginacion
                );

        return videojuegos.map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarPorGenero(
            Generos genero,
            Pageable paginacion) {

        Page<VideojuegoEntity> juegos =
                videojuegoRepository.findByGeneroAndActivoTrue(
                        genero,
                        paginacion
                );

        return juegos.map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarMultijugadores(Pageable paginacion) {

        Page<VideojuegoEntity> videojuegos =
                videojuegoRepository.findByMultijugadorTrueAndActivoTrue(
                        paginacion
                );

        return videojuegos.map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarPorLanzamiento(
            Year lanzamiento,
            Pageable paginacion) {

        Page<VideojuegoEntity> videojuegos =
                videojuegoRepository.findByLanzamientoAndActivoTrue(
                        lanzamiento,
                        paginacion
                );

        return videojuegos.map(videojuegoMapper::toResponse);
    }

    /// Update

    // Método para actualización completa (PUT)
    public VideojuegoResponse actualizarCompleto(int id, VideojuegoCreateOrReplaceRequest datosNuevos) {
        VideojuegoEntity videojuegoExistente = videojuegoRepository.findById(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id " + id + " no encontrado."));

        if (!videojuegoExistente.isActivo()) {
            throw new BusinessException(
                    "El videojuego está dado de baja y no se puede modificar."
            );
        }

        validarLanzamiento(datosNuevos.getLanzamiento());
        validarTituloDuplicado(datosNuevos.getTitulo(), id);

        // Sobrescribímos todo, porque el DTO tiene todo obligatorio
        videojuegoExistente.setTitulo(datosNuevos.getTitulo());
        videojuegoExistente.setDesarrollador(datosNuevos.getDesarrollador());
        videojuegoExistente.setGenero(datosNuevos.getGenero());
        videojuegoExistente.setLanzamiento(datosNuevos.getLanzamiento());
        videojuegoExistente.setDescripcion(datosNuevos.getDescripcion());
        videojuegoExistente.setMultijugador(datosNuevos.isMultijugador());

        // 2. Guardamos la entidad y luego la convertimos en response para devolversela a controller.
        return videojuegoMapper.toResponse(videojuegoRepository.save(videojuegoExistente));

    }

    // Método para actualización parcial (PATCH)
    public VideojuegoResponse actualizarPorCampo(int id, VideojuegoUpdateRequest datosActualizados) {
        VideojuegoEntity videojuegoExistente = videojuegoRepository.findById(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id " + id + " no encontrado."));
        if (!videojuegoExistente.isActivo()) {
            throw new BusinessException(
                    "El videojuego está dado de baja y no se puede modificar."
            );
        }

        validarLanzamiento(datosActualizados.getLanzamiento());
        validarTituloDuplicado(datosActualizados.getTitulo(), id);
        videojuegoMapper.actualizarEntity(videojuegoExistente, datosActualizados);
        return videojuegoMapper.toResponse(videojuegoRepository.save(videojuegoExistente));
    }

    @Transactional
    public VideojuegoResponse darDeAlta(int id) {
        VideojuegoEntity videojuego = videojuegoRepository.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException(
                        "Videojuego con id: " + id + " no encontrado."
                ));

        if (videojuego.isActivo()) {
            throw new BusinessException("El videojuego ya se encuentra activo.");
        }

        videojuego.setActivo(true);
        return videojuegoMapper.toResponse(videojuegoRepository.save(videojuego));
    }

    /// Delete

    @Transactional
    public void eliminar(int id) {

        VideojuegoEntity videojuego = videojuegoRepository.findById(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException(
                        "Videojuego con id: " + id + " no encontrado."
                ));

        if (!videojuego.isActivo()) {
            throw new BusinessException(
                    "El videojuego ya se encuentra dado de baja."
            );
        }

        boolean tieneInventariosActivos =
                inventarioItemRepository.existsByVideojuego_VideojuegoIdAndActivoTrue(id);

        if (tieneInventariosActivos) {
            throw new BusinessException(
                    "No se puede dar de baja el videojuego porque tiene inventarios activos."
            );
        }

        videojuego.setActivo(false);

        videojuegoRepository.save(videojuego);
    }

    /// Helpers privados

    private void validarTituloDuplicado(String titulo, Integer idActual) {

        if (titulo == null) {
            return;
        }

        boolean existe = (idActual == null)
                ? videojuegoRepository.existsByTitulo(titulo)
                : videojuegoRepository.existsByTituloAndVideojuegoIdNot(titulo, idActual);

        if (existe) {
            throw new BusinessException(
                    "Ya existe un videojuego con ese título.");
        }
    }

    private void validarLanzamiento(Year lanzamiento) {
        if (lanzamiento != null && lanzamiento.isAfter(Year.now())) {
            throw new BusinessException(
                    "El año de lanzamiento no puede ser futuro: " + lanzamiento);
        }
    }

}
