package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.NoSuchElementException;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private VideojuegoMapper videojuegoMapper;

    public VideojuegoResponse guardar(VideojuegoCreateOrReplaceRequest request) throws IllegalArgumentException, NoSuchElementException {
        if(!esGeneroValido(request.getGenero())){
            throw new NoSuchElementException("El genero ingresado no es valido: " + request.getGenero());
        }
        if (request.getLanzamiento() != null && request.getLanzamiento().isAfter(Year.now())) {
            throw new IllegalArgumentException("El año de lanzamiento no puede ser futuro: " + request.getLanzamiento());
        }

        // 1. Convertimos el request a entidad.
        VideojuegoEntity entity = videojuegoMapper.toEntityFromRequest(request);

        // 2. Guardamos la entidad y luego la convertimos en response para devolversela a controller.
        return videojuegoMapper.toResponse(videojuegoRepository.save(entity));

    }

    public void eliminar(int id){
        if (!videojuegoRepository.existsById(id)) {
            throw new VideojuegoNoEncontradoException("Videojuego con id: " + id + " no encontrado.");
        }
        videojuegoRepository.deleteById(id);
    }

    public Page<VideojuegoResponse> listarTodos(Pageable paginacion)
    {
        return videojuegoRepository.findAll(paginacion)
                .map(videojuegoMapper::toResponse);
    }

    public VideojuegoResponse buscarPorId(int id){
        VideojuegoEntity entity = videojuegoRepository.findById(id).orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + id + " no encontrado."));
        return videojuegoMapper.toResponse(entity);
    }

    public Page<VideojuegoResponse> buscarPorTitulo(String titulo, Pageable paginacion){
        Page<VideojuegoEntity> videojuegos = videojuegoRepository.findByTituloContaining(titulo, paginacion);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego que contenga el título: " + titulo);
        }
        return videojuegos
                .map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarPorDesarrollador(String desarrollador, Pageable paginacion){
        Page<VideojuegoEntity> videojuegos = videojuegoRepository.findByDesarrolladorContaining(desarrollador, paginacion);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego que contenga el desarrollador: " + desarrollador);
        }
        return videojuegos
                .map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarPorGenero(String generoStr, Pageable paginacion) throws NoSuchElementException, VideojuegoNoEncontradoException {
        if (!esGeneroValido(generoStr)){
            throw new NoSuchElementException("No existe el genero: " + generoStr);
        }

        Generos genero = Generos.valueOf(generoStr.trim().toUpperCase());

        Page<VideojuegoEntity> juegos = videojuegoRepository.findByGenero(genero, paginacion);

        if (juegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontraron juegos del genero: " + generoStr);
        }

        return juegos
                .map(videojuegoMapper::toResponse);
    }

    public boolean esGeneroValido(String generoStr) {
        try {
            Generos.valueOf(generoStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Page<VideojuegoResponse> buscarMultijugadores(Pageable paginacion){
        Page<VideojuegoEntity> videojuegos = videojuegoRepository.findByMultijugadorTrue(paginacion);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego multijugador.");
        }
        return videojuegos
                .map(videojuegoMapper::toResponse);
    }

    public Page<VideojuegoResponse> buscarPorLanzamiento(Year lanzamiento, Pageable paginacion){
        Page<VideojuegoEntity> videojuegos = videojuegoRepository.findByLanzamiento(lanzamiento, paginacion);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego con año de lanzamiento: " + lanzamiento);
        }
        return videojuegos
                .map(videojuegoMapper::toResponse);
    }

    // Método para actualización completa (PUT)
    public VideojuegoResponse actualizarCompleto(int id, VideojuegoCreateOrReplaceRequest datosNuevos) {
        VideojuegoEntity videojuegoExistente = videojuegoRepository.findById(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id " + id + " no encontrado."));

        // Sobrescribímos todo, porque el DTO tiene todo obligatorio
        videojuegoExistente.setTitulo(datosNuevos.getTitulo());
        videojuegoExistente.setDesarrollador(datosNuevos.getDesarrollador());
        videojuegoExistente.setGenero(Generos.valueOf(datosNuevos.getGenero().toUpperCase().trim()));
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
        videojuegoMapper.actualizarEntity(videojuegoExistente, datosActualizados);
        return videojuegoMapper.toResponse(videojuegoRepository.save(videojuegoExistente));
    }

}
