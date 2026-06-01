package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.factories.NotificadorFactory;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.interfaces.Notificador;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReservaService {

    public void procesarListaDeEspera(InventarioItemEntity inventarioItemEntity){

        //busca la primera reserva pendiente
        Optional<ReservaEntity> siguiente = inventarioItemEntity.getListaDeEspera().stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.PENDIENTE)
                .findFirst();

        if(siguiente.isPresent()){

            ReservaEntity r = siguiente.get();

            r.setEstadoReserva(EstadoReserva.NOTIFICADA);
            r.setFechaNotificacion(LocalDateTime.now());

            //notifica según los medios disponibles
            Notificador notificador = NotificadorFactory.obtenerNotificadorPara(r.getPersona());
            notificador.notificar(
                    r.getPersona(),
                    inventarioItemEntity.getVideojuego().getTitulo() + "está disponible. Tienes 24 hs para retirarlo."
            );
        }

    }

}
