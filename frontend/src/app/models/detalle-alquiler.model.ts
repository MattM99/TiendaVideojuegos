import { InventarioItemModel } from "./inventario-item.model";

export interface DetalleAlquilerModel {
  id?: number;
  inventarioItem: InventarioItemModel; // <--- asociación
  unidades: number;
  subtotal: number;
}