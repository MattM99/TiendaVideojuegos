package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.interfaces.Notificador;

public class NotificadorSMS implements Notificador {

    @Override
    public boolean aplica(PersonaEntity persona) {
        return persona.getTelefono() != null && !persona.getTelefono().isEmpty();
    }

    @Override
    public void notificar(PersonaEntity persona, String mensaje) {
        // Aquí iría la lógica para enviar un SMS, pero por ahora solo imprimimos el mensaje
        System.out.println("Enviando SMS a " + persona.getNombre() + ": " + mensaje);
    }

}
