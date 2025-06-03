package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.List;

@Repository
public interface VideojuegoRepository extends JpaRepository<VideojuegoEntity, Integer> {

    List<VideojuegoEntity> findByTituloContaining(String titulo);

    List<VideojuegoEntity> findByDesarrolladorContaining(String desarrollador);

    List<VideojuegoEntity> findByGenero(Generos genero);

    List<VideojuegoEntity> findByMultijugadorTrue();

    List<VideojuegoEntity> findByLanzamiento(Year lanzamiento);


    @Modifying
    @Transactional
    @Query("UPDATE VideojuegoEntity v SET v.titulo = ?2 WHERE v.videojuegoID = ?1")
    void updateTitulo(int videojuegoId, String titulo);

    @Modifying
    @Transactional
    @Query("UPDATE VideojuegoEntity v SET v.desarrollador = ?2 WHERE v.videojuegoID = ?1")
    void updateDesarrollador(int videojuegoId, String desarrollador);

    @Modifying
    @Transactional
    @Query("UPDATE VideojuegoEntity v SET v.genero = ?2 WHERE v.videojuegoID = ?1")
    void updateGenero(int videojuegoId, Generos genero);

    @Modifying
    @Transactional
    @Query("UPDATE VideojuegoEntity v SET v.lanzamiento = ?2 WHERE v.videojuegoID = ?1")
    void updateLanzamiento(int videojuegoId, Year lanzamiento);

    @Modifying
    @Transactional
    @Query("UPDATE VideojuegoEntity v SET v.descripcion = ?2 WHERE v.videojuegoID = ?1")
    void updateDescripcion(int videojuegoId, String descripcion);

    @Modifying
    @Transactional
    @Query("UPDATE VideojuegoEntity v SET v.multijugador = ?2 WHERE v.videojuegoID = ?1")
    void updateMultijugador(int videojuegoId, boolean multijugador);

}

