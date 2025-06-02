package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Year;
import java.util.List;

public interface VideojuegoRepository extends JpaRepository<VideojuegoEntity, Integer> {

    public List<VideojuegoEntity> findByTituloContaining(String titulo);

    public List<VideojuegoEntity> findByDesarrolladorContaining(String desarrollador);

    public List<VideojuegoEntity> findByGenero(Generos genero);

    public List<VideojuegoEntity> findByMultijugadorTrue();

    public List<VideojuegoEntity> findByLanzamiento(Year lanzamiento);

}
