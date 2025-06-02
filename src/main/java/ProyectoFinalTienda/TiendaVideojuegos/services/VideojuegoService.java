package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    public List<VideojuegoEntity> obtenerTodos(){
        return videojuegoRepository.findAll();
    }

    public VideojuegoEntity guardar(VideojuegoEntity videojuego){
        return videojuegoRepository.save(videojuego);
    }

    public void eliminar(int id){
        videojuegoRepository.deleteById(id);
    }

    public Optional<VideojuegoEntity> buscarPorId(int id){
        return videojuegoRepository.findById(id);
    }

    public List<VideojuegoEntity> buscarPorTitulo(String titulo){
        return videojuegoRepository.findByTituloContaining(titulo);
    }

    public List<VideojuegoEntity> buscarPorDesarrollador(String desarrollador){
        return videojuegoRepository.findByDesarrolladorContaining(desarrollador);
    }

    public List<VideojuegoEntity> buscarPorGenero(Generos genero){
        return videojuegoRepository.findByGenero(genero);
    }

    public List<VideojuegoEntity> buscarMultijugadores(){
        return videojuegoRepository.findByMultijugadorTrue();
    }

    public List<VideojuegoEntity> buscarPorLanzamiento(Year lanzamiento){
        return videojuegoRepository.findByLanzamiento(lanzamiento);
    }

}
