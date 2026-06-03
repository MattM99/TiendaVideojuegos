package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoReserva;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Plataformas;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "inventario_item"
)

public class InventarioItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "inventario_item_id",
            nullable = false
    )
    private int inventarioItemId;

    @ManyToOne
    @JoinColumn(name = "videojuego_id", nullable = false)
    @NotNull(message = "El videojuego no puede ser nulo")
    private VideojuegoEntity videojuego;

    @OneToMany(
            mappedBy = "inventarioItem",
            cascade = CascadeType.ALL
    )
    private List<DetalleAlquilerEntity> detalleAlquileres;

    @OneToMany(
            mappedBy = "inventarioItem",
            cascade = CascadeType.ALL
    )
    private List<ReservaEntity> listaDeEspera = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(
            name = "plataforma",
            nullable = false
    )
    @NotNull(message = "La plataforma no puede ser nula")
    private Plataformas plataforma;

    @Column(
            name = "precio_diario",
            nullable = false
    )
    @PositiveOrZero(message = "El precio diario no puede ser negativo")
    private BigDecimal precioDiario;

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

    public void aumentarStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a aumentar no puede ser negativa");
        }
        this.stockTotal += cantidad;
        this.stockDisponible += cantidad;
    }

    public void disminuirStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a disminuir no puede ser negativa");
        }
        if (cantidad > this.stockDisponible) {
            throw new IllegalArgumentException("El item: " + inventarioItemId + " no tiene suficiente stock disponible para disminuir. Copias disponibles: " + stockDisponible);
        }
        this.stockDisponible -= cantidad;
    }

    public boolean esStockValido() {
        return getStockDisponible() <= getStockTotal();
    }

    public void validarStock() {
        if (!esStockValido()) {
            throw new IllegalStateException("La suma de los stocks excede el stock total.");
        }
    }

    /// ---- Aggregate root ----

    public void agregarReserva(ReservaEntity reserva) {
        this.listaDeEspera.add(reserva);
        reserva.setInventarioItem(this);
    }

     public void eliminarReserva(ReservaEntity reserva) {
        this.listaDeEspera.remove(reserva);
        reserva.setInventarioItem(null);
    }

    public Optional<ReservaEntity> obtenerSiguienteReservaPendiente() {
        return this.listaDeEspera.stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.PENDIENTE)
                .min(Comparator.comparing(ReservaEntity::getFechaReserva));
    }

    public boolean tieneReservasPendientes() {
        return this.listaDeEspera.stream()
                .anyMatch(reserva -> reserva.getEstadoReserva() == EstadoReserva.PENDIENTE);
    }

    public void reservarCopiaPara(ReservaEntity reserva) {

        if (stockDisponible <= 0) {
            throw new IllegalStateException(
                    "No hay stock disponible."
            );
        }

        stockDisponible--;

        reserva.marcarComoNotificada();
    }

    public List<ReservaEntity> expirarReservasVencidas(LocalDateTime fechaActual) {

        List<ReservaEntity> expiradas = listaDeEspera.stream()
                .filter(r -> r.estaVencida(fechaActual))
                .toList();

        expiradas.forEach(r -> {

            r.setEstadoReserva(EstadoReserva.EXPIRADA);

            this.stockDisponible++;
        });

        return expiradas;
    }

}
