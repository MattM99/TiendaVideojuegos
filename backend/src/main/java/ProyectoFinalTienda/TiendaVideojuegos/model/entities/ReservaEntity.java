package ProyectoFinalTienda.TiendaVideojuegos.model.entities;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "reserva"
)
public class ReservaEntity {

    //private static final long TIEMPO_EXPIRACION_MINUTOS = 24 * 60;
    private static final long TIEMPO_EXPIRACION_MINUTOS = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "reserva_id",
            nullable = false
    )
    private int reservaId;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    @NotNull(message = "La persona a la que pertenece la reserva no puede ser nula")
    private PersonaEntity persona;

    @ManyToOne
    @JoinColumn(name = "inventario_item_id", nullable = false)
    @NotNull(message = "El item reservado no puede ser nulo")
    private InventarioItemEntity inventarioItem;

    @Column (
            name = "estado_reserva",
            nullable = false
    )
    @NotNull(message = "El estado de la reserva no puede ser nulo")
    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva;

    @Column (
            name = "fecha_reserva",
            nullable = false
    )
    @NotNull (message = "La fecha de reserva no puede ser nula")
    private LocalDateTime fechaReserva;

    @Column (
            name = "fecha_notificacion"
    )
    private LocalDateTime fechaNotificacion;

    public void marcarComoNotificada() {
        estadoReserva = EstadoReserva.NOTIFICADA;
        fechaNotificacion = LocalDateTime.now();
    }

    public void marcarComoConcluida() {
        estadoReserva = EstadoReserva.CONCLUIDA;
    }

    public void marcarComoVencida() {
        estadoReserva = EstadoReserva.EXPIRADA;
    }

    public boolean estaVencida(LocalDateTime fechaActual) {
        return estadoReserva == EstadoReserva.NOTIFICADA
                && fechaNotificacion.plusMinutes(TIEMPO_EXPIRACION_MINUTOS)
                .isBefore(fechaActual);
    }

    public void cancelarReserva() {
        if (getEstadoReserva() == EstadoReserva.PENDIENTE || getEstadoReserva() == EstadoReserva.NOTIFICADA) {
            setEstadoReserva(EstadoReserva.CANCELADA);
            // if estado es notificada, avisar a la persona.
        } else {
            throw new IllegalStateException("Solo se pueden cancelar reservas pendientes o notificadas.");
        }
    }

}
