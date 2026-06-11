package ProyectoFinalTienda.TiendaVideojuegos.model.interfaces;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;

public interface Notificador {
    boolean aplica(PersonaEntity persona);
    void notificar(PersonaEntity persona, String mensaje);
}
