package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.VideojuegoUpdateRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.VideojuegoNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    public VideojuegoEntity guardar(VideojuegoEntity videojuego){
        return videojuegoRepository.save(videojuego);
    }

    public void eliminar(int id){
        if (!videojuegoRepository.existsById(id)) {
            throw new VideojuegoNoEncontradoException("Videojuego con id: " + id + " no encontrado.");
        }
        videojuegoRepository.deleteById(id);
    }

    public List<VideojuegoEntity> obtenerTodos(){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findAll();
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego en el sistema.");
        }
        return videojuegos;
    }

    public VideojuegoEntity buscarPorId(int id){
        return videojuegoRepository.findById(id).orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id: " + id + " no encontrado."));
    }

    public List<VideojuegoEntity> buscarPorTitulo(String titulo){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByTituloContaining(titulo);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego que contenga el título: " + titulo);
        }
        return videojuegos;
    }

    public List<VideojuegoEntity> buscarPorDesarrollador(String desarrollador){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByDesarrolladorContaining(desarrollador);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego que contenga el desarrollador: " + desarrollador);
        }
        return videojuegos;
    }

    public List<VideojuegoEntity> buscarPorGenero(Generos genero){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByGenero(genero);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego del género: " + genero);
        }
        return videojuegos;
    }

    public List<VideojuegoEntity> buscarMultijugadores(){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByMultijugadorTrue();
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego multijugador.");
        }
        return videojuegos;
    }

    public List<VideojuegoEntity> buscarPorLanzamiento(Year lanzamiento){
        List<VideojuegoEntity> videojuegos = videojuegoRepository.findByLanzamiento(lanzamiento);
        if (videojuegos.isEmpty()) {
            throw new VideojuegoNoEncontradoException("No se encontró ningún videojuego con anio de lanzamiento: " + lanzamiento);
        }
        return videojuegos;
    }

    // Método para actualización parcial (PATCH)
    public VideojuegoEntity actualizarPorCampo(int id, VideojuegoUpdateRequest datosActualizados) {
        VideojuegoEntity videojuegoExistente = videojuegoRepository.findById(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id " + id + " no encontrado."));
        datosActualizados.actualizarVideojuego(videojuegoExistente);
        return videojuegoRepository.save(videojuegoExistente);
    }

    // Método para actualización completa (PUT)
    public VideojuegoEntity actualizarCompleto(int id, VideojuegoCreateOrReplaceRequest datosNuevos) {
        VideojuegoEntity videojuegoExistente = videojuegoRepository.findById(id)
                .orElseThrow(() -> new VideojuegoNoEncontradoException("Videojuego con id " + id + " no encontrado."));
        // Sobrescribes todo, porque el DTO tiene todo obligatorio
        videojuegoExistente.setTitulo(datosNuevos.getTitulo());
        videojuegoExistente.setDesarrollador(datosNuevos.getDesarrollador());
        videojuegoExistente.setGenero(datosNuevos.getGenero());
        videojuegoExistente.setLanzamiento(datosNuevos.getLanzamiento());
        videojuegoExistente.setDescripcion(datosNuevos.getDescripcion());
        videojuegoExistente.setMultijugador(datosNuevos.isMultijugador());
        return videojuegoRepository.save(videojuegoExistente);
    }

}
