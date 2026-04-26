package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(
        name = "alquiler"
)


public class AlquilerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "alquiler_id"
    )
    private int alquilerId;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    @NotNull(message = "Debe especificarse una persona para el alquiler")
    private PersonaEntity persona;

    @OneToMany(
            mappedBy = "alquiler",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<DetalleAlquilerEntity> items = new ArrayList<>();

    @Column(
            name = "fecha_inicio",
            nullable = false
    )
    @NotNull(message = "La fecha de retiro no puede ser nula")
    @PastOrPresent(message = "La fecha de retiro no puede estar en el futuro")
    private LocalDate fechaInicio;

    @Column(
            name = "fecha_fin",
            nullable = false
    )
    @NotNull(message = "La fecha limite no puede ser nula")
    @Future(message = "La fecha limite debe estar en el futuro")
    private LocalDate fechaFin; // fecha en la que DEBERIA devolver el juego

    @Column(
            name = "fecha_devolucion"
    )
    private LocalDate fechaDevolucion; // fecha en la que se devuelve el juego

    @Column(
            name = "estado_alquiler",
            nullable = false
    )
    @NotNull(message = "El estado del alquiler no puede ser nulo")
    @Enumerated(EnumType.STRING) // EN_CURSO, FINALIZADO, ATRASADO
    private EstadoAlquiler estadoAlquiler;

   /* @Column(
            name = "monto_diario_alquiler",
            nullable = false
    )
    @NotNull(message = "El monto diario del alquiler no puede ser nulo")
    @Positive(message = "El monto diario del alquiler debe ser mayor a cero")
    private BigDecimal montoDiarioAlquiler;*/

    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isFechaValida() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return fechaFin.isAfter(fechaInicio);
    }

    @Transient
    public boolean isAtrasado() {
        if (fechaFin == null) return false;
        return fechaDevolucion == null && LocalDate.now().isAfter(fechaFin);
    }


    public long calcularDiasAlquiler() {
       long dias = ChronoUnit.DAYS.between(this.getFechaInicio(), this.getFechaFin()) + 1; // +1 para incluir el día de inicio
        if (dias <= 0) {
            throw new IllegalArgumentException("La fecha de entrega debe ser posterior a la de retiro");
        }
        return dias;
    }

    public void agregarDetalle(DetalleAlquilerEntity detalle) {
        items.add(detalle);
        detalle.setAlquiler(this);

    }



}
