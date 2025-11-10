package ProyectoFinalTienda.TiendaVideojuegos.model.entities;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @Column(
            name = "desarrollador",
            nullable = false
    )
    @NotBlank(message = "El desarrollador no puede estar vacío")
    private String desarrollador;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "genero",
            nullable = false
    )
    @NotNull(message = "El género no puede ser nulo")
    private Generos genero;

    @Column(
            name = "lanzamiento",
            nullable = false
    )
    @NotNull(message = "El año de lanzamiento no puede ser nulo")
    private Year lanzamiento;

    @Column(
            name = "descripcion",
            nullable = false
    )
    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @Column(
            name = "multijugador",
            nullable = false
    )
    private boolean multijugador;


}
