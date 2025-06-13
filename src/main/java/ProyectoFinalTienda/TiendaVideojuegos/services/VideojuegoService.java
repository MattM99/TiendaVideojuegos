package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.VideojuegoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.VideojuegoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
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

    public List<VideojuegoResponse> obtenerTodos(){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findAll();
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego en el sistema.");
        }
        return videojuegoMapper.toResponseList(videojuegos);
    }

    public VideojuegoResponse buscarPorId(int id){
        VideojuegoEntity entity = videojuegoRepository.findById(id).orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + id + " no encontrado."));
        return videojuegoMapper.toResponse(entity);
    }

    public List<VideojuegoResponse> buscarPorTitulo(String titulo){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByTituloContaining(titulo);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego que contenga el título: " + titulo);
        }
        return videojuegoMapper.toResponseList(videojuegos);
    }

    public List<VideojuegoResponse> buscarPorDesarrollador(String desarrollador){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByDesarrolladorContaining(desarrollador);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego que contenga el desarrollador: " + desarrollador);
        }
        return videojuegoMapper.toResponseList(videojuegos);
    }

    public List<VideojuegoResponse> buscarPorGenero(String generoStr) throws NoSuchElementException, VideojuegoNoEncontradoException {
        if (!esGeneroValido(generoStr)){
            throw new NoSuchElementException("No existe el genero: " + generoStr);
        }

        Generos genero = Generos.valueOf(generoStr.trim().toUpperCase());

        List<VideojuegoEntity> juegos = videojuegoRepository.findByGenero(genero);

        if (juegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontraron juegos del genero: " + generoStr);
        }

        return videojuegoMapper.toResponseList(juegos);
    }

    public boolean esGeneroValido(String generoStr) {
        try {
            Generos.valueOf(generoStr.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<VideojuegoResponse> buscarMultijugadores(){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByMultijugadorTrue();
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego multijugador.");
        }
        return videojuegoMapper.toResponseList(videojuegos);
    }

    public List<VideojuegoResponse> buscarPorLanzamiento(Year lanzamiento){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByLanzamiento(lanzamiento);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego con anio de lanzamiento: " + lanzamiento);
        }
        return videojuegoMapper.toResponseList(videojuegos);
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
