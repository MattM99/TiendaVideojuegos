package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.MetodoPago;

import java.math.BigDecimal;

public class PagoResponse {

    private MetodoPago metodoPago;

    private BigDecimal descuento;

    private BigDecimal penalizacionTotal;

    private BigDecimal montoFinal;

}
