package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioUpdateRequest {

    private Double precioUnitarioDiario;
    private Integer stockTotal;
    private Integer stockDisponible;
    private Integer stockAlquilado;
    private Integer stockDescartado;

    public void actualizarInventario(InventarioEntity entity) {
        if (precioUnitarioDiario != null && precioUnitarioDiario > 0) {
            entity.setPrecioUnitarioDiario(precioUnitarioDiario);
        }
        if (stockTotal != null && stockTotal >= 0) {
            entity.setStockTotal(stockTotal);
        }
        if (stockDisponible != null && stockDisponible >= 0) {
            entity.setStockDisponible(stockDisponible);
        }
        if (stockAlquilado != null && stockAlquilado >= 0) {
            entity.setStockAlquilado(stockAlquilado);
        }
        if (stockDescartado != null && stockDescartado >= 0) {
            entity.setStockDescartado(stockDescartado);
        }
    }

}
