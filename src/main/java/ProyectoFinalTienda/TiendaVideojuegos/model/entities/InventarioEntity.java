package com.localvideojuegos.ProyectoFinal.model.entities;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class InventarioEntity {
    private int inventario_id;
    private int videojuego_id;
    private int stock_total;
    private int stock_disponible;
    private int stock_alquilado;
    private int stock_descartado;

}
