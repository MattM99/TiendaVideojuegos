package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Year;

@Repository
public interface VideojuegoRepository extends JpaRepository<VideojuegoEntity, Integer> {

    Page<VideojuegoEntity> findByTituloContaining(String titulo, Pageable paginacion);

    Page<VideojuegoEntity> findByDesarrolladorContaining(String desarrollador, Pageable paginacion);

    Page<VideojuegoEntity> findByGenero(Generos genero, Pageable paginacion);

    Page<VideojuegoEntity> findByMultijugadorTrue(Pageable paginacion);

    Page<VideojuegoEntity> findByLanzamiento(Year lanzamiento, Pageable paginacion);

}

