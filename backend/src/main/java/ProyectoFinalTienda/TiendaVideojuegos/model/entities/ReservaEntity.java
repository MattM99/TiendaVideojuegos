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
            name = "fecha_notificacion",
            nullable = true
    )
    private LocalDateTime fechaNotificacion;

}
