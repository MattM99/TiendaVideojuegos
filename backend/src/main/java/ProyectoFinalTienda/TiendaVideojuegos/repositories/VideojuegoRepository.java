package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.Optional;

@Repository
public interface VideojuegoRepository extends JpaRepository<VideojuegoEntity, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VideojuegoEntity v WHERE v.videojuegoId = :id")
    Optional<VideojuegoEntity> buscarPorIdParaActualizar(@Param("id") int id);

    Page<VideojuegoEntity> findByTituloContaining(String titulo, Pageable paginacion);

    Page<VideojuegoEntity> findByDesarrolladorContaining(String desarrollador, Pageable paginacion);

    Page<VideojuegoEntity> findByGenero(Generos genero, Pageable paginacion);

    Page<VideojuegoEntity> findByMultijugadorTrue(Pageable paginacion);

    Page<VideojuegoEntity> findByLanzamiento(Year lanzamiento, Pageable paginacion);

    Page<VideojuegoEntity> findByActivoTrue(Pageable paginacion);

    Page<VideojuegoEntity> findByTituloContainingAndActivoTrue(
            String titulo,
            Pageable paginacion
    );

    Page<VideojuegoEntity> findByDesarrolladorContainingAndActivoTrue(
            String desarrollador,
            Pageable paginacion
    );

    Page<VideojuegoEntity> findByGeneroAndActivoTrue(
            Generos genero,
            Pageable paginacion
    );

    Page<VideojuegoEntity> findByMultijugadorTrueAndActivoTrue(
            Pageable paginacion
    );

    Page<VideojuegoEntity> findByLanzamientoAndActivoTrue(
            Year lanzamiento,
            Pageable paginacion
    );

    boolean existsByTitulo(String titulo);

    boolean existsByTituloAndVideojuegoIdNot(String titulo, int videojuegoId);

}

