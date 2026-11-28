package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.events.StockDisponibleEvent;
import ProyectoFinalTienda.TiendaVideojuegos.exception.InventarioItemNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.factories.NotificadorFactory;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.InventarioItemEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.ReservaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.interfaces.Notificador;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.InventarioItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ListaDeEsperaWorkflowService {

    @Autowired
    InventarioItemRepository inventarioItemRepository;

    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onStockDisponible(StockDisponibleEvent event){

        System.out.println("Evento recibido!");

        // La transacción que publicó el evento ya bloqueó este inventario.
        InventarioItemEntity item = bloquearInventario(event.getInventarioItemId());

        if (!item.isActivo()) {
            return;
        }

        System.out.println(item.getListaDeEspera().size());

        boolean procesado;
        do {
            procesado = procesarListaDeEspera(item);
        } while (procesado && item.getStockDisponible() > 0);

    }

    private boolean procesarListaDeEspera(InventarioItemEntity item) {
        if (item.getStockDisponible() <= 0) {
            return false;
        }

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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    //@Scheduled(fixedRate = 3600000) // Cada 1 HORA revisa si hay notificaciones expiradas.
    @Scheduled(fixedRate = 60000) // 1 minuto
    public void revisarReservasExpiradas() {
        for (int inventarioId : inventarioItemRepository.buscarIdsConReservasEnEstado(EstadoReserva.NOTIFICADA)) {
            InventarioItemEntity inventario = bloquearInventario(inventarioId);

            if (!inventario.expirarReservasVencidas(LocalDateTime.now()).isEmpty()) {
                procesarListaDeEspera(inventario);
            }
        }
    }

    private InventarioItemEntity bloquearInventario(int id) {
        return inventarioItemRepository.buscarPorIdParaActualizar(id)
                .orElseThrow(() -> new InventarioItemNoEncontradoException(
                        "Inventario con id: " + id + " no encontrado."
                ));
    }

}
