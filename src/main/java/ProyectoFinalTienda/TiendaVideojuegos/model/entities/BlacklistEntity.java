package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "blacklist"
)

public class BlacklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "blacklist_id"
    )
    private int blacklist_id;

    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    private PersonaEntity persona;

    @Column(
            name = "fecha_inicio",
            nullable = false
    )
    private LocalDate fecha_inicio;

    @Column(
            name = "fecha_fin"
    )
    private LocalDate fecha_fin;

    @Column(
            name = "motivo",
            nullable = false
    )
    private String motivo;
}
