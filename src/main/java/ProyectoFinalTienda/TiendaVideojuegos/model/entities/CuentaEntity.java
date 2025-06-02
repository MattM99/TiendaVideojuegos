package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "cuenta"
)

public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "cuenta_id",
            nullable = false
    )
    private int cuenta_id;

    @OneToOne
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private PersonaEntity persona;

    @Column(
            name = "nickname",
            nullable = false,
            unique = true,
            length = 50
    )
    private String nickname;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "rol",
            nullable = false
    )
    private Roles rol;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false
    )
    private Estado estado;

    @Column(
            name = "alta",
            nullable = false
    )
    private boolean alta;

}
