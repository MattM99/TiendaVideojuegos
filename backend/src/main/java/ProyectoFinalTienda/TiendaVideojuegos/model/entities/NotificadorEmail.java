package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.interfaces.Notificador;

public class NotificadorEmail implements Notificador {

    @Override
    public boolean aplica(PersonaEntity persona) {
        return persona.getEmail() != null && !persona.getEmail().isEmpty();
    }

    @Override
    public void notificar(PersonaEntity persona, String mensaje) {
        // Aquí iría la lógica para enviar un email, pero por ahora solo imprimimos el mensaje
        System.out.println("Enviando email a " + persona.getNombre() + ": " + mensaje);
    }
}
