package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuenta")
@RequiredArgsConstructor
public class CuentaController {
    @PostMapping(value = "demo")
    public String demo() {
        return "Demo endpoint for CuentaController";
    }
}
