package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PagoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoPago;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;

}
