package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioItemNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.factories.NotificadorFactory;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
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

    @Transactional
    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onStockDisponible(StockDisponibleEvent event){

        System.out.println("Evento recibido!");

        InventarioItemEntity item = inventarioItemRepository.findById(event.getInventarioItemId())
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + event.getInventarioItemId() + " no encontrado."
                ));

        System.out.println(item.getListaDeEspera().size());

        boolean procesado;

        do {
            procesado = procesarListaDeEspera(item);
        } while (procesado && item.getStockDisponible() > 0);

    }

    @Transactional
    public boolean procesarListaDeEspera(InventarioItemEntity item){

        //busca la primera reserva pendiente
        Optional<ReservaEntity> siguiente = item.obtenerSiguienteReservaPendiente();

        System.out.println(
                "Reserva encontrada: " + siguiente.isPresent()
        );

        if (siguiente.isEmpty()) return false;

        ReservaEntity r = siguiente.get();

        //notifica según los medios disponibles
        Notificador notificador = NotificadorFactory.obtenerNotificadorPara(r.getPersona());
        notificador.notificar(
                r.getPersona(),
                item.getVideojuego().getTitulo() + "está disponible. Tienes 24 hs para retirarlo."
        );

        item.reservarCopiaPara(r);

        System.out.println(
                "Estado luego de notificar: "
                        + r.getEstadoReserva()
        );

        return true;

    }

    @Transactional
    //@Scheduled(fixedRate = 3600000) // Cada 1 HORA revisa si hay notificaciones expiradas.
    @Scheduled(fixedRate = 60000) // 1 minuto
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
