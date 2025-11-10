package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "alquiler"
)


public class AlquilerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "alquiler_id"
    )
    private int alquiler_id;
    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    @NotNull(message = "Debe especificarse una persona para el alquiler")
    private PersonaEntity persona;

    @OneToMany(
            mappedBy = "alquiler",
            cascade = CascadeType.ALL
    )
    private List<FacturaEntity> facturas = new ArrayList<>();

    @OneToMany(
            mappedBy = "alquiler",
            cascade = CascadeType.ALL
    )
    private List<DetalleAlquilerEntity> detalles = new ArrayList<>();

    @Column(
            name = "fecha_retiro",
            nullable = false
    )
    @NotNull(message = "La fecha de retiro no puede ser nula")
    @PastOrPresent(message = "La fecha de retiro no puede estar en el futuro")
    private LocalDate fecha_retiro;

    @Column(
            name = "fecha_devolucion",
            nullable = false
    )
    @NotNull(message = "La fecha de devolución no puede ser nula")
    @Future(message = "La fecha de devolución debe estar en el futuro")
    private LocalDate fecha_devolucion; // fecha en la que DEBERIA devolver el juego

    public long calcularDiasAlquiler() {
       long dias = ChronoUnit.DAYS.between(this.getFecha_retiro(), this.getFecha_devolucion());
        if (dias <= 0) {
            throw new IllegalArgumentException("La fecha de entrega debe ser posterior a la de retiro");
        }
        return dias;
    }

}
