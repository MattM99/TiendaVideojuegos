package ProyectoFinalTienda.TiendaVideojuegos.model.entities;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "inventario")
@Table(
        name = "videojuego"
)

public class VideojuegoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "videojuego_id",
            nullable = false
    )
    private int videojuegoID;

    @OneToMany(
            mappedBy = "videojuego",
            cascade = CascadeType.ALL
    )
    private List<InventarioEntity> inventario = new ArrayList<>();

    @Column(
            nullable = false
    )
    private String titulo;

    @Column(
            name = "desarrollador",
            nullable = false
    )
    private String desarrollador;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "genero",
            nullable = false
    )
    private Generos genero;

    @Column(
            name = "lanzamiento",
            nullable = false
    )
    private Year lanzamiento;

    @Column(
            name = "descripcion",
            nullable = false
    )
    private String descripcion;

    @Column(
            name = "multijugador",
            nullable = false
    )
    private boolean multijugador;


}
