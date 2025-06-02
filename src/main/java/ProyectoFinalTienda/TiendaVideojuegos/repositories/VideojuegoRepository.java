package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Year;
import java.util.List;

public interface VideojuegoRepository extends JpaRepository<VideojuegoEntity, Integer> {

    List<VideojuegoEntity> findByTituloContaining(String titulo);

    List<VideojuegoEntity> findByDesarrolladorContaining(String desarrollador);

    List<VideojuegoEntity> findByGenero(Generos genero);

    List<VideojuegoEntity> findByMultijugadorTrue();

    List<VideojuegoEntity> findByLanzamiento(Year lanzamiento);

}
