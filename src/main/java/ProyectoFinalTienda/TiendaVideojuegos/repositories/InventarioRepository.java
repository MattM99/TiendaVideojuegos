package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventarioRepository extends JpaRepository<InventarioEntity, Integer> {

    List<InventarioEntity> findByPlatform(Plataformas plataforma);

    List<InventarioEntity> findByPrecioUnitarioDiarioLessThan(double valor);

    List<InventarioEntity> findByPlataformaAndPrecioUnitarioDiarioLessThan(Plataformas plataforma, double precio);

    @Query("SELECT i.stockTotal FROM InventarioEntity i WHERE i.inventario_id = :inventarioId")
    Integer findStockTotalByInventarioId(@Param("inventarioId") int inventarioId);

    @Query("SELECT i.stockDisponible FROM InventarioEntity i WHERE i.inventario_id = :inventarioId")
    Integer findStockDisponibleByInventarioId(@Param("inventarioId") int inventarioId);

    @Query("SELECT i.stockAlquilado FROM InventarioEntity i WHERE i.inventario_id = :inventarioId")
    Integer findStockAlquiladoByInventarioId(@Param("inventarioId") int inventarioId);

    @Query("SELECT i.stockDescartado FROM InventarioEntity i WHERE i.inventario_id = :inventarioId")
    Integer findStockDescartadoByInventarioId(@Param("inventarioId") int inventarioId);



}
