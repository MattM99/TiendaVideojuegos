package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioCreateOrReplaceRequest {

    @NotNull(message = "El ID del videojuego es obligatorio")
    private Integer videojuegoId;

    @NotNull(message = "La plataforma es obligatoria")
    private Plataformas plataforma;

    @NotNull(message = "El precio unitario diario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precioUnitarioDiario;

    @NotNull(message = "El stock total es obligatorio")
    @Min(value = 0, message = "El stock total no puede ser negativo")
    private Integer stockTotal;

    @NotNull(message = "El stock disponible es obligatorio")
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private int stockDisponible;

    @NotNull(message = "El stock alquilado es obligatorio")
    @Min(value = 0, message = "El stock alquilado no puede ser negativo")
    private int stockAlquilado;

    @NotNull(message = "El stock descartado es obligatorio")
    @Min(value = 0, message = "El stock descartado no puede ser negativo")
    private int stockDescartado;

    public InventarioEntity toEntity(VideojuegoEntity videojuego) {
        return InventarioEntity.builder()
                .videojuego(videojuego)
                .plataforma(this.plataforma)
                .precioUnitarioDiario(this.precioUnitarioDiario)
                .stockTotal(this.stockTotal)
                .stockDisponible(this.stockDisponible)
                .stockAlquilado(this.stockAlquilado)
                .stockDescartado(this.stockDescartado)
                .build();
    }
}
