package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity, Integer> {

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

    @Modifying
    @Transactional
    @Query("UPDATE InventarioEntity i SET i.precioUnitarioDiario = ?2 WHERE i.inventario_id = ?1")
    void updatePrecioUnitario(int inventarioId, double precio);

    @Modifying
    @Transactional
    @Query("UPDATE InventarioEntity i SET i.stockTotal = ?2 WHERE i.inventario_id = ?1")
    void updateStockTotal(int inventarioId, int stockTotal);

    @Modifying
    @Transactional
    @Query("UPDATE InventarioEntity i SET i.stockDisponible = ?2 WHERE i.inventario_id = ?1")
    void updateStockDisponible(int inventarioId, int stockDisponible);

    @Modifying
    @Transactional
    @Query("UPDATE InventarioEntity i SET i.stockAlquilado = ?2 WHERE i.inventario_id = ?1")
    void updateStockAlquilado(int inventarioId, int stockAlquilado);

    @Modifying
    @Transactional
    @Query("UPDATE InventarioEntity i SET i.stockDescartado = ?2 WHERE i.inventario_id = ?1")
    void updateStockDescartado(int inventarioId, int stockDescartado);

}
