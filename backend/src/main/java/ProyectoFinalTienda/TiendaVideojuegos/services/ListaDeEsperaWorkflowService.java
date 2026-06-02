package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioItemNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.factories.NotificadorFactory;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.interfaces.Notificador;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ListaDeEsperaWorkflowService {

    @Autowired
    InventarioItemRepository inventarioItemRepository;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onStockDisponible(StockDisponibleEvent event){
        InventarioItemEntity inventarioItem = inventarioItemRepository.findById(event.getInventarioItemId())
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + event.getInventarioItemId() + " no encontrado."
                ));
        procesarListaDeEspera(inventarioItem);
    }

    public void procesarListaDeEspera(InventarioItemEntity inventarioItemEntity){

        //busca la primera reserva pendiente
        Optional<ReservaEntity> siguiente =
                inventarioItemEntity.obtenerSiguienteReservaPendiente();

        if(siguiente.isPresent()){

            ReservaEntity r = siguiente.get();

            //notifica según los medios disponibles
            Notificador notificador = NotificadorFactory.obtenerNotificadorPara(r.getPersona());
            notificador.notificar(
                    r.getPersona(),
                    inventarioItemEntity.getVideojuego().getTitulo() + "está disponible. Tienes 24 hs para retirarlo."
            );

            r.marcarComoNotificada();
        }

    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void revisarReservasExpiradas() {
        for (InventarioItemEntity inventarioItem : inventarioItemRepository.findAll()) {

            //buscar reservas que ya fueron notificadas pero expiraron
            List<ReservaEntity> expiradas = inventarioItem.expirarReservasVencidas(LocalDateTime.now());

            //si hay expiradas, notificar al siguiente pendiente
            if(!expiradas.isEmpty()){
                procesarListaDeEspera(inventarioItem);
            }

        }
    }

}
