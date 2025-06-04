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
    @JoinColumn(name = "videojuego_id", nullable = false)
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
    private double precioUnitarioDiario;

    @Column(
            name = "stock_total",
            nullable = false
    )
    private int stockTotal;

    @Column(
            name = "stock_disponible",
            nullable = false
    )
    private int stockDisponible;

    @Column(
            name = "stock_alquilado",
            nullable = false
    )
    private int stockAlquilado;

    @Column(
            name = "stock_descartado",
            nullable = false
    )
    private int stockDescartado;

    /// ---- Validaciones de stock ----- ///

    public boolean esStockValido() {
        return (stockDisponible + stockAlquilado + stockDescartado) <= stockTotal;
    }

    public void validarStock() {
        if (!esStockValido()) {
            throw new IllegalStateException("La suma de los stocks excede el stock total.");
        }
    }

}
