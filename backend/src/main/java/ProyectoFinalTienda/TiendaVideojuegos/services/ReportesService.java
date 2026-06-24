package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.*;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.ReportesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportesService {

    private final ReportesRepository reportesRepository;

    public DashboardResponse obtenerDashboard() {
        LocalDate hoy  = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        LocalDateTime inicioMesDateTime = inicioMes.atStartOfDay();
        LocalDateTime finMesDateTime = finMes.atTime(23, 59, 59);

        return DashboardResponse.builder()
                .totalAlquileres(reportesRepository.contarTotalAlquileres())
                .alquileresActivos(reportesRepository.contarActivos())
                .alquileresFinalizanManiana(reportesRepository.contarFinalizanPronto(hoy.plusDays(1)))
                .alquileresMesActual(reportesRepository.contarAlquileresMes(inicioMes, finMes))
                .ingresosMes(reportesRepository.ingresosMes(inicioMes, finMes))
                .ingresosPenalizacionesMes(reportesRepository.totalPenalizaciones(inicioMesDateTime, finMesDateTime))
                .promedioPorAlquiler(reportesRepository.promedioIngresoPorAlquiler(inicioMesDateTime, finMesDateTime))
                .videojuegosAlquilados(reportesRepository.contarVideojuegosAlquilados())
                .juegosMasAlquilados(obtenerTopJuegos())
                .generosMasAlquilados(obtenerTopGeneros())
                .plataformasMasAlquiladas(obtenerTopPlataformas())
                .clientesFrecuentes(obtenerTopClientes())
                .build();
    }
    public List<TopJuegosResponse> obtenerTopJuegos() {
        return reportesRepository.obtenerTopJuegos(
                PageRequest.of(0, 3)
        );
    }

    public List<TopGenerosResponse> obtenerTopGeneros() {
        return reportesRepository.obtenerTopGeneros(
                 PageRequest.of(0, 3)
        );
    }

    public List<TopPlataformasResponse> obtenerTopPlataformas() {
        return reportesRepository.obtenerTopPlataformas(
                 PageRequest.of(0, 3)
        );
    }

    public List<TopClientesResponse> obtenerTopClientes() {
        return reportesRepository.obtenerTopClientes(
                 PageRequest.of(0, 3)
        );
    }
}
