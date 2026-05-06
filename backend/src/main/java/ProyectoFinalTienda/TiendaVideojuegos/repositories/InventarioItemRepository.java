package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItemEntity, Integer> {

    @Query("SELECT i FROM InventarioItemEntity i WHERE i.videojuego.videojuegoId = :id")
    List<InventarioItemEntity> findByVideojuegoId(@Param("id") int id);

    List<InventarioItemEntity> findByPlataforma(Plataformas plataforma);

    List<InventarioItemEntity> findByPrecioDiarioLessThan(double valor);

    List<InventarioItemEntity> findByPlataformaAndPrecioDiarioLessThan(Plataformas plataforma, double precioDiario);

    @Query("SELECT i.stockTotal FROM InventarioItemEntity i WHERE i.inventarioItemId = ?1")
    Integer findStockTotalByInventarioId(int inventarioItemId);

    @Query("SELECT i.stockDisponible FROM InventarioItemEntity i WHERE i.inventarioItemId = ?1")
    Integer findStockDisponibleByInventarioId(int inventarioItemId);

}
