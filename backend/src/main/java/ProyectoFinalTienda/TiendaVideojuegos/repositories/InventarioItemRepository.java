package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItemEntity, Integer> {

    Page<InventarioItemEntity> findByActivoTrue(Pageable paginacion);

    Page<InventarioItemEntity> findByVideojuego_VideojuegoIdAndActivoTrue(
            int videojuegoId,
            Pageable paginacion
    );

    boolean existsByVideojuego_VideojuegoIdAndActivoTrue(
            int videojuegoId
    );

    Page<InventarioItemEntity> findByPrecioDiarioLessThanAndActivoTrue(
            double valor,
            Pageable paginacion
    );

    Page<InventarioItemEntity> findByPlataformaAndActivoTrue(
            Plataformas plataforma,
            Pageable paginacion
    );

    Page<InventarioItemEntity> findByPlataformaAndPrecioDiarioLessThanAndActivoTrue(
            Plataformas plataforma,
            double valor,
            Pageable paginacion
    );

    @Query(
    """
    SELECT i
    FROM InventarioItemEntity i
    WHERE i.videojuego.videojuegoId = :videojuegoId
    """)
    Page<InventarioItemEntity> findByVideojuegoId(
            @Param("videojuegoId") int videojuegoId,
            Pageable pageable
    );

    @Query(
    """
    SELECT i
    FROM InventarioItemEntity i
    WHERE i.videojuego.videojuegoId = :videojuegoId
    """)
    List<InventarioItemEntity> findByVideojuegoId(
            @Param("videojuegoId") int videojuegoId
    );

    Page<InventarioItemEntity> findByPlataforma(Plataformas plataforma, Pageable paginacion);

    Page<InventarioItemEntity> findByPrecioDiarioLessThan(double valor, Pageable paginacion);

    Page<InventarioItemEntity> findByPlataformaAndPrecioDiarioLessThan(Plataformas plataforma, double precioDiario, Pageable paginacion);

    boolean existsByVideojuego_VideojuegoIdAndPlataforma(Integer videojuegoId, Plataformas plataforma);

    boolean existsByVideojuego_VideojuegoIdAndPlataformaAndInventarioItemIdNot(
            Integer videojuegoId,
            Plataformas plataforma,
            Integer inventarioItemId
    );

}
