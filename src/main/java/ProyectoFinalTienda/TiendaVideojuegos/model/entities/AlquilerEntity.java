package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private LocalDate fecha_retiro;

    @Column(
            name = "fecha_devolucion",
            nullable = false
    )
    private LocalDate fecha_devolucion; // fecha en la que DEBERIA devolver el juego

}
