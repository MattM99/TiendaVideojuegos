package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonIgnoreProperties({"videojuegoId", "plataforma", "stockTotal", "stockDisponible"})
public record InventarioItemEdicionRequest(
        @NotNull(message = "El precio unitario diario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
        BigDecimal precioDiario
) {}
