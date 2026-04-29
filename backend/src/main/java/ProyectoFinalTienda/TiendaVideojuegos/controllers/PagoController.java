package ProyectoFinalTienda.TiendaVideojuegos.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
@Validated
@Tag(name = "Pagos", description = "Operaciones relacionadas con la gestión de pagos")
public class PagoController {

}
