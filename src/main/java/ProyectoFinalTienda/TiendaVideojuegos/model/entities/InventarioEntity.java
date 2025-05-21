package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
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
        name = "inventario"
)

public class InventarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "inventario_id",
            nullable = false
    )
    private int inventario_id;

    @ManyToOne
    @JoinColumn(name = "videojuego_id", nullable = false, unique = true)
    private VideojuegoEntity videojuego;

    @OneToMany(
            mappedBy = "inventario",
            cascade = CascadeType.ALL
    )
    private List<DetalleAlquilerEntity> detalle_alquileres;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "plataforma",
            nullable = false
    )
    private Plataformas plataforma;

    @Column(
            name = "precio_unitario_diario",
            nullable = false
    )
    private double precio_unitario_diario;

    @Column(
            name = "stock_total",
            nullable = false
    )
    private int stock_total;

    @Column(
            name = "stock_disponible",
            nullable = false
    )
    private int stock_disponible;

    @Column(
            name = "stock_alquilado",
            nullable = false
    )
    private int stock_alquilado;

    @Column(
            name = "stock_descartado",
            nullable = false
    )
    private int stock_descartado;

}
