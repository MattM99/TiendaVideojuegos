package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.DashboardResponse;
import ProyectoFinalTienda.TiendaVideojuegos.services.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reportes")
@RequiredArgsConstructor
public class ReportesController {
    private final ReportesService reportesService;

    @GetMapping("/dashboard")
    public DashboardResponse obtenerDashboard(){
        return reportesService.obtenerDashboard();
    }
}
