package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Integer> {

    @Query("SELECT i FROM InventarioEntity i WHERE i.videojuego.videojuegoID = :id")
    List<InventarioEntity> findByVideojuegoId(@Param("id") int id);

    List<InventarioEntity> findByPlataforma(Plataformas plataforma);

    List<InventarioEntity> findByPrecioUnitarioDiarioLessThan(double valor);

    List<InventarioEntity> findByPlataformaAndPrecioUnitarioDiarioLessThan(Plataformas plataforma, double precio);

    @Query("SELECT i.stockTotal FROM InventarioEntity i WHERE i.inventario_id = ?1")
    Integer findStockTotalByInventarioId(int inventarioId);

    @Query("SELECT i.stockDisponible FROM InventarioEntity i WHERE i.inventario_id = ?1")
    Integer findStockDisponibleByInventarioId(int inventarioId);

    @Query("SELECT i.stockAlquilado FROM InventarioEntity i WHERE i.inventario_id = ?1")
    Integer findStockAlquiladoByInventarioId(int inventarioId);

    @Query("SELECT i.stockDescartado FROM InventarioEntity i WHERE i.inventario_id = ?1")
    Integer findStockDescartadoByInventarioId(int inventarioId);

}
