package ProyectoFinalTienda.TiendaVideojuegos.factories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.NotificadorEmail;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.NotificadorSMS;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.interfaces.Notificador;
import java.util.List;

public class NotificadorFactory {

    private static final List<Notificador> notificadores = List.of(
            new NotificadorSMS(),
            new NotificadorEmail()
            // Podemos agregar más notificadores acá sin modificar la lógica.
    );

    public static Notificador obtenerNotificadorPara(PersonaEntity persona) {
        return notificadores.stream()
                .filter(n -> n.aplica(persona))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La persona no tiene medios de contacto válidos."));
    }

}
