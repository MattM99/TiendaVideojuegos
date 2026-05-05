package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Setter;

import java.math.BigDecimal;

public class PagoResponse {

    private int pagoId;
    private AlquilerResponse alquilerResponse;
    private EstadoPago estadoPago;
    private BigDecimal descuento;
    private BigDecimal penalizacionTotal;
    private BigDecimal costoTotal;

}
