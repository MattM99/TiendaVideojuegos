package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItemEntity, Integer> {

    Page<InventarioItemEntity> findByVideojuegoVideojuegoId(
            int videojuegoId,
            Pageable pageable
    );

    List<InventarioItemEntity> findByVideojuegoVideojuegoId(
            int videojuegoId
    );

    Page<InventarioItemEntity> findByPlataforma(Plataformas plataforma, Pageable paginacion);

    Page<InventarioItemEntity> findByPrecioDiarioLessThan(double valor, Pageable paginacion);

    Page<InventarioItemEntity> findByPlataformaAndPrecioDiarioLessThan(String plataforma, double precioDiario, Pageable paginacion);

    @Query("SELECT i.stockTotal FROM InventarioItemEntity i WHERE i.inventarioItemId = ?1")
    Integer findStockTotalByInventarioId(int inventarioItemId);

    @Query("SELECT i.stockDisponible FROM InventarioItemEntity i WHERE i.inventarioItemId = ?1")
    Integer findStockDisponibleByInventarioId(int inventarioItemId);

}
