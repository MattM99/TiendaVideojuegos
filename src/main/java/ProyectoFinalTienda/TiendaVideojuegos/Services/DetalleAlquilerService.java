package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.DetalleAlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleAlquilerService {

    @Autowired
    private DetalleAlquilerRepository detalleAlquilerRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private VideojuegoRepository videojuegoRepository;



}
