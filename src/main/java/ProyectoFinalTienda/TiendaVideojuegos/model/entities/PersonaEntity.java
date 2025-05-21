package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "persona"
)

public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "persona_id"
    )
    private int personaId;

    @OneToOne(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private CuentaEntity cuenta;

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<BlacklistEntity> blacklist;

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<AlquilerEntity> alquiler;

    @Column(
            nullable = false
    )
    private String nombre;

    @Column(
            nullable = false
    )
    private String apellido;

    @Column(
            nullable = false,
            unique = true
    )
    private String dni;

    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            nullable = false
    )
    private String telefono;
}
