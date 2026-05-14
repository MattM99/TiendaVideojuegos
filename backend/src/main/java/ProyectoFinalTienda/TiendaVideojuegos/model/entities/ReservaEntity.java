package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "reserva")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserva_id")
    private int reservaId;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    @NotNull(message = "La persona no puede ser nula")
    private PersonaEntity persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @NotNull(message = "El estado no puede ser nulo")
    private EstadoReserva estado;

    @Column(name = "fecha_reserva", nullable = false)
    @NotNull(message = "La fecha de reserva no puede ser nula")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

}

