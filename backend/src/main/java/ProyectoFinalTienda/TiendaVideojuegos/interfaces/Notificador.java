package ProyectoFinalTienda.TiendaVideojuegos.interfaces;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;

public interface Notificador {
    public boolean aplica(PersonaEntity persona);
    public void notificar(String mensaje, PersonaEntity persona);
}
