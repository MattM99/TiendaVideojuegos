package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Long totalAlquileres;
    private Long alquileresActivos;
    private Long alquileresFinalizanManiana;
    private Long alquileresMesActual;

    private BigDecimal ingresosMes;
    private BigDecimal ingresosPenalizacionesMes;
    private BigDecimal promedioPorAlquiler;

    private Long videojuegosAlquilados;
    private List<TopJuegosResponse> juegosMasAlquilados;
    private List<TopGenerosResponse> generosMasAlquilados;
    private List<TopPlataformasResponse> plataformasMasAlquiladas;

    private List<TopClientesResponse> clientesFrecuentes;
}