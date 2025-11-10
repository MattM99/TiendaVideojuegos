package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    @JoinColumn(name = "videojuego_id", nullable = false)
    @NotNull(message = "El videojuego no puede ser nulo")
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
    @NotNull(message = "La plataforma no puede ser nula")
    private Plataformas plataforma;

    @Column(
            name = "precio_unitario_diario",
            nullable = false
    )
    @PositiveOrZero(message = "El precio unitario diario no puede ser negativo")
    private double precioUnitarioDiario;

    @Column(
            name = "stock_total",
            nullable = false
    )
    @Min(value = 0, message = "El stock total no puede ser negativo")
    private int stockTotal;

    @Column(
            name = "stock_disponible",
            nullable = false
    )
    @Min(value = 0, message = "El stock disponible no puede ser negativo")
    private int stockDisponible;

    @Column(
            name = "stock_alquilado",
            nullable = false
    )
    @Min(value = 0, message = "El stock alquilado no puede ser negativo")
    private int stockAlquilado;

    @Column(
            name = "stock_descartado",
            nullable = false
    )
    @Min(value = 0, message = "El stock descartado no puede ser negativo")
    private int stockDescartado;



}
